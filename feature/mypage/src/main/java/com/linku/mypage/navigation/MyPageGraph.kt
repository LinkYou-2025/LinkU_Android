package com.linku.mypage.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.linku.core.navigation.AppNavigator
import com.linku.core.navigation.LinkuRoute
import com.linku.mypage.MyPageApp
import com.linku.mypage.MyPageViewModel
import com.linku.mypage.NotificationViewModel
import com.linku.mypage.screen.AlarmSettingScreen

/**
 * 마이페이지 탭과 알림 설정 화면을 내비게이션 그래프에 등록합니다.
 *
 * 다른 모듈의 화면으로 이동하는 동작과 앱 전역 UI 상태는 호출한 쪽이 넘겨주는 콜백으로 이어받습니다.
 *
 * @param appNavigator 다른 모듈의 화면으로 이동할 때 사용하는 [AppNavigator]
 * @param myPageViewModel 마이페이지 상태를 제공하는 [MyPageViewModel]
 * @param onShowNavBarChange 마이페이지 내부 화면에 따라 하단 내비게이션 바 표시 여부를 갱신하는 함수
 * @param onImmersiveTransitionChange 로그아웃, 탈퇴 버튼을 누른 즉시 시스템 바를 몰입 모드로 전환하거나
 * 되돌리는 함수
 * @param onLogoutToLogin 로그아웃이나 탈퇴가 끝난 뒤 로그인 화면으로 돌아가는 함수
 * @param onDeleteLink 링크를 삭제하는 함수
 * @param onAlarmSettingBack 알림 설정 화면에서 뒤로 가는 함수
 */
fun NavGraphBuilder.myPageGraph(
    appNavigator: AppNavigator,
    myPageViewModel: MyPageViewModel,
    onShowNavBarChange: (Boolean) -> Unit,
    onImmersiveTransitionChange: (Boolean) -> Unit,
    onLogoutToLogin: () -> Unit,
    onDeleteLink: (
        userLinkuId: Long,
        onSuccess: () -> Unit,
        onFailed: (Throwable) -> Unit,
    ) -> Unit,
    onAlarmSettingBack: () -> Unit,
) {
    composable(LinkuRoute.MY_PAGE) {
        LaunchedEffect(Unit) {
            myPageViewModel.loadUserInfo()
        }

        MyPageApp(
            viewModel = myPageViewModel,
            // MyPageScreen(마이페이지 메인 화면)일 때만 하단 네비게이션 바를 표시.
            // 계정설정/탈퇴/FAQ 등 마이페이지 내부 하위 화면에서는 숨김.
            onShowNavBarChange = onShowNavBarChange,
            onImmersiveTransitionChange = onImmersiveTransitionChange,
            onLogoutToLogin = onLogoutToLogin,
            onNavigateToAlarm = appNavigator::navigateToAlarm,
            onNavigateToLinkDetail = appNavigator::navigateToLinkDetail,
            onDeleteLink = onDeleteLink,
        )
    }

    composable(LinkuRoute.ALARM_SETTING) {
        val notificationViewModel: NotificationViewModel = hiltViewModel()

        AlarmSettingScreen(
            onBackClick = onAlarmSettingBack,
            viewModel = notificationViewModel
        )
    }
}
