package com.linku.home.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.paging.PagingData
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.SituationOptions
import com.linku.design.component.CustomToastMessage
import com.linku.design.component.LinkCardItem
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.design.top.search.SearchBarUiState
import com.linku.design.top.search.SearchBarTopSheet
import com.linku.design.top.search.SearchResultItem
import com.linku.home.HomeViewModel
import com.linku.home.R
import com.linku.home.component.ClipboardLinkPasteBanner
import com.linku.home.component.rememberClipboardUrl
import com.linku.home.ui.home.bar.HomeTopBar
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    userName: String,
    recommendedLinks: LazyPagingItems<LinkSimpleInfo>,
    recentLinks: List<LinkSimpleInfo>,
    isRecommendMode: Boolean,
    onRecommendRequest: (
        emotionId: Long,
        situationId: Long,
    ) -> Unit,
    onExitRecommendMode: () -> Unit,
    needMoreForRecommendation: Boolean,
    jobId: Long,
    onLinkClick: (userLinkuId: Long) -> Unit,
    onNavigateToSaveLink: (url: String) -> Unit,
    onAlarmClick: () -> Unit,
    searchUiState: SearchBarUiState,
    searchResults: Flow<PagingData<SearchResultItem>>,
    onSearchQueryChange: (String) -> Unit,
    onSearchOpen: () -> Unit,
    onSearchDismiss: () -> Unit,
    onSearchHistoryDelete: (Long) -> Unit,
    onSearchHistoryClear: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        homeViewModel.refreshHomeData()
    }

    // 시스템 바 숨김/복원은 MainScreen의 EdgeToEdgeSystemBars(hideSystemBars) 한 곳에서만 처리함
    // (MainApp.kt가 로그인 성공 시 edgeToEdgeSystemBars=false로 되돌림).

    var openedDeleteMenuId by remember { mutableStateOf<Long?>(null) }

    // 알림 읽지 않음 여부 갱신
    LaunchedEffect(Unit) {
        homeViewModel.refreshUnreadAlarm()
    }
    val listState = rememberLazyListState()

    val recommendationRefreshState = recommendedLinks.loadState.refresh

    val recommendationAppendState = recommendedLinks.loadState.append

    val isInitialRecommendationLoading =
        isRecommendMode &&
                recommendationRefreshState is LoadState.Loading &&
                recommendedLinks.itemCount == 0

    val isAppendRecommendationLoading =
        isRecommendMode &&
                recommendationAppendState is LoadState.Loading

    val initialRecommendationError = recommendationRefreshState as? LoadState.Error

    val appendRecommendationError = recommendationAppendState as? LoadState.Error

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { isScrolling ->
                if (isScrolling) {
                    openedDeleteMenuId = null
                }
            }
    }

    val coroutineScope = rememberCoroutineScope()

    var selectedEmotion by remember { mutableStateOf<Long?>(null) }
    var selectedTask by remember { mutableStateOf<Long?>(null) }

    // 추천 누르면 강제로 접힘 유지하는 용도
    var isTopBarLockedCollapsed by remember { mutableStateOf(false) }

    // 직업별 상황 리스트
    val jobSituations = remember(jobId) { SituationOptions.situationsFor(jobId) }

    val density = LocalDensity.current
    val collapseThresholdDp = remember { 20.dp }

    var hasRequestedRecommend by remember { mutableStateOf(false) }

    LaunchedEffect(isRecommendMode) {
        if (isRecommendMode) {
            isTopBarLockedCollapsed = true
            hasRequestedRecommend = true
        }
    }

    LaunchedEffect(
        listState.firstVisibleItemIndex,
        listState.firstVisibleItemScrollOffset,
        density
    ) {
        val scrollOffsetDp = with(density) {
            listState.firstVisibleItemScrollOffset.toDp()
        }

        val shouldCollapse =
            listState.firstVisibleItemIndex > 0 ||
                    scrollOffsetDp > collapseThresholdDp

        if (shouldCollapse) {
            isTopBarLockedCollapsed = true
        }
    }

    val topBarCollapsed = isTopBarLockedCollapsed

    val onRecommendClick: () -> Unit = {
        hasRequestedRecommend = true

        val emotionId = selectedEmotion
        val situationId = selectedTask

        if (
            emotionId != null &&
            situationId != null &&
            !isInitialRecommendationLoading
        ) {
            onRecommendRequest(
                emotionId,
                situationId,
            )

            isTopBarLockedCollapsed = true

            coroutineScope.launch {
                listState.animateScrollToItem(1)
            }
        }
    }

    val titleText =
        if (isRecommendMode) "${userName}님에게 딱 맞는 링크"
        else "${userName}님이 최근에 열람한 링크"

    fun slackFractionFor(count: Int, collapsed: Boolean): Float = when (count) {
        0 -> 0f
        1 -> if (!collapsed) 0.55f else 0.48f
        2 -> if (!collapsed) 0.40f else 0.33f
        3 -> if (!collapsed) 0.28f else 0.22f
        else -> 0f
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val footerHeight =
        remember(
            recentLinks.size,
            topBarCollapsed,
            screenHeight,
        ) {
            val fraction = slackFractionFor(
                count = recentLinks.size,
                collapsed = topBarCollapsed,
            )

            if (recentLinks.size <= 3) {
                screenHeight * fraction
            } else {
                0.dp
            }
        }

    val clipboardUrl by rememberClipboardUrl()
    var dismissedClipboardUrl by remember { mutableStateOf<String?>(null) }

    // 클립보드 배너(사용자가 "아래로 밀어서 닫기" 전까지 유지)
    val shouldShowClipboardBanner =
        clipboardUrl != null && clipboardUrl != dismissedClipboardUrl

    val isUnreadAlarmExists by homeViewModel.isUnreadAlarmExists.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.gray[100])
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.gray[100])
                .then(
                    if (openedDeleteMenuId != null) {
                        Modifier.pointerInput(openedDeleteMenuId) {
                            detectTapGestures {
                                openedDeleteMenuId = null
                            }
                        }
                    } else {
                        Modifier
                    }
                ),
            state = listState
        ) {
            stickyHeader {
                HomeTopBar(
                    isNoticeExist = isUnreadAlarmExists,
                    userName = userName,
                    selectedEmotionId = selectedEmotion,
                    onEmotionChange = { id -> selectedEmotion = id },
                    selectedTaskId = selectedTask,
                    onTaskChange = { id -> selectedTask = id },
                    situations = jobSituations,
                    recommendEnabled =
                        selectedEmotion != null &&
                                selectedTask != null &&
                                !isInitialRecommendationLoading,
                    onRecommendClick = onRecommendClick,
                    isCollapsed = topBarCollapsed,
                    expandEnabled =
                        !isInitialRecommendationLoading &&
                                !isAppendRecommendationLoading,
                    onExpandClick = {
                        if (
                            !isInitialRecommendationLoading &&
                            !isAppendRecommendationLoading
                        ) {
                            openedDeleteMenuId = null
                            hasRequestedRecommend = false

                            onExitRecommendMode()

                            isTopBarLockedCollapsed = false
                            selectedEmotion = null
                            selectedTask = null

                            coroutineScope.launch {
                                listState.animateScrollToItem(0)
                            }
                        }
                    },
                    hasRequestedRecommend = hasRequestedRecommend,
                    onAlarmClick = onAlarmClick,
                    onSearchClick = {
                        homeViewModel.updateSearchTopSheetVisible(true)
                        onSearchOpen()
                    },
                )
            }

            item(key = "home-title") {
                Text(
                    text = titleText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.black,
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 20.dp,
                        top = 20.dp,
                        bottom = 20.dp,
                    ),
                )
            }

            when {
                // 최근 열람 링크가 없는 경우
                !isRecommendMode && recentLinks.isEmpty() -> {
                    item(key = "empty-recent") {
                        Box(
                            modifier = Modifier.padding(horizontal = 20.dp),
                        ) {
                            EmptyRecentBox()
                        }
                    }
                }

                // 추천에 필요한 저장 링크가 부족한 경우
                isRecommendMode && needMoreForRecommendation -> {
                    item(key = "need-more-links") {
                        Box(
                            modifier = Modifier.padding(horizontal = 20.dp),
                        ) {
                            NeedMoreLinks()
                        }
                    }
                }

                // 최초 추천 데이터를 불러오는 경우
                isInitialRecommendationLoading -> {
                    /*
                     * 기존 정책대로 목록 영역은 비웁니다.
                     * 하단 CustomToastMessage에서 로딩을 표시합니다.
                     */
                }

                isRecommendMode && initialRecommendationError != null -> {
                    item(key = "recommendation-refresh-error") {
                        RecommendationLoadError(
                            message = "추천 링크를 불러오지 못했어요.",
                            onRetry = recommendedLinks::retry,
                        )
                    }
                }

                isRecommendMode -> {
                    items(
                        count = recommendedLinks.itemCount,
                        key = recommendedLinks.itemKey { link ->
                            "recommend-${link.userLinkuId}"
                        },
                        contentType = recommendedLinks.itemContentType { "recommendation-link" },
                    ) { index ->
                        val link = recommendedLinks[index] ?: return@items
                        val menuId = link.userLinkuId

                        LinkCard(
                            link = link,
                            isDeleteMenuVisible = openedDeleteMenuId == menuId,
                            onMoreClick = {
                                openedDeleteMenuId = if (openedDeleteMenuId == menuId) null else menuId
                            },
                            onDeleteClick = {
                                openedDeleteMenuId = null

                                /*
                                 * TODO 삭제 API 성공 후:
                                 * recommendedLinks.refresh()
                                 */
                            },
                            onCardClick = { userLinkuId ->
                                openedDeleteMenuId = null
                                onLinkClick(userLinkuId)
                            },
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                        )
                    }

                    when {
                        isAppendRecommendationLoading -> {
                            item(key = "recommendation-append-loading") {
                                RecommendationAppendLoading()
                            }
                        }

                        appendRecommendationError != null -> {
                            item(key = "recommendation-append-error") {
                                RecommendationAppendError(onRetry = recommendedLinks::retry)
                            }
                        }
                    }
                }

                else -> {
                    items(
                        items = recentLinks,
                        key = { link -> "recent-${link.userLinkuId}" },
                    ) { link ->
                        val menuId = link.userLinkuId

                        LinkCard(
                            link = link,
                            isDeleteMenuVisible = openedDeleteMenuId == menuId,
                            onMoreClick = {
                                openedDeleteMenuId =
                                    if (openedDeleteMenuId == menuId) {
                                        null
                                    } else {
                                        menuId
                                    }
                            },
                            onDeleteClick = { userLinkuId ->
                                openedDeleteMenuId = null

                                // TODO: 삭제 API 연결
                            },
                            onCardClick = { userLinkuId ->
                                openedDeleteMenuId = null
                                onLinkClick(userLinkuId)
                            },
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                        )
                    }

//                    if (
//                        isRecommendMode &&
//                        isLoadingMoreRecommendations
//                    ) {
//                        item(key = "recommendation-loading-more") {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(80.dp),
//                                contentAlignment = Alignment.Center,
//                            ) {
//                                Text(
//                                    text = "추천 링크를 더 불러오는 중...",
//                                    fontSize = 13.sp,
//                                    fontWeight = FontWeight.Normal,
//                                    color = colors.gray[600],
//                                )
//                            }
//                        }
//                    }
                }
            }

            if (!isRecommendMode && footerHeight > 0.dp) {
                item(key = "footer-slack") {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(footerHeight),
                    )
                }
            }
        }

        if (isInitialRecommendationLoading) {
            CustomToastMessage(
                toastMessage = "추천할 링크 분류중..",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 22.dp)
                    .zIndex(20f),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 15.dp)
                .zIndex(10f)
        ) {
            ClipboardLinkPasteBanner(
                visible = shouldShowClipboardBanner,
                link = clipboardUrl.orEmpty(),
                modifier = Modifier,
                onDismiss = {
                    dismissedClipboardUrl = clipboardUrl  // 사용자가 닫은 링크는 동일 값이면 다시 안 띄움
                },
                onPasteClick = {
                    clipboardUrl?.let { url ->
                        onNavigateToSaveLink(url)  // 저장 화면으로 이동
                        dismissedClipboardUrl = url  // 눌렀으면 배너는 닫아버리기
                    }
                }
            )
        }
    }

    // 검색창 탑 시트
    SearchBarTopSheet(
        visible = homeViewModel.searchTopSheetVisible,
        onLinkClick = { onLinkClick(it) },
        onDismiss = {
            if (homeViewModel.searchTopSheetVisible) {
                homeViewModel.updateSearchTopSheetVisible(false)
                onSearchDismiss()
            }
        },
        onQueryChange = onSearchQueryChange,
        onQueryDelete = onSearchHistoryDelete,
        onQueryClear = onSearchHistoryClear,
        searchResults = searchResults,
        uiState = searchUiState,
    )
}

