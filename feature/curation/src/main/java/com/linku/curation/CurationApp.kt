package com.linku.linku_android.curation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.linku.curation.CurationViewModel
import com.linku.curation.ui.CurationScreen
import com.linku.curation.ui.monthly.MonthlyCurationScreen
import com.linku.curation.ui.screen.CurationCard1Screen
import com.linku.curation.ui.screen.CurationCard3Screen

fun NavGraphBuilder.curationGraph(
    navigator: NavHostController,
    showNavBar: (Boolean) -> Unit,
    nickname: String,
) {
    navigation(
        startDestination = "curation_list",
        route = "curation"
    ) {
        composable("curation_list") { backStackEntry ->
            showNavBar(true)

            val parentEntry = remember(backStackEntry) {
                navigator.getBackStackEntry("curation")
            }
            val curationVm: CurationViewModel = hiltViewModel(parentEntry)

            CurationScreen(
                nickname = nickname,
                viewModel = curationVm,
                onCard1Click = { navigator.navigate("curation_card1") },
                onCard3Click = { navigator.navigate("curation_card3") },
                onMonthlyCurationClick = { navigator.navigate("curation_monthly") }
            )
        }

        composable("curation_card1") {
            showNavBar(false)
            CurationCard1Screen(onBack = { navigator.popBackStack() })
        }

        composable("curation_card3") {
            showNavBar(false)
            CurationCard3Screen(onBack = { navigator.popBackStack() })
        }

        composable("curation_monthly") {
            showNavBar(false)
            MonthlyCurationScreen(onBackClick = { navigator.popBackStack() })
        }
    }
}
