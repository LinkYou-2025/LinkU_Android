package com.example.home.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.example.core.model.LinkSimpleInfo
import com.example.core.system.SystemBarController
import com.example.design.BrushText
import com.example.design.top.search.SearchBarTopSheet
import com.example.design.modifier.noRippleClickable
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.LocalFontTheme
import com.example.design.theme.color.Basic
import com.example.file.ui.theme.FileTopBarLinkUFont
import com.example.file.ui.theme.MainColor
import androidx.compose.runtime.DisposableEffect
import com.example.core.model.SystemBarMode
import com.example.home.HomeViewModel
import com.example.home.R
import com.example.home.component.ClipboardLinkPasteBanner
import com.example.home.component.rememberClipboardUrl
import com.example.home.ui.top.bar.HomeTopBar
import kotlinx.coroutines.launch
import com.example.design.R as Res

data class LinkItem(
    val imageResId: Int?,  // 링크 대표 이미지
    val title: String,  // 링크 제목
    val tags: List<String>,  // 태그 2개
    val siteIconResId: Int,  // 사이트 아이콘 (예: 네이버, 유튜브 등)
    val siteName: String,  // 사이트 이름
    val aiSummarized: Boolean  // AI 요약 여부
)

data class Situation(val id: Long, val name: String)

fun situationsFor(jobId: Long): List<Situation> = when (jobId) {
    1L -> listOf( // 고등학생 1~8
        Situation(1, "통학 중"), Situation(2, "공부 중"), Situation(3, "식사 중"), Situation(4, "시험 준비"),
        Situation(5, "친구랑"), Situation(6, "쇼핑 중"), Situation(7, "휴식 중"), Situation(8, "자기 전")
    )
    2L -> listOf( // 대학생 9~16
        Situation(9, "과제 중"), Situation(10, "통학 중"), Situation(11, "쇼핑 중"), Situation(12, "알바 중"),
        Situation(13, "트렌드 확인"), Situation(14, "데이트 중"), Situation(15, "휴식 중"), Situation(16, "자기 전")
    )
    3L -> listOf( // 직장인 17~24
        Situation(17, "출퇴근"), Situation(18, "트렌드 확인"), Situation(19, "업무 중"), Situation(20, "커리어 고민"),
        Situation(21, "쇼핑 중"), Situation(22, "데이트 중"), Situation(23, "휴식 중"), Situation(24, "자기 전")
    )
    4L -> listOf( // 자영업자 25~32
        Situation(25, "출퇴근"), Situation(26, "업무 준비 중"), Situation(27, "데이트 중"), Situation(28, "식사"),
        Situation(29, "쇼핑 중"), Situation(30, "트렌드 확인"), Situation(31, "휴식 중"), Situation(32, "자기 전")
    )
    5L -> listOf( // 프리랜서 33~40
        Situation(33, "작업 중"), Situation(34, "쇼핑 중"), Situation(35, "트렌드 확인"), Situation(36, "데이트 중"),
        Situation(37, "운동 중"), Situation(38, "식사"), Situation(39, "휴식 중"), Situation(40, "자기 전")
    )
    6L -> listOf( // 취준생 41~48
        Situation(41, "자소서 작성"), Situation(42, "면접 준비"), Situation(43, "요리 중"), Situation(44, "트렌드 확인"),
        Situation(45, "쇼핑 중"), Situation(46, "운동 중"), Situation(47, "휴식 중"), Situation(48, "자기 전")
    )
    else -> situationsFor(3L) // 혹시 모를 기본값(직장인 세트)
}

