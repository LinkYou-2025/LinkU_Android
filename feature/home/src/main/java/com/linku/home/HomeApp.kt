package com.linku.home

import android.net.Uri
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.linku.home.screen.AlarmScreen
import com.linku.home.screen.HomeScreen
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun HomeApp(
    viewModel: HomeViewModel,
    nickname: String,
    onNavigateToMyPage: () -> Unit,
    onNavigateToSaveLink: (String) -> Unit,
    onNavigateToLinkDetail: (Long) -> Unit,
    onShowNavBar: (Boolean) -> Unit = {},
) {
    val recentLinks by viewModel.recentLinks.collectAsStateWithLifecycle()
    val navController = rememberNavController()

//    LaunchedEffect(Unit) {
//        viewModel.loadCategoryColors()
//    }

    NavHost(
        navController = navController,
        startDestination = "onboarding",
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable("onboarding") {
            HomeScreen(
                homeViewModel = viewModel,
                userName = nickname,
                showRecommendations = viewModel.showRecommendations,
                recommendedLinks = viewModel.recommendedLinks,
                recentLinks = recentLinks,
                isRecommending = viewModel.isRecommending,
                onRecommendRequest = { emotionId, situationId, size ->
                    viewModel.fetchRecommendations(
                        situationId = situationId,
                        emotionId = emotionId,
                        size = size
                    )
                },
                needMoreForRecommendation = viewModel.needMoreForRecommendation,
                onClearNeedMoreNotice = viewModel::clearNeedMoreNotice,
                jobId = viewModel.jobId ?: 2L,
                onLinkClick = { id ->
                    onNavigateToLinkDetail(id)
                },
                onNavigateToSaveLink = { url ->
                    onNavigateToSaveLink(url)
                },
                onAlarmClick = { navController.navigate("alarm") }
            )
        }

        composable("alarm") {
            DisposableEffect(Unit) {
                onShowNavBar(false)
                onDispose { onShowNavBar(true) }
            }

            AlarmScreen(
                onNavigateToMyPage = onNavigateToMyPage,
                onBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate("onboarding") {
                        popUpTo("onboarding") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

/** Uri를 앱 캐시 폴더의 임시 File로 복사 */
private fun Uri.toTempFile(context: android.content.Context): File {
    val fileName = "picked_${System.currentTimeMillis()}.jpg"
    val tempFile = File(context.cacheDir, fileName)
    context.contentResolver.openInputStream(this).use { input: InputStream? ->
        FileOutputStream(tempFile).use { output ->
            if (input != null) input.copyTo(output)
        }
    }
    return tempFile
}