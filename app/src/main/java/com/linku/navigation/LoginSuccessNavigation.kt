package com.linku.navigation

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.linku.MainViewModel
import com.linku.NavigationRoute
import com.linku.R
import com.linku.core.model.alarm.AlarmType
import com.linku.core.usecase.AcceptSharedFolderInvitationResult
import com.linku.deeplink.DeepLinkHandlerViewModel
import com.linku.deeplink.showAcceptedSharedFolder
import com.linku.file.FileViewModel
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 로그인 성공 이후의 화면 전환을 처리합니다.
 *
 * 보류된 초대 토큰을 먼저 처리하고, 없으면 공유 폴더 ID를 처리합니다. 둘 다 없으면 보류된 푸시 알림
 * 대상으로, 그것도 없으면 홈 화면을 새 루트로 엽니다. 로그인 모듈은 다른 모듈의 상태를 알 수 없으므로
 * 앱 모듈이 이 흐름을 이어받습니다.
 *
 * @param scope 비동기 초대 수락과 이동을 실행할 로그인 목적지의 [CoroutineScope]
 * @param context 토스트를 표시할 [Context]
 * @param navigator 화면 전환에 사용할 내비게이션 컨트롤러
 * @param viewModel 인증 상태와 보류된 알림을 제공하는 [MainViewModel]
 * @param fileViewModel 공유 폴더 초대 수락을 처리하는 [FileViewModel]
 * @param folderStateViewModel 파일 화면의 폴더 상태를 관리하는 [FolderStateViewModel]
 * @param deepLinkViewModel 보류된 딥링크를 관리하는 [DeepLinkHandlerViewModel]
 * @param setShowNavBar 하단 내비게이션 바 표시 여부를 갱신하는 함수
 * @param onEdgeToEdgeChange 상태바와 내비게이션 바 숨김 여부를 갱신하는 함수
 * @param navigateByNotification 보류된 푸시 알림 대상으로 이동하는 함수
 */
