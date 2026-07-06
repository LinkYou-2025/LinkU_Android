package com.linku.curation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.linku.curation.ui.CurationScreen
import com.linku.curation.ui.monthly.MonthlyCurationScreen
import com.linku.curation.ui.screen.CurationMonthlyDetailScreen
import com.linku.curation.ui.screen.CurationRemindScreen

fun NavGraphBuilder.curationGraph(
    navigator: NavHostController,
    curationViewModel: CurationViewModel,
    showNavBar: (Boolean) -> Unit,
    nickname: String,
) {
    navigation(
        startDestination = "curation_list",
        route = "curation"
    ) {
        composable("curation_list") {
            showNavBar(true)

            CurationScreen(
                nickname = nickname,
                viewModel = curationViewModel,
                onCard1Click = { navigator.navigate("curation_card1") },
                onCard3Click = { navigator.navigate("curation_card3") },
                onMonthlyCurationClick = { navigator.navigate("curation_monthly") }
            )
        }

        composable("curation_card1") {
            showNavBar(false)
            CurationMonthlyDetailScreen(
                onBack = { navigator.popBackStack() },
                onGoHome = {
                    navigator.navigate("home") {
                        popUpTo(navigator.graph.findStartDestination().id) {
                            saveState = true
                            inclusive = false
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
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
