package com.linku.linku_android.curation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.linku.curation.CurationViewModel
import com.linku.curation.ui.CurationScreen
import com.linku.curation.ui.screen.detail.CurationMonthDetailScreen

@OptIn (ExperimentalSharedTransitionApi::class)
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
                viewModel = curationVm
            )
        }

        composable(
            route = "curation_month_detail/{curationId}?imageUrl={imageUrl}&cardIndex={cardIndex}",
//            enterTransition = { EnterTransition.None },
//            exitTransition = { ExitTransition.None }
        ) { backStack ->
            showNavBar(false)

            val curationId = backStack.arguments?.getString("curationId")?.toLong() ?: 0L
            val imageUrl = backStack.arguments?.getString("imageUrl")
            val cardIndex = backStack.arguments?.getString("cardIndex")?.toInt() ?: 0

            CurationMonthDetailScreen(
                curationId = curationId,
                imageUrl = imageUrl,
                onBack = { navigator.popBackStack() }
            )
        }
    }
}
