package com.linku.home.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.linku.core.navigation.AppNavigator
import com.linku.core.navigation.LinkuRoute
import com.linku.home.HomeApp
import com.linku.home.HomeViewModel
import com.linku.home.screen.AlarmScreen
import com.linku.home.screen.NoticeScreen

/**
 * 홈 탭과 알림 목록, 공지 상세 화면을 내비게이션 그래프에 등록합니다.
 *
 * 다른 모듈의 화면으로 이동하는 동작과 앱 전역 UI 상태는 호출한 쪽이 넘겨주는 콜백으로 이어받습니다.
 * 그래프 빌더는 최초 1회만 실행되므로 변하는 값은 값이 아닌 람다로 받아 각 화면이 그릴 때마다 최신 값을
 * 읽게 합니다.
 *
 * @param navigator 홈 모듈 안의 화면 전환에 사용할 내비게이션 컨트롤러
 * @param appNavigator 다른 모듈의 화면으로 이동할 때 사용하는 [AppNavigator]
 * @param homeViewModel 홈 화면 상태를 제공하는 [HomeViewModel]
 * @param nickname 홈 화면에 표시할 닉네임을 읽는 함수
 * @param isNicknameLoading 닉네임을 아직 불러오는 중인지 읽는 함수
 * @param onHomeEntered 홈 화면에 진입할 때 호출하는 함수
 * @param onSearchOpen 검색 화면을 여는 함수
 * @param onDeleteLink 링크를 삭제하는 함수
 */
fun NavGraphBuilder.homeGraph(
    navigator: NavHostController,
    appNavigator: AppNavigator,
    homeViewModel: HomeViewModel,
    nickname: () -> String,
    isNicknameLoading: () -> Boolean,
    onHomeEntered: () -> Unit,
    onSearchOpen: () -> Unit,
    onDeleteLink: (
        userLinkuId: Long,
        onSuccess: () -> Unit,
        onFailed: (Throwable) -> Unit,
    ) -> Unit,
) {
    composable(LinkuRoute.HOME) {
        LaunchedEffect(Unit) { onHomeEntered() }

        HomeApp(
            viewModel = homeViewModel,
            onSearchOpen = onSearchOpen,
            nickname = nickname(),
            isNicknameLoading = isNicknameLoading(),
            onNavigateToSetting = {
                navigator.navigate(LinkuRoute.ALARM_SETTING)
            },
            onNavigateToSaveLink = appNavigator::navigateToSaveLinkWithUrl,
            onNavigateToLinkDetail = appNavigator::navigateToLinkDetail,
            onDeleteLink = onDeleteLink,
            onNavigateToAlarm = {
                navigator.navigate(LinkuRoute.ALARM)
            }
        )
    }

    composable(LinkuRoute.ALARM) {
        AlarmScreen(
            onBack = { navigator.popBackStack() },
            onNavigateToSetting = { navigator.navigate(LinkuRoute.ALARM_SETTING) },
            onNavigateToHome = {
                navigator.navigate(LinkuRoute.HOME) {
                    popUpTo(LinkuRoute.HOME) { inclusive = false }
                }
            },
            onNavigateToLinkDetail = appNavigator::navigateToLinkDetail,
            onNavigateToFolder = { /* TODO */ },
            onNavigateToCuration = appNavigator::navigateToCurationFromAlarm,
            onNavigateToNotice = { targetId ->
                navigator.navigate(LinkuRoute.Notice.create(targetId))
            }
        )
    }

    composable(
        route = LinkuRoute.Notice.PATTERN,
        arguments = listOf(navArgument(LinkuRoute.Notice.ARG_TARGET_ID) { type = NavType.LongType })
    ) {
        NoticeScreen(
            onBack = { navigator.popBackStack() }
        )
    }
}
