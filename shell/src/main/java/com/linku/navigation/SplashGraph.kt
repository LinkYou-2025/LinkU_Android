package com.linku.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.linku.MainViewModel
import com.linku.splash.Splash
import com.linku.core.model.alarm.AlarmType
import com.linku.home.HomeViewModel
import kotlinx.coroutines.launch
import com.linku.core.navigation.LinkuRoute
import com.linku.login.navigation.LoginRoute

/**
 * 스플래시 화면과 자동 로그인 결과에 따른 첫 화면 전환을 내비게이션 그래프에 등록합니다.
 *
 * 자동 로그인 결과 판단은 스플래시 모듈이 하고, 이 그래프는 결과에 따른 앱 전역 상태 변경과 이동만 담당합니다.
 *
 * @param navigator 화면 전환에 사용할 내비게이션 컨트롤러
 * @param viewModel 인증 상태와 보류된 알림을 제공하는 [MainViewModel]
 * @param homeViewModel 자동 로그인 성공 후 홈 데이터를 갱신할 [HomeViewModel]
 * @param setShowNavBar 하단 내비게이션 바 표시 여부를 갱신하는 함수
 * @param onEdgeToEdgeChange 상태바와 내비게이션 바 숨김 여부를 갱신하는 함수
 * @param navigateByNotification 보류된 푸시 알림 대상으로 이동하는 함수
 */
internal fun NavGraphBuilder.splashGraph(
    navigator: NavHostController,
    viewModel: MainViewModel,
    homeViewModel: HomeViewModel,
    setShowNavBar: (Boolean) -> Unit,
    onEdgeToEdgeChange: (hideStatusBar: Boolean, hideNavigationBar: Boolean) -> Unit,
    navigateByNotification: (type: AlarmType, targetId: Long) -> Unit,
) {
    composable(AppRoute.SPLASH) {
        LaunchedEffect(Unit) {
            setShowNavBar(false)
            onEdgeToEdgeChange(true, true) // 스플래시: 상태/내비게이션 바 완전히 숨김
        }

        val splashScope = rememberCoroutineScope()

        Splash(
            onNavigateToHome = {
                splashScope.launch {
                    setShowNavBar(true)
                    viewModel.setAuthenticated(true)
                    // 캐시된 닉네임을 먼저 반영한 뒤 홈으로 이동해야 "링큐" 기본값이
                    // 잠깐 보였다가 실제 닉네임으로 바뀌는 깜빡임이 없음.
                    viewModel.awaitCachedNickname()
                    onEdgeToEdgeChange(false, false)
                    homeViewModel.refreshAfterLogin()
                    navigator.navigate(LinkuRoute.HOME) {
                        popUpTo(AppRoute.SPLASH) { inclusive = true }
                        launchSingleTop = true
                    }
                    // 자동 로그인 성공 후 pending 알림 처리
                    viewModel.consumePendingNotification()?.let {
                        navigateByNotification(it.type, it.targetId)
                    }
                }
            },
            onNavigateToLogin = {
                navigator.navigate(LoginRoute.ROOT) {
                    popUpTo(AppRoute.SPLASH) { inclusive = true }
                }
            },
        )
    }
}