@Composable
private fun EmptyRecentBox() {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.gray[100])
            .padding(top = 65.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_no_recents),
            contentDescription = null,
            modifier = Modifier.size(width = 80.dp, height = 60.55.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "최근에 열람한 링크가 없어요!",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = colors.gray[800]
        )

        Spacer(modifier = Modifier.height(1.dp))

        Text(
            text = "지금 링크를 둘러보세요",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = colors.gray[600]
        )
    }
}

@Composable
private fun NeedMoreLinks() {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.gray[100])
            .padding(top = 65.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_no_recents),
            contentDescription = null,
            modifier = Modifier.size(width = 80.dp, height = 60.55.dp)
        )

        Spacer(modifier = Modifier.height(25.45.dp))

        Text(
            text = "지금 마음과 딱 맞는 콘텐츠는 아직 없지만,\n저장된 링크가 늘어날수록 더 나은 추천이 가능해져요.",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            color = colors.gray[600]
        )
    }
}

// 추가 로딩 UI
@Composable
private fun RecommendationAppendLoading() {  // TODO: 다인언니에게 물어본 후 확정 예정, 지금은 임시
    val colors = MaterialTheme.linkuColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "추천 링크를 더 불러오는 중...",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = colors.gray[600],
        )
    }
}

// 추가 로딩 실패 UI
@Composable
private fun RecommendationAppendError(  // TODO: 다인언니에게 물어본 후 확정 예정, 지금은 임시
    onRetry: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "추천 링크를 더 불러오지 못했어요.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = colors.gray[600],
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "다시 시도",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = colors.black,
            modifier = Modifier
                .clickable(
                    role = Role.Button,
                    onClick = onRetry,
                )
                .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                .wrapContentSize()
                .padding(horizontal = 12.dp),
        )
    }
}

