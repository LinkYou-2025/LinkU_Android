package com.linku.curation.navigation

import android.net.Uri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.linku.core.navigation.AppNavigator
import com.linku.core.navigation.LinkuRoute
import com.linku.curation.ui.screen.CurationKeywordDetailScreen
import com.linku.curation.ui.screen.CurationKeywordLinksScreen
import com.linku.curation.ui.screen.CurationMonthlyDetailScreen
import com.linku.curation.ui.screen.CurationRemindScreen
import com.linku.curation.ui.screen.CurationScreen
import com.linku.curation.ui.screen.MonthlyCurationScreen
import com.linku.curation.viewModel.CurationDetailViewModel
import com.linku.curation.viewModel.CurationHistoryViewModel
import com.linku.curation.viewModel.CurationKeywordLinksViewModel
import com.linku.curation.viewModel.CurationKeywordViewModel
import com.linku.curation.viewModel.CurationRemindViewModel
import com.linku.curation.viewModel.CurationViewModel

fun NavGraphBuilder.curationGraph(
    navigator: NavHostController,
    appNavigator: AppNavigator,
    // NavHost의 그래프 빌더는 최초 1회만 실행되므로, 값(String)을 그대로 받으면 그 시점의
    // 스냅샷에 고정된다. 각 composable 진입 시점(리컴포지션 시점)에 최신 값을 읽도록 람다로 받는다.
    nickname: () -> String,
) {
    val goHome: () -> Unit = {
        navigator.navigate(LinkuRoute.HOME) {
            popUpTo(navigator.graph.findStartDestination().id) {
                saveState = true
                inclusive = false
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    // 큐레이션 탭 루트 라우트는 다른 탭(home/file/mypage)과 동일하게 "curation" 그대로 사용하고,
    // 하위 화면은 "curation/..." 접두사를 붙여 MainApp의 isTabRoute("curation" 기준)와
    // DoubleBackToExitIfTop의 탭 판정 로직에 별도 처리 없이 그대로 매칭되도록 한다.
    composable(CurationRoute.ROOT) {
        // 상태바/내비게이션 바는 MainScreen(app 모듈)에서 공통으로 흰색 처리함.
        val viewModel = hiltViewModel<CurationViewModel>()

        CurationScreen(
            nickname = nickname(),
            viewModel = viewModel,
            onMonthlyDetailClick = { month, curationId ->
                navigator.navigate(CurationRoute.detail(month, curationId))
            },
            onKeywordDetailClick = { month ->
                navigator.navigate(CurationRoute.keywordDetail(month))
            },
            onRemindClick = { navigator.navigate(CurationRoute.REMIND) },
            onCurationHistoryClick = { year ->
                navigator.navigate(CurationRoute.monthly(year))
            }
        )
    }

    composable(
        route = CurationRoute.DETAIL_PATTERN,
        arguments = listOf(
            navArgument(CurationRoute.ARG_MONTH) { type = NavType.StringType },
            navArgument(CurationRoute.ARG_CURATION_ID) { type = NavType.LongType }
        )
    ) {
        val viewModel = hiltViewModel<CurationDetailViewModel>()

        CurationMonthlyDetailScreen(
            viewModel = viewModel,
            onBack = { navigator.popBackStack() },
            onGoHome = goHome,
            onNavigateToLinkDetail = appNavigator::navigateToLinkDetail,
        )
    }

    composable(
        route = CurationRoute.KEYWORD_DETAIL_PATTERN,
        arguments = listOf(navArgument(CurationRoute.ARG_MONTH) { type = NavType.StringType })
    ) { backStackEntry ->
        val month = backStackEntry.arguments?.getString(CurationRoute.ARG_MONTH).orEmpty()
        val viewModel = hiltViewModel<CurationKeywordViewModel>()

        CurationKeywordDetailScreen(
            nickname = nickname(),
            month = month,
            viewModel = viewModel,
            onBack = { navigator.popBackStack() },
            onNavigateToSaveLink = appNavigator::navigateToSaveLink,
            onKeywordClick = { keyword ->
                navigator.navigate(CurationRoute.keywordLinks(Uri.encode(keyword)))
            }
        )
    }

    composable(
        route = CurationRoute.KEYWORD_LINKS_PATTERN,
        arguments = listOf(navArgument(CurationRoute.ARG_KEYWORD) { type = NavType.StringType })
    ) { backStackEntry ->
        val keyword = backStackEntry.arguments?.getString(CurationRoute.ARG_KEYWORD).orEmpty()
        val viewModel = hiltViewModel<CurationKeywordLinksViewModel>()

        CurationKeywordLinksScreen(
            keyword = keyword,
            viewModel = viewModel,
            onBack = { navigator.popBackStack() },
            // 링크 상세 라우트는 링크 영역이 소유하므로 앱이 넘겨준 이동 콜백을 사용함.
            onNavigateToLinkDetail = appNavigator::navigateToLinkDetail
        )
    }

    composable(CurationRoute.REMIND) {
        val viewModel = hiltViewModel<CurationRemindViewModel>()

        CurationRemindScreen(
            viewModel = viewModel,
            onBack = { navigator.popBackStack() },
            onNavigateToLinkDetail = appNavigator::navigateToLinkDetail,
        )
    }

    composable(
        route = CurationRoute.MONTHLY_PATTERN,
        arguments = listOf(navArgument(CurationRoute.ARG_YEAR) { type = NavType.IntType })
    ) { backStackEntry ->
        val year = backStackEntry.arguments?.getInt(CurationRoute.ARG_YEAR) ?: 0
        val viewModel = hiltViewModel<CurationHistoryViewModel>()

        MonthlyCurationScreen(
            year = year,
            viewModel = viewModel,
            onBackClick = { navigator.popBackStack() },
            onMonthClick = { month, curationId ->
                navigator.navigate(CurationRoute.detail(month, curationId))
            }
        )
    }
}
