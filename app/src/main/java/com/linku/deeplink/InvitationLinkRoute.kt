package com.linku.deeplink

import androidx.navigation.NavHostController
import com.linku.NavigationRoute
import com.linku.core.usecase.AcceptSharedFolderInvitationResult
import kotlinx.coroutines.CancellationException

/**
 * 공유 폴더 초대 딥링크를 로그인 상태와 초대 수락 결과에 따라 처리합니다.
 *
 * 토큰이 비어 있으면 잘못된 링크로 처리하고 기본 루트로 이동합니다. 로그아웃 상태에서는 초대
 * 토큰을 보류한 뒤 로그인 화면으로 이동합니다. 로그인 상태에서는 초대 수락을 기다린 다음 성공,
 * 인증 필요, 잘못된 초대, 네트워크 실패 및 기타 실패 결과에 맞는 상태 갱신과 화면 이동을 수행합니다.
 * 처리 도중 다른 딥링크 항목으로 이동한 경우에는 [deepLinkEntryId]를 비교해 오래된 결과를 반영하지
 * 않습니다.
 * 복구 가능한 [Exception]은 일반 실패 결과로 변환하지만 JVM의 치명적 [Error]는 호출자에게 전파합니다.
 *
 * @param token 처리할 공유 폴더 초대 토큰
 * @param isLoggedIn 현재 유효한 로그인 세션이 있는지 여부
 * @param onReceiveSharedFolderInvitation 초대 토큰을 수락하고 처리 결과를 반환하는 함수
 * @param onOpenAcceptedSharedFolder 수락된 폴더 결과를 파일 화면의 상세 상태에 반영하는 함수
 * @param onUpdateIsSharedFolders 파일 화면의 공유 폴더 모드 여부를 갱신하는 함수
 * @param onSetPendingInvitation 로그인 후 이어서 처리할 초대 토큰을 저장하거나 제거하는 함수
 * @param onInvalidLink 초대 토큰이 비어 있거나 유효하지 않을 때 호출하는 함수
 * @param onAuthenticationRequired 초대 수락에 인증이 필요할 때 호출하는 함수
 * @param onNetworkFailure 네트워크 문제로 초대 수락에 실패했을 때 호출하는 함수
 * @param onRefreshFailed 초대 수락 후 공유 폴더 목록 갱신에 실패했을 때 호출하는 함수
 * @param onFailure 분류되지 않은 오류로 초대 처리에 실패했을 때 호출하는 함수
 * @param navigator 처리 결과에 따라 루트 화면을 전환할 내비게이션 컨트롤러
 * @param deepLinkEntryId 현재 처리 중인 딥링크 백 스택 항목 ID
 * @throws CancellationException 초대 수락 작업이 취소된 경우 취소 상태를 호출자에게 전파합니다.
 */
internal suspend fun invitationLinkRoute(
    token: String,
    isLoggedIn: Boolean,
    onReceiveSharedFolderInvitation: suspend (String) -> AcceptSharedFolderInvitationResult,
    onOpenAcceptedSharedFolder: (AcceptSharedFolderInvitationResult.Accepted) -> Unit,
    onUpdateIsSharedFolders: (Boolean) -> Unit,
    onSetPendingInvitation: (String) -> Unit,
    onInvalidLink: () -> Unit = {},
    onAuthenticationRequired: () -> Unit = {},
    onNetworkFailure: () -> Unit = {},
    onRefreshFailed: () -> Unit = {},
    onFailure: () -> Unit = {},
    navigator: NavHostController,
    deepLinkEntryId: String,
) {
    /**
     * 비동기 처리 결과를 시작한 딥링크 백 스택 항목에 아직 반영할 수 있는지 확인합니다.
     *
     * @return 현재 백 스택 항목이 처리 시작 항목과 같으면 `true`
     */
    fun isHandlingDeepLink(): Boolean =
        navigator.currentBackStackEntry?.id == deepLinkEntryId

    /**
     * 기존 내비게이션 백 스택을 비우고 지정한 경로를 새 루트로 엽니다.
     *
     * @param route 새 루트로 이동할 목적지 경로
     */
    fun navigateAsRoot(route: String) {
        navigator.navigate(route) {
            popUpTo(navigator.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    /** 로그인 후 이어서 처리하도록 저장된 초대 토큰을 제거합니다. */
    fun clearPendingInvitation() {
        onSetPendingInvitation("")
    }

    /** 로그인 상태에 따라 홈 또는 로그인 화면을 기본 복귀 루트로 엽니다. */
    fun navigateToDefaultRoot() {
        navigateAsRoot(
            if (isLoggedIn) NavigationRoute.Home.route else NavigationRoute.Login.route
        )
    }

    /** 잘못된 초대 상태를 정리하고 오류를 알린 뒤 기본 루트로 복귀합니다. */
    fun handleInvalidLink() {
        clearPendingInvitation()
        onUpdateIsSharedFolders(false)
        onInvalidLink()
        navigateToDefaultRoot()
    }

    if (token.isBlank()) {
        if (isHandlingDeepLink()) {
            handleInvalidLink()
        }
        return
    }

    // 로그아웃 상태에서는 초대 처리를 미루고 로그인 성공 후 이어서 처리하도록 토큰을 보관합니다.
    if (!isLoggedIn) {
        if (isHandlingDeepLink()) {
            onUpdateIsSharedFolders(false)
            onSetPendingInvitation(token)
            navigateAsRoot(NavigationRoute.Login.route)
        }
        return
    }

    // 취소는 상위 coroutine에 전파하고 복구 가능한 예외만 도메인 실패 결과로 정규화합니다.
    val result = try {
        onReceiveSharedFolderInvitation(token)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AcceptSharedFolderInvitationResult.Failure(e)
    }

    // 네트워크 대기 중 다른 화면이나 딥링크로 이동했다면 오래된 결과를 반영하지 않습니다.
    if (!isHandlingDeepLink()) {
        return
    }

    when (result) {
        is AcceptSharedFolderInvitationResult.Accepted -> {
            clearPendingInvitation()
            onOpenAcceptedSharedFolder(result)
            navigateAsRoot(NavigationRoute.File.route)
        }

        is AcceptSharedFolderInvitationResult.AcceptedButRefreshFailed -> {
            // 초대 수락은 성공했으므로 공유 모드와 파일 화면 이동은 유지합니다.
            onUpdateIsSharedFolders(true)
            onRefreshFailed()
            navigateAsRoot(NavigationRoute.File.route)
        }

        is AcceptSharedFolderInvitationResult.AuthenticationRequired -> {
            // 재인증 후 같은 초대를 이어서 처리할 수 있도록 토큰을 다시 보관합니다.
            onUpdateIsSharedFolders(false)
            onSetPendingInvitation(token)
            onAuthenticationRequired()
            navigateAsRoot(NavigationRoute.Login.route)
        }

        is AcceptSharedFolderInvitationResult.InvalidInvitation -> {
            handleInvalidLink()
        }

        is AcceptSharedFolderInvitationResult.NetworkFailure -> {
            clearPendingInvitation()
            onUpdateIsSharedFolders(false)
            onNetworkFailure()
            navigateToDefaultRoot()
        }

        is AcceptSharedFolderInvitationResult.Failure -> {
            clearPendingInvitation()
            onUpdateIsSharedFolders(false)
            onFailure()
            navigateToDefaultRoot()
        }
    }
}
