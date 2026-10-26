package com.linku.file.deeplink

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.linku.file.R
import com.linku.core.error.DeepLinkError
import com.linku.file.FileViewModel
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import kotlin.coroutines.cancellation.CancellationException

/**
 * 공유 폴더 초대 딥링크(`open?token=`)를 내비게이션 그래프에 등록합니다.
 *
 * 백 스택 항목마다 한 번 초대를 처리하며, 로그인 상태와 초대 수락 결과에 따른 화면 이동은
 * [invitationLinkRoute]가 담당하고 이 그래프는 결과에 맞는 안내 토스트만 연결합니다.
 *
 * @param deepLinkHost https 앱 링크로 인식할 서버 호스트
 * @param navigator 처리 결과에 따라 루트 화면을 전환할 내비게이션 컨트롤러
 * @param hasValidSession 유효한 로그인 세션이 있는지 확인하는 함수
 * @param fileViewModel 초대 수락을 처리하는 [FileViewModel]
 * @param folderStateViewModel 파일 화면의 폴더 상태를 관리하는 [FolderStateViewModel]
 * @param deepLinkViewModel 로그인 후 이어서 처리할 초대를 보관하는 [DeepLinkHandlerViewModel]
 */
fun NavGraphBuilder.openDeepLinkGraph(
    deepLinkHost: String,
    navigator: NavHostController,
    hasValidSession: suspend () -> Boolean,
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    deepLinkViewModel: DeepLinkHandlerViewModel,
) {
    composable(
        route = OPEN_DEEP_LINK_ROUTE,
        arguments = listOf(
            openDeepLinkTokenArgument(),
        ),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = openDeepLinkUriPattern(deepLinkHost)
            },
            navDeepLink {
                uriPattern = CUSTOM_SCHEME_OPEN_DEEP_LINK_URI_PATTERN
            },
        )
    ) { backStackEntry ->
        val context = LocalContext.current

        /**
         * 초대 처리 결과를 짧은 토스트로 안내합니다.
         *
         * @param messageResId 안내할 문자열 리소스 ID
         */
        fun showToast(@StringRes messageResId: Int) {
            Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show()
        }

        // 백 스택 항목마다 한 번 처리하고 같은 ID를 비동기 결과의 유효성 확인에 사용합니다.
        LaunchedEffect(backStackEntry.id) {

            try {
                // non-null 인자의 빈 기본값은 파서에서 구체적인 딥링크 오류로 변환합니다.
                val token = parseOpenDeepLinkToken(
                    backStackEntry.arguments?.getString(
                        OPEN_DEEP_LINK_TOKEN_ARGUMENT
                    ).orEmpty()
                )

                invitationLinkRoute(
                    token = token,
                    isLoggedIn = hasValidSession(),
                    onReceiveSharedFolderInvitation = fileViewModel::receiveSharedFolderInvitation,
                    onOpenAcceptedSharedFolder = { acceptedResult ->
                        folderStateViewModel.showAcceptedSharedFolder(acceptedResult)
                    },
                    onUpdateIsSharedFolders = { isSharedFolders ->
                        folderStateViewModel.resetSharedFolderState()
                        folderStateViewModel.updateIsSharedFolders(
                            isSharedFolders
                        )
                    },
                    onSetPendingInvitation = deepLinkViewModel::setPendingInvitation,
                    onInvalidLink = { showToast(R.string.invalid_share_link) },
                    onAuthenticationRequired = { showToast(R.string.authentication_required) },
                    onNetworkFailure = { showToast(R.string.network_error) },
                    onRefreshFailed = { showToast(R.string.share_folder_refresh_failed) },
                    onFailure = { showToast(R.string.undefined_behavior) },
                    navigator = navigator,
                    deepLinkEntryId = backStackEntry.id,
                )

            } catch (e: CancellationException) {
                // Compose effect 취소를 일반 오류로 변환하지 않고 구조화된 취소를 전파합니다.
                throw e

            } catch (e: DeepLinkError.MissingInvitationToken) {
                // 토큰 누락은 현재 화면과 초대 상태를 바꾸지 않고 안내만 표시합니다.
                showToast(R.string.invalid_share_link)

            } catch (e: IllegalArgumentException) {
                // 추후 공통 토스트 메시지로 변경
                showToast(R.string.undefined_behavior)

            } catch (e: Exception) {
                // 추후 공통 토스트 메시지로 변경
                showToast(R.string.undefined_behavior)
            }
        }
    }
}