private fun emotionName(id: Long?): String? = when (id) {
    1L -> "즐거움"
    2L -> "평온"
    3L -> "설렘"
    4L -> "슬픔"
    5L -> "짜증"
    6L -> "분노"
    else -> null
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    userName: String,
    showRecommendations: Boolean,
    recommendedLinks: List<LinkSimpleInfo>,
    recentLinks: List<LinkSimpleInfo>,
    isRecommending: Boolean,
    onRecommendRequest: (emotionId: Long, situationId: Long, size: Int) -> Unit,
    needMoreForRecommendation: Boolean,
    onClearNeedMoreNotice: () -> Unit,
    jobId: Long,
    onLinkClick: (linkuId: Long) -> Unit,
    onNavigateToSaveLink: (url: String) -> Unit,
) {
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
    val coroutineScope = rememberCoroutineScope()

    var showRecs by remember(showRecommendations) { mutableStateOf(showRecommendations) }

    var selectedEmotion by remember { mutableStateOf<Long?>(null) }
    var selectedTask by remember { mutableStateOf<Long?>(null) }

    // 추천 누르면 강제로 접힘 유지하는 용도
    var isTopBarLockedCollapsed by remember { mutableStateOf(false) }

    // 직업별 상황 리스트
    val jobSituations = remember(jobId) { situationsFor(jobId) }

    val density = LocalDensity.current
    val collapseThresholdDp = remember { 20.dp }

    // firstVisibleItemIndex>0 이면 이미 헤더가 올라간 상태라 무조건 접힘
    LaunchedEffect(
        listState.firstVisibleItemIndex,
        listState.firstVisibleItemScrollOffset,
        density
    ) {
        val scrollOffsetDp = with(density) { listState.firstVisibleItemScrollOffset.toDp() }

        val shouldCollapse =
            listState.firstVisibleItemIndex > 0 || scrollOffsetDp > collapseThresholdDp

        // "접힘 고정" 상태에 스크롤 접힘을 누적
        isTopBarLockedCollapsed = isTopBarLockedCollapsed || shouldCollapse
    }

    // 고정 접힘이 우선 (collapsedByScroll 변수 삭제)
    val topBarCollapsed = isTopBarLockedCollapsed

    var hasRequestedRecommend by remember { mutableStateOf(false) }

    val onRecommendClick: () -> Unit = {
        hasRequestedRecommend = true // 클릭 기록
        // 선택 없어도 접히는건 스크롤이 담당
        // 추천 요청은 선택이 있어야만
        if (selectedEmotion != null && selectedTask != null) {
            onClearNeedMoreNotice()
            onRecommendRequest(selectedEmotion!!, selectedTask!!, 10)
            showRecs = true

            // 추천 눌렀으면 강제 접힘(스크롤 상관 없이)
            isTopBarLockedCollapsed = true

            coroutineScope.launch { listState.animateScrollToItem(1) }
        }
    }

    val itemsToRender = if (showRecs) recommendedLinks else recentLinks
    val titleText =
        if (showRecs) "${userName}님의 오늘에 어울리는 콘텐츠예요!"
        else "${userName}님이 최근에 열람한 링크"

    fun slackFractionFor(count: Int, collapsed: Boolean): Float = when (count) {
        0 -> 0f
        1 -> if (!collapsed) 0.55f else 0.48f
        2 -> if (!collapsed) 0.40f else 0.33f
        3 -> if (!collapsed) 0.28f else 0.22f
        else -> 0f
    }

//    val slackFraction = remember(itemsToRender.size, topBarCollapsed) {
//        slackFractionFor(itemsToRender.size, topBarCollapsed)
//    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val footerHeight = remember(itemsToRender.size, topBarCollapsed, screenHeight) {
        val fraction = slackFractionFor(itemsToRender.size, topBarCollapsed)

        // 아이템이 적어서 스크롤 여유가 필요할 때만 footer를 줌
        if (itemsToRender.size <= 3) {
            maxOf(screenHeight * fraction, 800.dp)
        } else {
            // 아이템 많으면 굳이 여유 필요 없음
            0.dp
        }
    }

    val clipboardUrl by rememberClipboardUrl()
    var dismissedClipboardUrl by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.gray[100])
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColorTheme.current.gray[100]),
            state = listState
        ) {
            stickyHeader {
                Spacer(modifier = Modifier.height(32.dp))

                HomeTopBar(
                    isNoticeExist = false, // TODO 실제 알림 여부 연결
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
                        // "링크 추천해줘!" 누르기 전으로(원하는 동작)
                        showRecs = false
                        onClearNeedMoreNotice()
                        isTopBarLockedCollapsed = false

                        coroutineScope.launch { listState.animateScrollToItem(0) } // 맨 위로 올려서 펼침 유지
                    },
                    hasRequestedRecommend = hasRequestedRecommend
                )
            }

            item {
                Column(
                    modifier = Modifier.padding(20.dp, 24.dp)
                ) {
                    val itemsToRender = if (showRecs) recommendedLinks else recentLinks
                    val titleText = if (showRecs) "${userName}님의 오늘에 어울리는 콘텐츠예요!"
                    else "${userName}님이 최근에 열람한 링크"

                    when {
                        // 1) 링크 3개 미만 안내
                        showRecs && needMoreForRecommendation -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LocalColorTheme.current.gray[100])
                                    .padding(top = 65.dp, bottom = 195.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_no_recents),
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp)
                                )

                                Spacer(modifier = Modifier.height(25.dp))

                                Text(
                                    text = "추천을 위해 최소 3개의 링크가 필요해요.\n지금 링크 하나 저장해볼까요?",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = LocalFontTheme.current.font
                                    ),
                                    color = LocalColorTheme.current.gray[700]
                                )
                            }
                        }
                        // 2) 추천 모드 + 분류 중
                        showRecs && isRecommending -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LocalColorTheme.current.gray[100])
                                    .padding(top = 65.dp, bottom = 195.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    // TODO: 애니메이션으로 변경
                                    painter = painterResource(R.drawable.ic_recommending),
                                    contentDescription = null,
                                    modifier = Modifier.height(40.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "잠시만 기다려주세요!",
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = LocalFontTheme.current.font
                                    ),
                                    color = LocalColorTheme.current.gray[800]
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "AI가 ${userName}님의 감정과 상황에 맞춰\n추천할 링크를 분류하고 있어요!",
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        fontFamily = LocalFontTheme.current.font
                                    ),
                                    color = LocalColorTheme.current.gray[600]
                                )
                            }
                        }
                        // 3) 일반 모드에서 최근 없음
                        !showRecs && itemsToRender.isEmpty() -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LocalColorTheme.current.gray[100])
                                    .padding(top = 65.dp, bottom = 195.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
//                            Text(
//                                text = "${userName}님이 최근에 열람한 링크",
//                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start),
//                                color = LocalColorTheme.current.black
//                            )
//
//                            Spacer(modifier = Modifier.height(65.dp))

                                Image(
                                    painter = painterResource(R.drawable.ic_no_recents),
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp)
                                )

                                Spacer(modifier = Modifier.height(25.dp))

                                Text(
                                    text = "최근에 열람한 링크가 없어요!",
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = LocalFontTheme.current.font
                                    ),
                                    color = LocalColorTheme.current.gray[800]
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "지금 링크를 둘러볼까요?",
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFamily = LocalFontTheme.current.font
                                    ),
                                    color = LocalColorTheme.current.gray[600]
                                )
                            }
                        }

                        else -> {
                            if (itemsToRender.isEmpty()) {
                                val emptyMsg =
                                    if (showRecs) "지금 마음과 딱 맞는 콘텐츠는 아직 없지만,\n저장된 링크가 늘어날수록 더 나은 추천이 가능해져요." else "최근에 열람한 링크가 없어요!"

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(LocalColorTheme.current.gray[100])
                                        .padding(top = 150.dp, bottom = 217.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = emptyMsg,
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center,
                                            fontFamily = LocalFontTheme.current.font
                                        ),
                                        color = LocalColorTheme.current.gray[600]
                                    )
                                }
                            } else {
                                Text(
                                    text = titleText,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = LocalFontTheme.current.font
                                    ),
                                    color = LocalColorTheme.current.black
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                itemsToRender.forEach { link ->
                                    LinkCard(
                                        link = link,
                                        onClick = { onLinkClick(link.linkuId) }
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                }


            }
//            if (slackFraction > 0f) {
//                item(key = "footer-slack") {
//                    Spacer(
//                        Modifier
//                            .fillMaxWidth()
//                            .fillParentMaxHeight(slackFraction)
//                    )
//                }
//            }
            if (footerHeight > 0.dp) {
                item(key = "footer-slack") {
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(footerHeight)
                    )
                }
            }
        }

        // 클립보드 배너(사용자가 "아래로 밀어서 닫기" 전까지 유지)
        val shouldShowClipboardBanner =
            clipboardUrl != null && clipboardUrl != dismissedClipboardUrl

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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LocalColorTheme.current.gray[100])
            .padding(top = 150.dp, bottom = 217.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "최근에 열람한 링크가 없어요!",
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = LocalFontTheme.current.font),
            color = LocalColorTheme.current.gray[600]
        )
    }
}