// 최초 로딩 실패 UI
@Composable
private fun RecommendationLoadError(  // TODO: 다인언니에게 물어본 후 확정 예정, 지금은 임시
    message: String,
    onRetry: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp,vertical = 65.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = colors.gray[800],
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "다시 시도하기",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = colors.black,
            modifier = Modifier
                .clickable(
                    role = Role.Button,
                    onClick = onRetry,
                )
                .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                .wrapContentSize()
                .padding(horizontal = 12.dp),
        )
    }
}

@Composable
private fun LinkCard(
    link: LinkSimpleInfo,
    isDeleteMenuVisible: Boolean,
    onMoreClick: () -> Unit,
    onCardClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        LinkCardItem(
            hasAiSummary = link.aiArticleExists,
            linkTitle = link.title,
            tags = buildList {
                link.categoryType?.tagName?.let(::add)
                link.emotionType?.tagName?.let(::add)
            },
            domainName = link.domain,
            isExternalLink = false,
            linkImageUrl = link.linkuImageUrl.orEmpty(),
            domainImageUrl = link.domainImageUrl.orEmpty(),
            isDeleteMenuVisible = isDeleteMenuVisible,
            onMoreClick = onMoreClick,
            onCardClick = {
                onCardClick(link.userLinkuId)
            },
            onDeleteClick = {
                onDeleteClick(link.userLinkuId)
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEmptyRecentBox() {
    ThemeProvider {
        EmptyRecentBox()
    }
}