internal fun handleLoginSuccess(
    scope: CoroutineScope,
    context: Context,
    navigator: NavHostController,
    viewModel: MainViewModel,
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    deepLinkViewModel: DeepLinkHandlerViewModel,
    setShowNavBar: (Boolean) -> Unit,
    onEdgeToEdgeChange: (hideStatusBar: Boolean, hideNavigationBar: Boolean) -> Unit,
    navigateByNotification: (type: AlarmType, targetId: Long) -> Unit,
) {
    /**
     * 비동기 로그인 결과가 도착한 시점에도 로그인 화면에 있을 때만 대상 화면으로 이동합니다.
     *
     * @param route 로그인 화면에서 전환할 목적지 경로
     */
    fun navigateFromLoginTo(route: String) {
        if (navigator.currentDestination?.route == NavigationRoute.Login.route) {
            setShowNavBar(route != NavigationRoute.Login.route)
            navigator.navigate(route) {
                popUpTo(NavigationRoute.Login.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    /**
     * 로그인 화면에서 초대 수락 결과에 맞는 공유 폴더 화면을 새 루트로 엽니다.
     *
     * @param acceptedResult 상세로 열 초대 수락 결과. 목록 갱신에 실패한 부분 성공이면
     * `null`을 전달해 공유 폴더 그룹 화면으로 대체합니다.
     */
    fun openSharedFoldersFromLogin(
        acceptedResult: AcceptSharedFolderInvitationResult.Accepted? = null,
    ) {
        if (navigator.currentDestination?.route == NavigationRoute.Login.route) {
            setShowNavBar(true)
            if (acceptedResult == null) {
                folderStateViewModel.resetSharedFolderState()
                folderStateViewModel.showSharedFolderGroups()
            } else {
                folderStateViewModel.showAcceptedSharedFolder(acceptedResult)
            }

            navigator.navigate(NavigationRoute.File.route) {
                popUpTo(NavigationRoute.Login.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    /**
     * 보류 중인 초대 처리 실패를 알리고 일반 폴더 상태로 홈 화면을 새 루트로 엽니다.
     *
     * @param messageResId 실패 원인을 안내할 문자열 리소스 ID
     */
    fun handlePendingInvitationFailure(messageResId: Int) {
        if (navigator.currentDestination?.route == NavigationRoute.Login.route) {
            setShowNavBar(true)
            folderStateViewModel.resetSharedFolderState()
            folderStateViewModel.updateIsSharedFolders(false)
            Toast.makeText(
                context,
                messageResId,
                Toast.LENGTH_SHORT,
            ).show()

            navigator.navigate(NavigationRoute.Home.route) {
                popUpTo(NavigationRoute.Login.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    setShowNavBar(true)
    viewModel.setAuthenticated(true)
    onEdgeToEdgeChange(false, false)

    // 보류된 초대 토큰을 먼저 처리하고, 없으면 공유 폴더 ID를 처리합니다.
    // 둘 다 없을 때만 정상 로그인 경로로 홈 화면을 엽니다.
    val pendingInvitationToken = deepLinkViewModel.consumePendingInvitation()

    if (pendingInvitationToken.isNotBlank()) {
        scope.launch {
            when (val result = fileViewModel.receiveSharedFolderInvitation(
                pendingInvitationToken
            )) {
                is AcceptSharedFolderInvitationResult.Accepted -> {
                    openSharedFoldersFromLogin(result)
                }

                is AcceptSharedFolderInvitationResult.AcceptedButRefreshFailed -> {
                    // 초대 수락은 완료되었으므로 갱신 실패를 알리고 공유 폴더를 엽니다.
                    Toast.makeText(
                        context,
                        R.string.share_folder_refresh_failed,
                        Toast.LENGTH_SHORT,
                    ).show()
                    openSharedFoldersFromLogin()
                }

                is AcceptSharedFolderInvitationResult.AuthenticationRequired -> {
                    // 소비한 토큰을 복원해 다음 로그인 성공 후 초대 수락을 재시도합니다.
                    deepLinkViewModel.setPendingInvitation(pendingInvitationToken)
                    Toast.makeText(
                        context,
                        R.string.authentication_required,
                        Toast.LENGTH_SHORT,
                    ).show()
                    navigateFromLoginTo(NavigationRoute.Login.route)
                }

                is AcceptSharedFolderInvitationResult.InvalidInvitation -> {
                    handlePendingInvitationFailure(R.string.invalid_share_link)
                }

                is AcceptSharedFolderInvitationResult.NetworkFailure -> {
                    handlePendingInvitationFailure(R.string.network_error)
                }

                is AcceptSharedFolderInvitationResult.Failure -> {
                    handlePendingInvitationFailure(R.string.undefined_behavior)
                }
            }
        }
        return
    }

    deepLinkViewModel.consumePendingShare()?.let { folderId ->
        setShowNavBar(true)
        fileViewModel.receiveSharedFolder(folderId)
        folderStateViewModel.resetSharedFolderState()
        folderStateViewModel.updateIsSharedFolders(true)

        navigator.navigate(NavigationRoute.File.route) {
            popUpTo(NavigationRoute.Login.route) { inclusive = true }
            launchSingleTop = true
        }
        return
    }

    // 수동 로그인 성공 후 pending 알림 처리
    viewModel.consumePendingNotification()?.let {
        scope.launch {
            // 캐시된 닉네임을 먼저 반영한 뒤 홈으로 이동해야 "링큐" 기본값이
            // 잠깐 보였다가 실제 닉네임으로 바뀌는 깜빡임이 없음.
            viewModel.awaitCachedNickname()
            navigator.navigate(NavigationRoute.Home.route) {
                popUpTo(NavigationRoute.Login.route) { inclusive = true }
                launchSingleTop = true
            }
            navigateByNotification(it.type, it.targetId)
        }
        return
    }

    // pending 알림이 없는 경우의 기본 동작
    setShowNavBar(true)
    scope.launch {
        // 캐시된 닉네임을 먼저 반영한 뒤 홈으로 이동해야 "링큐" 기본값이
        // 잠깐 보였다가 실제 닉네임으로 바뀌는 깜빡임이 없음.
        viewModel.awaitCachedNickname()
        navigator.navigate(NavigationRoute.Home.route) {
            popUpTo(NavigationRoute.Login.route) { inclusive = true }
            launchSingleTop = true
        }
    }
}
