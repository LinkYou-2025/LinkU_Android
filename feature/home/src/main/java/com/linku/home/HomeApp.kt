package com.linku.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.linku.design.util.WhiteSystemBars
import com.linku.home.screen.AlarmScreen
import com.linku.home.screen.HomeScreen
import com.linku.home.screen.NoticeScreen
import com.linku.home.viewmodel.AlarmViewModel

@Composable
fun HomeApp(
    viewModel: HomeViewModel,
    nickname: String, // 닉네임 호출을 위해 추가함.
    onNavigateToSetting: () -> Unit,
    onNavigateToSaveLink: (String) -> Unit,
    onNavigateToLinkDetail: (Long) -> Unit,
    onNavigateToCuration: () -> Unit,
    onShowNavBar: (Boolean) -> Unit = {},
) {
    val recentLinks by viewModel.recentLinks.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val navController = rememberNavController()

    // 일림 목록창에서 사용할 뷰모델
    // 홈 화면에 귀속되는 UI이므로, MainApp에서부터 주입하지 않고
    // HomeApp에서 만들어 주입한다.
    val alarmViewModel: AlarmViewModel = hiltViewModel()

    WhiteSystemBars()

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
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fixed))
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
                onNavigateToSetting = onNavigateToSetting,
                onBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate("onboarding") {
                        popUpTo("onboarding") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                viewModel = alarmViewModel,
                onNavigateToNotice = { targetId ->
                    navController.navigate("notice/$targetId")
                },
                onNavigateToLinkDetail = onNavigateToLinkDetail,
                onNavigateToFolder = {}, // TODO: 지민오빠가 나중에 이동 함수 준다고 했씀!
                onNavigateToCuration = {}, // TODO: 아직 curation_card1 라우트에 파라미터가 없어서 일단은 빈 람다 처리
            )
        }

        composable(
            route = "notice/{targetId}",
            arguments = listOf(navArgument("targetId") { type = NavType.LongType })
        ) {
            NoticeScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}