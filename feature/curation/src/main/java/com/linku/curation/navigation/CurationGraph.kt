package com.linku.curation.navigation

import android.net.Uri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
    showNavBar: (Boolean) -> Unit,
    nickname: String,
    onNavigateToSaveLink: () -> Unit = {},
) {
    val goHome: () -> Unit = {
        navigator.navigate("home") {
            popUpTo(navigator.graph.findStartDestination().id) {
                saveState = true
                inclusive = false
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    // 큐레이션 탭 루트 라우트는 다른 탭(home/file/my_page)과 동일하게 "curation" 그대로 사용하고,
    // 하위 화면은 "curation/..." 접두사를 붙여 MainApp의 isTabRoute("curation" 기준)와
    // DoubleBackToExitIfTop의 탭 판정 로직에 별도 처리 없이 그대로 매칭되도록 한다.
    composable("curation") {
        showNavBar(true)
        // 상태바/내비게이션 바는 MainScreen(app 모듈)에서 공통으로 흰색 처리함.
        val viewModel = hiltViewModel<CurationViewModel>()

        CurationScreen(
            nickname = nickname,
            viewModel = viewModel,
            onMonthlyDetailClick = { month, curationId ->
                navigator.navigate("curation/detail/$month/$curationId")
            },
            onKeywordDetailClick = { month -> navigator.navigate("curation/card2/$month") },
            onRemindClick = { navigator.navigate("curation/card3") },
            onCurationHistoryClick = { year -> navigator.navigate("curation/monthly/$year") }
        )
    }

    composable(
        route = "curation/detail/{month}/{curationId}",
        arguments = listOf(
            navArgument("month") { type = NavType.StringType },
            navArgument("curationId") { type = NavType.LongType }
        )
    ) {
        showNavBar(false)
        val viewModel = hiltViewModel<CurationDetailViewModel>()

        CurationMonthlyDetailScreen(
            viewModel = viewModel,
            onBack = { navigator.popBackStack() },
            onGoHome = goHome,
            onNavigateToLinkDetail = { linkId -> navigator.navigate("savelinkresult/$linkId") }
        )
    }

    composable(
        route = "curation/card2/{month}",
        arguments = listOf(navArgument("month") { type = NavType.StringType })
    ) { backStackEntry ->
        showNavBar(false)
        val month = backStackEntry.arguments?.getString("month").orEmpty()
        val viewModel = hiltViewModel<CurationKeywordViewModel>()

        CurationKeywordDetailScreen(
            nickname = nickname,
            month = month,
            viewModel = viewModel,
            onBack = { navigator.popBackStack() },
            onNavigateToSaveLink = onNavigateToSaveLink,
            onKeywordClick = { keyword ->
                navigator.navigate("curation/keyword_links/${Uri.encode(keyword)}")
            }
        )
    }

    composable(
        route = "curation/keyword_links/{keyword}",
        arguments = listOf(navArgument("keyword") { type = NavType.StringType })
    ) { backStackEntry ->
        showNavBar(false)
        val keyword = backStackEntry.arguments?.getString("keyword")
            ?.let { Uri.decode(it) }
            .orEmpty()
        val viewModel = hiltViewModel<CurationKeywordLinksViewModel>()

        CurationKeywordLinksScreen(
            keyword = keyword,
            nickname = nickname,
            viewModel = viewModel,
            onBack = { navigator.popBackStack() },
            onNavigateToLinkDetail = { linkId -> navigator.navigate("savelinkresult/$linkId") }
        )
    }

    composable("curation/card3") {
        showNavBar(false)
        val viewModel = hiltViewModel<CurationRemindViewModel>()

        CurationRemindScreen(
            viewModel = viewModel,
            onBack = { navigator.popBackStack() },
            onNavigateToLinkDetail = { linkId -> navigator.navigate("savelinkresult/$linkId") }
        )
    }

    composable(
        route = "curation/monthly/{year}",
        arguments = listOf(navArgument("year") { type = NavType.IntType })
    ) { backStackEntry ->
        showNavBar(false)
        val year = backStackEntry.arguments?.getInt("year") ?: 0
        val viewModel = hiltViewModel<CurationHistoryViewModel>()

        MonthlyCurationScreen(
            year = year,
            viewModel = viewModel,
            onBackClick = { navigator.popBackStack() },
            onMonthClick = { month, curationId ->
                navigator.navigate("curation/detail/$month/$curationId")
            }
        )
    }
}
