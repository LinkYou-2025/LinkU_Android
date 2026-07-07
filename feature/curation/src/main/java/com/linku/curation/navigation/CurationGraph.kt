package com.linku.curation.navigation

import android.net.Uri
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.linku.curation.ui.CurationScreen
import com.linku.curation.ui.monthly.MonthlyCurationScreen
import com.linku.curation.ui.screen.CurationKeywordDetailScreen
import com.linku.curation.ui.screen.CurationKeywordLinksScreen
import com.linku.curation.ui.screen.CurationMonthlyDetailScreen
import com.linku.curation.ui.screen.CurationRemindScreen
import com.linku.curation.viewModel.CurationViewModel

fun NavGraphBuilder.curationGraph(
    navigator: NavHostController,
    curationViewModel: CurationViewModel,
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

            CurationScreen(
                nickname = nickname,
                viewModel = curationViewModel,
                onMonthlyDetailClick = { navigator.navigate("curation_card1") },
                onKeywordDetailClick = { navigator.navigate("curation_card2") },
                onRemindClick = { navigator.navigate("curation_card3") },
                onMonthlyCurationClick = { navigator.navigate("curation_monthly") }
            )
        }

        composable("curation_card1") {
            showNavBar(false)
            CurationMonthlyDetailScreen(
                onBack = { navigator.popBackStack() },
                onGoHome = goHome
            )
        }

        composable("curation_card2") {
            showNavBar(false)
            CurationKeywordDetailScreen(
                nickname = nickname,
                onBack = { navigator.popBackStack() },
                onGoHome = goHome,
                onKeywordClick = { keyword ->
                    navigator.navigate(
                        "curation_keyword_links/${
                            Uri.encode(
                                keyword
                            )
                        }"
                    )
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
            CurationRemindScreen(onBack = { navigator.popBackStack() })
        }

        composable("curation_monthly") {
            showNavBar(false)
            MonthlyCurationScreen(onBackClick = { navigator.popBackStack() })
        }
    }
}
