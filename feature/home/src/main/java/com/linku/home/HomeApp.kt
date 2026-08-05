package com.linku.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.PagingData
import com.linku.design.top.search.SearchBarUiState
import com.linku.design.top.search.SearchResultItem
import com.linku.home.screen.AlarmScreen
import com.linku.home.screen.HomeScreen
import com.linku.home.viewmodel.AlarmViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun HomeApp(
    viewModel: HomeViewModel,
    nickname: String,
    onNavigateToSetting: () -> Unit,
    onNavigateToSaveLink: (String) -> Unit,
    onNavigateToLinkDetail: (Long) -> Unit,
    onNavigateToCuration: () -> Unit,
    onNavigateToAlarm: () -> Unit,
    searchUiState: SearchBarUiState,
    searchResults: Flow<PagingData<SearchResultItem>>,
    onSearchQueryChange: (String) -> Unit,
    onSearchOpen: () -> Unit,
    onSearchDismiss: () -> Unit,
    onSearchHistoryDelete: (Long) -> Unit,
    onSearchHistoryClear: () -> Unit,
    onShowNavBar: (Boolean) -> Unit = {},
) {
    val recentLinks by viewModel.recentLinks.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val navController = rememberNavController()

    // 일림 목록창에서 사용할 뷰모델
    // 홈 화면에 귀속되는 UI이므로, MainApp에서부터 주입하지 않고
    // HomeApp에서 만들어 주입한다.
    //val alarmViewModel: AlarmViewModel = hiltViewModel()

    // 상태바/내비게이션 바는 MainScreen(app 모듈)에서 공통으로 흰색 처리함.

    // === 감정/상황 키 → 서버 ID 매핑 ===
    // fun emotionKeyToId(key: String): Long = when (key) {
    //     "joy" -> 1L
    //     "calm" -> 2L
    //     "excitement" -> 3L
    //     "sadness" -> 4L
    //     "irritation" -> 5L
    //     "anger" -> 6L
    //     else -> 0L
    // }
    //
    // fun taskKeyToSituationId(key: String): Long = when (key) {
    //     "트렌드 확인" -> 11L
    //     "과제 중"   -> 12L
    //     "쇼핑 중"   -> 13L
    //     "데이트 중" -> 14L
    //     "통학 중"   -> 15L
    //     "알바 중"   -> 16L
    //     "휴식 중"   -> 17L
    //     "자기 전"   -> 18L
    //     else -> 0L
    // }

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

    NavHost(
        navController = navController,
        startDestination = "onboarding",
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
                onAlarmClick = onNavigateToAlarm,
                searchUiState = searchUiState,
                searchResults = searchResults,
                onSearchQueryChange = onSearchQueryChange,
                onSearchOpen = onSearchOpen,
                onSearchDismiss = onSearchDismiss,
                onSearchHistoryDelete = onSearchHistoryDelete,
                onSearchHistoryClear = onSearchHistoryClear,
            )
        }
    }
}
