package com.linku.curation.navigation

import android.net.Uri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.linku.curation.ui.screen.CurationKeywordDetailScreen
import com.linku.curation.ui.screen.CurationKeywordLinksScreen
import com.linku.curation.ui.screen.CurationMonthlyDetailScreen
import com.linku.curation.ui.screen.CurationRemindScreen
import com.linku.curation.ui.screen.CurationScreen
import com.linku.curation.ui.screen.MonthlyCurationScreen
import com.linku.curation.viewModel.CurationDetailViewModel
import com.linku.curation.viewModel.CurationHistoryViewModel
import com.linku.curation.viewModel.CurationKeywordViewModel
import com.linku.curation.viewModel.CurationRemindViewModel
import com.linku.curation.viewModel.CurationViewModel

fun NavGraphBuilder.curationGraph(
    navigator: NavHostController,
    showNavBar: (Boolean) -> Unit,
    nickname: String,
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

    navigation(
        startDestination = "curation_list",
        route = "curation"
    ) {
        composable("curation_list") {
            showNavBar(true)
            // 상태바/내비게이션 바는 MainScreen(app 모듈)에서 공통으로 흰색 처리함.
            val viewModel = hiltViewModel<CurationViewModel>()

            CurationScreen(
                nickname = nickname,
                viewModel = viewModel,
                onMonthlyDetailClick = { month, curationId ->
                    navigator.navigate("curation_detail/$month/$curationId")
                },
                onKeywordDetailClick = { month -> navigator.navigate("curation_card2/$month") },
                onRemindClick = { navigator.navigate("curation_card3") },
                onCurationHistoryClick = { year -> navigator.navigate("curation_monthly/$year") }
            )
        }

        composable(
            route = "curation_detail/{month}/{curationId}",
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
            route = "curation_card2/{month}",
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
                onGoHome = goHome,
                onKeywordClick = { keyword ->
                    navigator.navigate("curation_keyword_links/${Uri.encode(keyword)}")
                }
            )
        }

        composable(
            route = "curation_keyword_links/{keyword}",
            arguments = listOf(navArgument("keyword") { type = NavType.StringType })
        ) { backStackEntry ->
            showNavBar(false)
            val keyword = backStackEntry.arguments?.getString("keyword")
                ?.let { Uri.decode(it) }
                .orEmpty()

            CurationKeywordLinksScreen(
                keyword = keyword,
                nickname = nickname,
                onBack = { navigator.popBackStack() }
            )
        }

        composable("curation_card3") {
            showNavBar(false)
            val viewModel = hiltViewModel<CurationRemindViewModel>()

            CurationRemindScreen(
                viewModel = viewModel,
                onBack = { navigator.popBackStack() },
                onNavigateToLinkDetail = { linkId -> navigator.navigate("savelinkresult/$linkId") }
            )
        }

        composable(
            route = "curation_monthly/{year}",
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
                    navigator.navigate("curation_detail/$month/$curationId")
                }
            )
        }
    }
}