@Composable
private fun LinkCard(
    link: LinkSimpleInfo,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(LocalColorTheme.current.white)
            .padding(10.dp)
            .clickable { onClick() }
    ) {
        Box() {
//            Image(
//                painter = painterResource(id = link.imageResId ?: R.drawable.img_default),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(85.dp)
//                    .clip(RoundedCornerShape(12.dp))
//            )
            AsyncImage(
                model = link.linkuImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.img_default),
                fallback    = painterResource(R.drawable.img_default),
                error = painterResource(id = R.drawable.img_default)
            )

            // AI 요약 뱃지
            if (link.aiArticleExists) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_ai_summarize),
                    contentDescription = "AI 요약됨",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(30.dp)
                        .padding(6.dp),
                    tint = Color.Unspecified
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Column {
                Text(
                    text = link.title,
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = LocalFontTheme.current.font),
                    color = LocalColorTheme.current.black
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 태그(옵션): emotionId만 라벨 매핑해서 노출 (categoryId 라벨 없으면 생략)
                val tags = buildList {
                    link.categoryType?.tagName?.let { add(it) }
                    link.emotionType?.tagName?.let { add(it) }
                }

                if (tags.isNotEmpty()) {
                    Row {
                        tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        LocalColorTheme.current.gray[100],
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = tag,
                                    style = TextStyle(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = LocalColorTheme.current.gray[600],
                                        fontFamily = LocalFontTheme.current.font
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(5.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                    Image(
//                        painter = painterResource(id = link.siteIconResId),
//                        contentDescription = "사이트 아이콘",
//                        modifier = Modifier.size(22.dp)
//                    )
                    AsyncImage(
                        model = link.domainImageUrl,
                        contentDescription = "사이트 아이콘",
                        modifier = Modifier.size(22.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.ic_domain_default), // 기본 아이콘 대체
                        fallback = painterResource(R.drawable.ic_domain_default),
                        error = painterResource(R.drawable.ic_domain_default)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = link.domain,
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LocalColorTheme.current.gray[800], fontFamily = LocalFontTheme.current.font)
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewHomeScreen() {
//    HomeScreen(
////        homeViewModel = hiltViewModel(),
//        userName = "세나",
//        showRecommendations = false, // or false
//        recommendedLinks = listOf(
//            LinkSimpleInfo(
//                linkuId = 1L,
//                categoryId = 1L,
//                memo = "",
//                emotionId = 3L,
//                title = "샘플 링크 제목",
//                domain = "naver.com",
//                domainImageUrl = "",
//                linkuImageUrl = ""
//            )
//        ),
//        recentLinks = listOf( // 프리뷰에 recentLinks 전달
//
//        ),
//        isRecommending = false,
//        onRecommendRequest = { _, _, _ -> }, // no-op
//        needMoreForRecommendation = false,
//        onClearNeedMoreNotice = {},
//        jobId = 2L,
//        onLinkClick = { /* no-op in preview */ }
//    )
//}