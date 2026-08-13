package com.linku.home

import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.linku.home.screen.HomeScreen

@Composable
fun HomeApp(
    viewModel: HomeViewModel,
    nickname: String,
    onNavigateToSetting: () -> Unit,
    onNavigateToSaveLink: (String) -> Unit,
    onNavigateToLinkDetail: (Long) -> Unit,
    onNavigateToAlarm: () -> Unit,
    onSearchOpen: () -> Unit,
    onShowNavBar: (Boolean) -> Unit = {},
) {
    val recentLinks by viewModel.recentLinks.collectAsStateWithLifecycle()
    val recommendedLinks = viewModel.recommendedLinks.collectAsLazyPagingItems()
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "onboarding",
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable("onboarding") {
            HomeScreen(
                homeViewModel = viewModel,
                userName = nickname,
                recommendedLinks = recommendedLinks,
                recentLinks = recentLinks,
                isRecommendMode = viewModel.isRecommendMode,
                onRecommendRequest = { emotionId, situationId ->
                    viewModel.fetchRecommendations(
                        situationId = situationId,
                        emotionId = emotionId,
                    )
                },
                onExitRecommendMode = viewModel::exitRecommendMode,
                needMoreForRecommendation = viewModel.needMoreForRecommendation,
                jobId = viewModel.jobId ?: 2L,
                onLinkClick = onNavigateToLinkDetail,
                onNavigateToSaveLink = onNavigateToSaveLink,
                onAlarmClick = onNavigateToAlarm,
                onSearchOpen = onSearchOpen,
            )
        }
    }
}
