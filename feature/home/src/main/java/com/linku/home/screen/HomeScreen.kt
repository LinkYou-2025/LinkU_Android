package com.linku.home.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.SituationOptions
import com.linku.core.model.SystemBarMode
import com.linku.core.system.SystemBarController
import com.linku.design.component.CustomToastMessage
import com.linku.design.component.LinkCardItem
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.design.top.search.SearchBarTopSheet
import com.linku.home.HomeViewModel
import com.linku.home.R
import com.linku.home.component.ClipboardLinkPasteBanner
import com.linku.home.component.rememberClipboardUrl
import com.linku.home.ui.home.bar.HomeTopBar
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    userName: String,
    recommendedLinks: List<LinkSimpleInfo>,
    recentLinks: List<LinkSimpleInfo>,
    isRecommending: Boolean,
    isLoadingMoreRecommendations: Boolean,
    onRecommendRequest: (emotionId: Long, situationId: Long, size: Int) -> Unit,
    onLoadMoreRecommendations: () -> Unit,
    needMoreForRecommendation: Boolean,
    onClearNeedMoreNotice: () -> Unit,
    jobId: Long,
    onLinkClick: (linkuId: Long) -> Unit,
    onNavigateToSaveLink: (url: String) -> Unit,
    onAlarmClick: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors

    var isRecommendMode by remember { mutableStateOf(false) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        isRecommendMode = false
        homeViewModel.refreshHomeData()
    }

    //스플래쉬에서 숨긴 시스템 바 다시 뜨도록
    val systemBarController =
        LocalContext.current as? SystemBarController
    val isPreview = LocalInspectionMode.current

    // Home 진입 시 시스템 바 표시
    DisposableEffect(Unit) { // systemBarController 대신 Unit 권장
        if (!isPreview && systemBarController != null) {
            systemBarController.setSystemBarMode(SystemBarMode.VISIBLE)
        }
        onDispose {
            // 이 화면을 나갈 때의 동작이 필요 없다면 비움..
        }
    }

    val listState = rememberLazyListState()

    LaunchedEffect(
        listState,
        isRecommendMode,
        isRecommending,
        isLoadingMoreRecommendations,
    ) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItemIndex =
                layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemsCount = layoutInfo.totalItemsCount

            totalItemsCount > 0 &&
                    lastVisibleItemIndex >= totalItemsCount - 2
        }
            .distinctUntilChanged()
            .collect { reachedEnd ->
                if (
                    reachedEnd &&
                    isRecommendMode &&
                    !isRecommending &&
                    !isLoadingMoreRecommendations &&
                    recommendedLinks.isNotEmpty()
                ) {
                    onLoadMoreRecommendations()
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
        hasRequestedRecommend = true // 클릭 기록
        // 선택 없어도 접히는건 스크롤이 담당
        // 추천 요청은 선택이 있어야만
        if (selectedEmotion != null && selectedTask != null) {
            onRecommendRequest(selectedEmotion!!, selectedTask!!, 5)

            isRecommendMode = true
            isTopBarLockedCollapsed = true

            coroutineScope.launch { listState.animateScrollToItem(1) }
        }
    }

    val itemsToRender = if (isRecommendMode) recommendedLinks else recentLinks
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

    val footerHeight = remember(itemsToRender.size, topBarCollapsed, screenHeight) {
        val fraction = slackFractionFor(itemsToRender.size, topBarCollapsed)

        // 아이템이 적어서 스크롤 여유가 필요할 때만 footer를 줌
        if (itemsToRender.size <= 3) {
            screenHeight * fraction
        } else {
            // 아이템 많으면 굳이 여유 필요 없음
            0.dp
        }
    }

    val clipboardUrl by rememberClipboardUrl()
    var dismissedClipboardUrl by remember { mutableStateOf<String?>(null) }

    // 클립보드 배너(사용자가 "아래로 밀어서 닫기" 전까지 유지)
    val shouldShowClipboardBanner =
        clipboardUrl != null && clipboardUrl != dismissedClipboardUrl

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.gray[100])
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.gray[100]),
            state = listState
        ) {
            stickyHeader {
                HomeTopBar(
                    isNoticeExist = false, // TODO: 실제 알림 여부 연결
                    userName = userName,
                    selectedEmotionId = selectedEmotion,
                    onEmotionChange = { id -> selectedEmotion = id },
                    selectedTaskId = selectedTask,
                    onTaskChange = { id -> selectedTask = id },
                    situations = jobSituations,
                    recommendEnabled = (selectedEmotion != null && selectedTask != null && !isRecommending),
                    onRecommendClick = onRecommendClick,
                    isCollapsed = topBarCollapsed,
                    onExpandClick = {
                        hasRequestedRecommend = false
                        isRecommendMode = false
                        onClearNeedMoreNotice()
                        isTopBarLockedCollapsed = false
                        selectedEmotion = null
                        selectedTask = null

                        coroutineScope.launch { listState.animateScrollToItem(0) } // 맨 위로 올려서 펼침 유지
                    },
                    hasRequestedRecommend = hasRequestedRecommend,
                    onAlarmClick = onAlarmClick,
                )
            }

//            item {
//                Column(
//                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
//                ) {
//                    val itemsToRender = if (isRecommendMode) recommendedLinks else recentLinks
//
//                    Text(
//                        text = titleText,
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = colors.black,
//                        modifier = Modifier.padding(start = 4.dp)
//                    )
//
//                    when {
//                        // 1) 최근 열람 링크 없음
//                        !isRecommendMode && itemsToRender.isEmpty() -> {
//                            EmptyRecentBox()
//                        }
//
//                        // 2) 추천 데이터 부족 (링크 3개 미만)
//                        isRecommendMode && needMoreForRecommendation -> {
//                            NeedMoreLinks()
//                        }
//
//                        // 3) 추천할 링크 분류 중
//                        isRecommendMode && isRecommending -> {
//                            Box(
//                                modifier = Modifier.fillMaxSize()
//                            ) {
//                                CustomToastMessage(
//                                    toastMessage = "추천할 링크 분류중..",
//                                    modifier = Modifier
//                                        .align(Alignment.BottomCenter)
//                                        .padding(bottom = 152.dp)
//                                )
//                            }
//                        }
//
//                        // 4) 추천 모드 및 최근 열람 링크 리스트
//                        else -> {
//                            LinkList(
//                                links = itemsToRender,
//                                onCardClick = onLinkClick,
//                                onDeleteClick = { linkuId ->
//                                    // TODO: 삭제 API 연결
//                                }
//                            )
//                        }
//                    }
//                }
//            }

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
                isRecommendMode &&
                        isRecommending &&
                        recommendedLinks.isEmpty() -> {
                    item(key = "initial-recommendation-loading") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                        ) {
                            CustomToastMessage(
                                toastMessage = "추천할 링크 분류중..",
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }
                }

                else -> {
                    items(
                        items = itemsToRender,
                        key = { link ->
                            if (isRecommendMode) {
                                "recommend-${link.userLinkuId}-${link.linkuId}"
                            } else {
                                "recent-${link.userLinkuId}-${link.linkuId}"
                            }
                        },
                    ) { link ->
                        LinkCard(
                            link = link,
                            onCardClick = onLinkClick,
                            onDeleteClick = { userLinkuId ->
                                // TODO: 삭제 API 연결
                            },
                            modifier = Modifier.padding(
                                start = 20.dp,
                                end = 20.dp,
                                bottom = 10.dp,
                            ),
                        )
                    }

                    if (
                        isRecommendMode &&
                        isLoadingMoreRecommendations
                    ) {
                        item(key = "recommendation-loading-more") {
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
                    }
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
        onDismiss = { homeViewModel.updateSearchTopSheetVisible(false) },
        onQueryChange = { homeViewModel.fastSearch(it) },
        onQuerySave = { homeViewModel.addRecentQuery(it) },
        onQueryDelete = { homeViewModel.removeRecentQuery(it) },
        onQueryClear = { homeViewModel.clearRecentQuery() },
        fastSearchItems = homeViewModel.fastSearchItems.collectAsState().value,
        recentQueries = homeViewModel.recentQueryList.collectAsState().value.map{it.text}
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

@Composable
private fun LinkCard(
    link: LinkSimpleInfo,
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
            onCardClick = {
                onCardClick(link.linkuId)
            },
            onDeleteClick = {
                link.userLinkuId?.let(onDeleteClick)
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    ThemeProvider {
        HomeScreen(
            homeViewModel = hiltViewModel(),
            userName = "세나",
            recommendedLinks = emptyList(),
            recentLinks = emptyList(),
            isRecommending = false,
            isLoadingMoreRecommendations = false,
            onLoadMoreRecommendations = { },
            onRecommendRequest = { _, _, _ -> },
            needMoreForRecommendation = false,
            onClearNeedMoreNotice = { },
            jobId = 2L,
            onLinkClick = { },
            onNavigateToSaveLink = { },
            onAlarmClick = { }
        )
    }
}