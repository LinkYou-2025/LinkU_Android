package com.linku.home

import android.widget.Toast
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.linku.home.screen.AlarmScreen
import com.linku.home.screen.HomeScreen
import com.linku.home.viewmodel.AlarmViewModel

@Composable
fun HomeApp(
    viewModel: HomeViewModel,
    nickname: String, // 닉네임 호출을 위해 추가함.
    onNavigateToSetting: () -> Unit,
    onNavigateToSaveLink: (String) -> Unit,
    onNavigateToLinkDetail: (Long) -> Unit,
    onShowNavBar: (Boolean) -> Unit = {},
) {
    val recentLinks by viewModel.recentLinks.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val navController = rememberNavController()

    // 일림 목록창에서 사용할 뷰모델
    // 홈 화면에 귀속되는 UI이므로, MainApp에서부터 주입하지 않고
    // HomeApp에서 만들어 주입한다.
    val alarmViewModel: AlarmViewModel = hiltViewModel()

    // 상태바/내비게이션 바는 MainScreen(app 모듈)에서 공통으로 흰색 처리함.

//    // === 감정/상황 키 → 서버 ID 매핑 ===
//    fun emotionKeyToId(key: String): Long = when (key) {
//        "joy" -> 1L
//        "calm" -> 2L
//        "excitement" -> 3L
//        "sadness" -> 4L
//        "irritation" -> 5L
//        "anger" -> 6L
//        else -> 0L
//    }
//    fun taskKeyToSituationId(key: String): Long = when (key) {
//        "트렌드 확인" -> 11L
//        "과제 중"   -> 12L
//        "쇼핑 중"   -> 13L
//        "데이트 중" -> 14L
//        "통학 중"   -> 15L
//        "알바 중"   -> 16L
//        "휴식 중"   -> 17L
//        "자기 전"   -> 18L
//        else -> 0L
//    }

    // 외부 브라우저 열기
    fun openUrl(url: String) {
        runCatching {
            val fixed = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(fixed))
            context.startActivity(intent)
        }.onFailure {
            Toast.makeText(context, "링크를 열 수 없어요.", Toast.LENGTH_SHORT).show()
        }
    }

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
                recommendedLinks = viewModel.recommendedLinks,
                recentLinks = recentLinks,
                isRecommendMode = viewModel.isRecommendMode,
                isRecommending = viewModel.isRecommending,
                isLoadingMoreRecommendations = viewModel.isLoadingMoreRecommendations,
                onRecommendRequest = { emotionId, situationId, size ->
                    viewModel.fetchRecommendations(
                        situationId = situationId,
                        emotionId = emotionId,
                        size = size
                    )
                },
                onLoadMoreRecommendations = viewModel::loadMoreRecommendations,
                onExitRecommendMode = viewModel::exitRecommendMode,
                needMoreForRecommendation = viewModel.needMoreForRecommendation,
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
                onNavigateToSetting = onNavigateToSetting,
                onBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate("onboarding") {
                        popUpTo("onboarding") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                viewModel = alarmViewModel
            )
        }
    }
}