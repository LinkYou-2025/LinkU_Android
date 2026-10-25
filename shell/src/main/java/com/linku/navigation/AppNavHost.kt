package com.linku.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import com.linku.AppViewModels
import com.linku.curation.navigation.curationGraph
import com.linku.file.deeplink.openDeepLinkGraph
import com.linku.file.navigation.fileGraph
import com.linku.home.navigation.homeGraph
import com.linku.link.navigation.linkGraph
import com.linku.login.navigation.LoginRoute
import com.linku.login.navigation.loginGraph
import com.linku.mypage.navigation.myPageGraph

/**
 * 앱 최상위 내비게이션 호스트에 각 모듈의 그래프를 연결합니다.
 *
 * 그래프는 각 기능 모듈이 소유하고, 이 호스트는 모듈 사이를 잇는 이동과 앱 전역 상태 변경만
 * 콜백으로 연결합니다.
 *
 * @param appState 내비게이션과 셸 UI 상태를 제공하는 [AppState]
 * @param viewModels 그래프들이 공유하는 [AppViewModels]
 * @param deepLinkHost https 앱 링크로 인식할 서버 호스트
 */
@Composable
internal fun AppNavHost(
    appState: AppState,
    viewModels: AppViewModels,
    deepLinkHost: String,
) {
    val context = LocalContext.current
    val navigator = appState.navigator

    val viewModel = viewModels.main
    val homeViewModel = viewModels.home
    val linkViewModel = viewModels.link
    val fileViewModel = viewModels.file
    val folderStateViewModel = viewModels.folderState
    val searchViewModel = viewModels.search
    val deepLinkViewModel = viewModels.deepLink

    // 닉네임 최상단 뒤치(사용하는 스크린)
    val nickname by viewModel.nickname.collectAsStateWithLifecycle()
    val isNicknameLoading by viewModel.isNicknameLoading.collectAsStateWithLifecycle()

    // 홈과 큐레이션이 같은 기본 닉네임 정책을 쓰도록 한 곳에서 만듭니다.
    val appNavigator = remember(appState, linkViewModel) {
        AppNavigatorImpl(appState, linkViewModel)
    }

    val displayNickname: () -> String = { nickname.orEmpty().ifBlank { "링큐" } }

    NavHost(
        navController = navigator,
        startDestination = AppRoute.SPLASH,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        splashGraph(
            navigator = navigator,
            viewModel = viewModel,
            homeViewModel = homeViewModel,
            setShowNavBar = { appState.showNavBar = it },
            onEdgeToEdgeChange = appState::updateSystemBars,
            navigateByNotification = appState::navigateToNotificationTarget,
        )

        loginGraph(
            route = LoginRoute.ROOT,
            onEnter = {
                // 로그인 화면은 별도 Activity가 그리므로, 돌아와서 전환하기 전까지 메인 쪽은
                // 스플래시와 같은 몰입 상태를 유지함.
                appState.showNavBar = false
                appState.updateSystemBars(true, true)
            },
            onLoginSuccess = { loginScope, reopenLogin ->
                handleLoginSuccess(
                    scope = loginScope,
                    context = context,
                    navigator = navigator,
                    viewModel = viewModel,
                    fileViewModel = fileViewModel,
                    folderStateViewModel = folderStateViewModel,
                    deepLinkViewModel = deepLinkViewModel,
                    setShowNavBar = { appState.showNavBar = it },
                    onEdgeToEdgeChange = appState::updateSystemBars,
                    navigateByNotification = appState::navigateToNotificationTarget,
                    reopenLogin = reopenLogin,
                )
            },
        )

        homeGraph(
            navigator = navigator,
            appNavigator = appNavigator,
            homeViewModel = homeViewModel,
            nickname = displayNickname,
            isNicknameLoading = { isNicknameLoading },
            onHomeEntered = viewModel::checkAndShowPushAlarmDialog,
            onSearchOpen = searchViewModel::openSearch,
            onDeleteLink = { userLinkuId, onSuccess, onFailed ->
                linkViewModel.deleteLink(
                    userLinkuId = userLinkuId,
                    onSucceed = onSuccess,
                    onFailed = onFailed,
                )
            },
        )

        fileGraph(
            appNavigator = appNavigator,
            fileViewModel = fileViewModel,
            folderStateViewModel = folderStateViewModel,
            onSearchOpen = searchViewModel::openSearch,
        )

        // 큐레이션 파트 리팩토링 적용
        curationGraph(
            navigator = navigator,
            appNavigator = appNavigator,
            // 그래프 빌더는 최초 1회만 실행되므로 값이 아닌 람다로 넘겨 매번 최신 nickname을 읽게 함.
            nickname = displayNickname,
        )

        myPageGraph(
            appNavigator = appNavigator,
            myPageViewModel = viewModels.myPage,
            onShowNavBarChange = { appState.showNavBar = it },
            // 로그아웃/탈퇴 버튼을 누른 "즉시"(API 응답 기다리지 않고) 시스템 바를
            // 몰입 모드로 전환함 — API 호출 및 Toast 표시 사이에 시스템 바가
            // 잠깐 보였다가 사라지는 깜빡임을 없애기 위함. 실패해서 MyPage에 남으면
            // MyPageApp이 false로 되돌림.
            onImmersiveTransitionChange = { appState.updateSystemBars(it, it) },
            onLogoutToLogin = {
                handleLogoutToLogin(
                    navigator = navigator,
                    viewModel = viewModel,
                    homeViewModel = homeViewModel,
                    searchViewModel = searchViewModel,
                    deepLinkViewModel = deepLinkViewModel,
                    folderStateViewModel = folderStateViewModel,
                    fileViewModel = fileViewModel,
                    setShowNavBar = { appState.showNavBar = it },
                    onEdgeToEdgeChange = appState::updateSystemBars,
                )
            },
            onDeleteLink = { userLinkuId, onSuccess, onFailed ->
                linkViewModel.deleteLink(
                    userLinkuId = userLinkuId,
                    onSucceed = {
                        // AI 목록에서 삭제한 링크가 홈의 최근 링크에도 남지 않도록
                        // 홈 캐시를 같은 식별자로 즉시 정리한 뒤 화면 성공 처리를 알립니다.
                        homeViewModel.onLinkDeleted(userLinkuId)
                        onSuccess()
                    },
                    onFailed = onFailed,
                )
            },
            onAlarmSettingBack = { navigator.popBackStack() },
        )

        linkGraph(
            navigator = navigator,
            linkViewModel = linkViewModel,
            selectedSharedLink = { folderStateViewModel.selectedSharedLink },
            onLinkUpdated = homeViewModel::loadRecentLinks,
            onLinkDeleted = homeViewModel::refreshHomeData,
        )

        openDeepLinkGraph(
            deepLinkHost = deepLinkHost,
            navigator = navigator,
            hasValidSession = viewModel::hasValidRefreshToken,
            fileViewModel = fileViewModel,
            folderStateViewModel = folderStateViewModel,
            deepLinkViewModel = deepLinkViewModel,
        )
    }
}
