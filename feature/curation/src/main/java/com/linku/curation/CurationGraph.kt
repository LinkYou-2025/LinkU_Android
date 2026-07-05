package com.linku.curation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.linku.curation.ui.CurationScreen
import com.linku.curation.ui.monthly.MonthlyCurationScreen
import com.linku.curation.ui.screen.CurationCard1Screen
import com.linku.curation.ui.screen.CurationCard3Screen

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
