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
import com.example.design.BrushText
import com.example.design.top.search.SearchBarTopSheet
import com.example.design.modifier.noRippleClickable
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.LocalFontTheme
import com.example.design.theme.color.Basic
import com.example.file.ui.theme.FileTopBarLinkUFont
import com.example.file.ui.theme.MainColor
import com.example.home.HomeViewModel
import com.example.home.R
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
) {
    var showRecs by remember { mutableStateOf(showRecommendations) }
    LaunchedEffect(showRecommendations) { showRecs = showRecommendations }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var isTopBarExpanded by remember { mutableStateOf(true) }
    var selectedEmotion by remember { mutableStateOf<Long?>(null) }
    var selectedTask by remember { mutableStateOf<Long?>(null) }
    var isTopBarLockedCollapsed by remember { mutableStateOf(false) } // 접힘 고정

    val emotionIdMap = mapOf(
        1L to "즐거움",
        2L to "평온",
        3L to "설렘",
        4L to "슬픔",
        5L to "짜증",
        6L to "분노"
    )

    fun emotionLabelToId(label: String?): Long? =
        emotionIdMap.entries.firstOrNull { it.value == label }?.key

    fun emotionIdToLabel(id: Long?): String? = emotionIdMap[id]

//    val taskIdMap = mapOf(
//        1L to "트렌드 확인",
//        2L to "휴식",
//        3L to "집중",
//        4L to "학습",
//        5L to "운동",
//        6L to "업무",
//        7L to "여행",
//        8L to "기타"
//    )

//    fun taskLabelToId(label: String?): Long? =
//        taskIdMap.entries.firstOrNull { it.value == label }?.key
//
//    fun taskIdToLabel(id: Long?): String? = taskIdMap[id]
//
//    var showRecommendations by remember { mutableStateOf(false) }

    var showNeedMoreNotice by remember { mutableStateOf(false) }

    // ✅ 직업별 상황 리스트
    val jobSituations = remember(jobId) { situationsFor(jobId) }

    // ✅ 배지에 찍을 상황 라벨
    val selectedTaskLabel = remember(selectedTask, jobSituations) {
        jobSituations.firstOrNull { it.id == selectedTask }?.name
    }

    // 아이템 개수와 TopBar 접힘 여부에 따라 필요한 여유(화면 비율) 계산
    fun slackFractionFor(count: Int, isExpanded: Boolean): Float = when (count) {
        0 -> 0f
        1 -> if (isExpanded) 0.55f else 0.48f
        2 -> if (isExpanded) 0.40f else 0.33f
        3 -> if (isExpanded) 0.28f else 0.22f
        else -> 0f
    }


//    // 🔹 샘플 링크 데이터 (향후 실제 데이터로 대체)
//    val linkList = remember {  // 데이터가 있는 경우
//        mutableStateOf(
//            listOf(
//                LinkItem(
//                    imageResId = R.drawable.sample_drive,
//                    title = "서울 근교 드라이브 코스 TOP5",
//                    tags = listOf("드라이브", "서울근교"),
//                    siteIconResId = R.drawable.ic_naver,
//                    siteName = "NAVER",
//                    aiSummarized = false
//                ),
//                LinkItem(
//                    imageResId = null,
//                    title = "글램핑 예약, 누구보다 싸게하기",
//                    tags = listOf("여행", "글램핑"),
//                    siteIconResId = R.drawable.ic_naverblog,
//                    siteName = "BLOG",
//                    aiSummarized = true
//                ),
//                LinkItem(
//                    imageResId = R.drawable.sample_drive,
//                    title = "서울 근교 드라이브 코스 TOP5",
//                    tags = listOf("드라이브", "서울근교"),
//                    siteIconResId = R.drawable.ic_naver,
//                    siteName = "NAVER",
//                    aiSummarized = false
//                ),
//                LinkItem(
//                    imageResId = null,
//                    title = "글램핑 예약, 누구보다 싸게하기",
//                    tags = listOf("여행", "글램핑"),
//                    siteIconResId = R.drawable.ic_naverblog,
//                    siteName = "BLOG",
//                    aiSummarized = true
//                ),
//                LinkItem(
//                    imageResId = R.drawable.sample_drive,
//                    title = "서울 근교 드라이브 코스 TOP5",
//                    tags = listOf("드라이브", "서울근교"),
//                    siteIconResId = R.drawable.ic_naver,
//                    siteName = "NAVER",
//                    aiSummarized = false
//                ),
//                LinkItem(
//                    imageResId = null,
//                    title = "글램핑 예약, 누구보다 싸게하기",
//                    tags = listOf("여행", "글램핑"),
//                    siteIconResId = R.drawable.ic_naverblog,
//                    siteName = "BLOG",
//                    aiSummarized = true
//                )
//            )
//        )
//    }
    // 데이터가 없는 경우
//    val linkList = remember { mutableStateOf(listOf()) }

    // 스크롤 변화 감지해서 TopBar 접기/펼치기
//    LaunchedEffect(listState.firstVisibleItemScrollOffset, listState.firstVisibleItemIndex) {
//        isTopBarExpanded =
//            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
//    }
    LaunchedEffect(listState.firstVisibleItemScrollOffset, listState.firstVisibleItemIndex, isTopBarLockedCollapsed) {
        if (!isTopBarLockedCollapsed) {
            isTopBarExpanded =
                listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        } else {
            // 고정 접힘이면 스크롤 상관없이 계속 접힘 유지
            isTopBarExpanded = false
        }
    }

    // String 키 → 서버 ID 매핑 (필요 시 서버 정의에 맞춰 숫자만 바꾸면 됨)
//    fun emotionKeyToId(key: String?): Long? = when (key) {
//        "joy" -> 1L
//        "calm" -> 2L
//        "excitement" -> 3L
//        "sadness" -> 4L
//        "irritation" -> 5L
//        "anger" -> 6L
//        else -> null
//    }
//    fun taskNameToSituationId(task: String?): Long? = when (task) {
//        "트렌드 확인" -> 1L
//        "과제 중"   -> 2L
//        "쇼핑 중"   -> 3L
//        "데이트 중" -> 4L
//        "통학 중"   -> 5L
//        "알바 중"   -> 6L
//        "휴식 중"   -> 7L
//        "자기 전"   -> 8L
//        else -> null
//    }

//    // 추천 버튼 클릭 → ID 매핑해서 상위 콜백 호출
//    val onRecommendClick: () -> Unit = {
//        if (selectedEmotion != null && selectedTask != null) {
//            onRecommendRequest(selectedEmotion!!, selectedTask!!, 10)
//            showRecs = true
//            isTopBarExpanded = false
//            coroutineScope.launch { listState.animateScrollToItem(1) }
//        }
//    }
    // 추천 버튼 클릭 → 사전검증(링크 3개 미만이면 안내만), 아니면 API 호출
    // ✅ 개수 체크 제거: 에러 핸들링은 ViewModel이 함
    val onRecommendClick: () -> Unit = {
        if (selectedEmotion != null && selectedTask != null) {
            onClearNeedMoreNotice() // 이전 안내 끄기
            onRecommendRequest(selectedEmotion!!, selectedTask!!, 10)
            showRecs = true
            isTopBarLockedCollapsed = true
            isTopBarExpanded = false
            coroutineScope.launch { listState.animateScrollToItem(1) }
        }
    }
    val itemsToRender = if (showRecs) recommendedLinks else recentLinks
    val titleText = if (showRecs) "세나님의 오늘에 어울리는 콘텐츠예요!"
    else "${userName}님이 최근에 열람한 링크"
    // 비율 계산은 remember로 한 번 더 안정화해도 OK
    val slackFraction = remember(itemsToRender.size, isTopBarExpanded) {
        slackFractionFor(itemsToRender.size, isTopBarExpanded)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.gray[100]),
        state = listState
    ) {
        stickyHeader {
            TopBar(
                homeViewModel = homeViewModel ,
                isExpanded = isTopBarExpanded,
                selectedEmotion = selectedEmotion,
                selectedTask = selectedTask,
                onEmotionChange = { id -> selectedEmotion = id },
                onTaskChange = { id -> selectedTask = id },
                onExpandRequest = {
                    // ▼ 아래 화살표 클릭 시: 추천 배지/멘트 숨기고 선택 UI 다시 보이게
                    showRecs = false          // 배지/추천 모드 끄기
                    selectedEmotion = null     // 감정 선택 초기화
                    selectedTask = null        // 상황 선택 초기화
                    onClearNeedMoreNotice()  // 문구 초기화

                    isTopBarLockedCollapsed = false  // 고정 해제
                    isTopBarExpanded = true    // 상단 영역 펼치기
                    coroutineScope.launch { listState.animateScrollToItem(0) } // 맨 위로
                },
                userName = userName,
                recommendEnabled = (selectedEmotion != null && selectedTask != null && !isRecommending),
                onRecommendClick = onRecommendClick,
                showRecommendations = showRecs,
                selectedEmotionLabel = emotionIdToLabel(selectedEmotion),
                selectedTaskLabel = selectedTaskLabel,
                situations = jobSituations
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
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = LocalFontTheme.current.font),
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
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = LocalFontTheme.current.font),
                                color = LocalColorTheme.current.gray[800]
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "AI가 ${userName}님의 감정과 상황에 맞춰\n추천할 링크를 분류하고 있어요!",
                                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, textAlign = TextAlign.Center, fontFamily = LocalFontTheme.current.font),
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
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = LocalFontTheme.current.font),
                                color = LocalColorTheme.current.gray[800]
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "지금 링크를 둘러볼까요?",
                                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
                                color = LocalColorTheme.current.gray[600]
                            )
                        }
                    }
                    else -> {
                        if (itemsToRender.isEmpty()) {
                            val emptyMsg = if (showRecs) "지금 마음과 딱 맞는 콘텐츠는 아직 없지만,\n저장된 링크가 늘어날수록 더 나은 추천이 가능해져요." else "최근에 열람한 링크가 없어요!"

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
                                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, fontFamily = LocalFontTheme.current.font),
                                    color = LocalColorTheme.current.gray[600]
                                )
                            }
                        } else {
                            Text(
                                text = titleText,
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = LocalFontTheme.current.font),
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
        if (slackFraction > 0f) {
            item(key = "footer-slack") {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight(slackFraction)
                )
            }
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
        recentQuerys = homeViewModel.recentQueryList.collectAsState().value.map{it.text}
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

@Composable
fun TopBar(
    homeViewModel: HomeViewModel,
    isExpanded: Boolean,
    selectedEmotion: Long?,
    selectedTask: Long?,
    onEmotionChange: (Long?) -> Unit,
    onTaskChange: (Long?) -> Unit,
    onExpandRequest: () -> Unit,
    userName: String,
    recommendEnabled: Boolean,
    onRecommendClick: () -> Unit,
    showRecommendations: Boolean,
    selectedEmotionLabel: String?,
    selectedTaskLabel: String?,
    situations: List<Situation>,
) {
//    val deactive_maincolor = Brush.horizontalGradient(
//        listOf(
//            Color(0xFF2C6FFF).copy(alpha = 0.2f),
//            Color(0xFFC800FF).copy(alpha = 0.2f)
//        )
//    )

    val buttonBrush =
        if (recommendEnabled) Basic.maincolor
        else Brush.horizontalGradient(
            listOf(
                Color(0xFF2C6FFF).copy(alpha = 0.2f),
                Color(0xFFC800FF).copy(alpha = 0.2f)
            )
        )

    val selectedBrush = Brush.horizontalGradient(
        listOf(
            Color(0xFF2C6FFF).copy(alpha = 0.2f),
            Color(0xFFC800FF).copy(alpha = 0.2f)
        )
    )

    var isNoticeExist by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .clip(
                RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
            )
            .background(LocalColorTheme.current.white)
            .padding(bottom = if (!isExpanded) 5.dp else 0.dp) // 하단 여백 확보
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 상단 로고 및 알림 아이콘
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.38.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 19.dp),
                    // 텍스트(그라데이션 및 스타일 지정)
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontSize = 24.sp,

                                // 사용할 폰트 (태백 폰트)
                                fontFamily = FileTopBarLinkUFont,

                                fontWeight = FontWeight.Normal,

                                // 텍스트 그라데이션 색상(링큐 메인 색상)
                                brush = MainColor,
                            )
                        ) {
                            // 실제 표시할 텍스트
                            append("링큐")
                        }
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .padding(end = 13.8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = Res.drawable.ic_alarm),
                        contentDescription = null,
                        tint = LocalColorTheme.current.gray[300]
                    )

                    if (isNoticeExist) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 3.8.dp, y = (-3.38).dp)
                                .background(LocalColorTheme.current.negative, shape = RoundedCornerShape(50))
                                .border(
                                    width = 3.dp,
                                    color = LocalColorTheme.current.white,
                                    shape = RoundedCornerShape(50)
                                )
                                .zIndex(1f)
                        )
                    }
                }
            }

            // 빠른 링크 검색
            Box(
                modifier = Modifier
                    .padding(top = 15.dp, start = 16.dp, end = 16.dp)
                    .height(48.dp)
                    .noRippleClickable{
                        homeViewModel.updateSearchTopSheetVisible(true)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp)) // 모서리 둥글게
                        .background(brush = Basic.maincolor) // 그라데이션 Brush 적용
                        .padding(horizontal = 18.51.dp, vertical = 15.dp) // 내부 여백
                    ,
                    // 가로 정렬: 요소 간 13dp 간격, 왼쪽부터 배치
                    horizontalArrangement = Arrangement.spacedBy(13.dp, Alignment.Start),

                    // 세로 정렬: 세로 중앙 정렬
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logo_white),
                        contentDescription = null,
//                        modifier = Modifier
//                            .height(17.dp),
                        tint = LocalColorTheme.current.white
                    )

                    Text(
                        text = "빠른 링크 검색",
                        color = LocalColorTheme.current.white,
                        fontFamily = LocalFontTheme.current.font,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(500),
                    )
                }
            }

//            // 토글 되는 부분
//            AnimatedVisibility(
//                visible = isExpanded,
//                enter = expandVertically(),
//                exit = shrinkVertically()
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 19.dp)
//                ) {
//                    Column {
//                        Text(
//                            text = "${userName}님의 감정과 상황을 알려주세요!",
//                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
//                            color = LocalColorTheme.current.black,
//                            modifier = Modifier.padding(start = 20.dp, end = 21.dp)
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(22.dp))
//
//                    Column {
//                        Box {
//                            Text(
//                                text = "오늘의 감정은 어때요?",
//                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
//                                color = LocalColorTheme.current.gray[700],
//                                modifier = Modifier.padding(start = 20.dp, end = 21.dp)
//                            )
//                        }
//
//                        Spacer(modifier = Modifier.height(15.dp))
//
//                        EmotionSelector(selectedEmotion, onEmotionChange)
////                        Row {
////                            // 6가지 감정 선택 버튼
////                            Box(
////                                modifier = Modifier
////                                    .size(56.dp)
////                                    .clip(RoundedCornerShape(18.dp))
////                                    .background(LocalColorTheme.current.gray[100])
////                                    .padding(12.dp, 14.dp)
////                            ) {
////                                Text(
////                                    text = "\uD83D\uDE00",  // 😃
////                                    style = TextStyle(fontSize = 25.5.sp, fontWeight = FontWeight.Medium)
////                                )
////                            }
////
////                            Spacer(modifier = Modifier.width(7.dp))
////
////                            Box(
////                                modifier = Modifier
////                                    .size(56.dp)
////                                    .clip(RoundedCornerShape(18.dp))
////                                    .background(LocalColorTheme.current.gray[100])
////                                    .padding(12.dp, 14.dp)
////                            ) {
////                                Text(
////                                    text = "\uD83D\uDE10",  // 😐
////                                    style = TextStyle(fontSize = 25.5.sp, fontWeight = FontWeight.Medium)
////                                )
////                            }
////
////                            Spacer(modifier = Modifier.width(7.dp))
////
////                            Box(
////                                modifier = Modifier
////                                    .size(56.dp)
////                                    .clip(RoundedCornerShape(18.dp))
////                                    .background(LocalColorTheme.current.gray[100])
////                                    .padding(12.dp, 14.dp)
////                            ) {
////                                Text(
////                                    text = "\uD83D\uDE0D",  // 😍
////                                    style = TextStyle(fontSize = 25.5.sp, fontWeight = FontWeight.Medium)
////                                )
////                            }
////
////                            Spacer(modifier = Modifier.width(7.dp))
////
////                            Box(
////                                modifier = Modifier
////                                    .size(56.dp)
////                                    .clip(RoundedCornerShape(18.dp))
////                                    .background(LocalColorTheme.current.gray[100])
////                                    .padding(12.dp, 14.dp)
////                            ) {
////                                Text(
////                                    text = "\uD83E\uDD72",  // 🥲
////                                    style = TextStyle(fontSize = 25.5.sp, fontWeight = FontWeight.Medium)
////                                )
////                            }
////
////                            Spacer(modifier = Modifier.width(7.dp))
////
////                            Box(
////                                modifier = Modifier
////                                    .size(56.dp)
////                                    .clip(RoundedCornerShape(18.dp))
////                                    .background(LocalColorTheme.current.gray[100])
////                                    .padding(12.dp, 14.dp)
////                            ) {
////                                Text(
////                                    text = "\uD83D\uDE2B",  // 😫
////                                    style = TextStyle(fontSize = 25.5.sp, fontWeight = FontWeight.Medium)
////                                )
////                            }
////
////                            Spacer(modifier = Modifier.width(7.dp))
////
////                            Box(
////                                modifier = Modifier
////                                    .size(56.dp)
////                                    .clip(RoundedCornerShape(18.dp))
////                                    .background(LocalColorTheme.current.gray[100])
////                                    .padding(12.dp, 14.dp)
////                            ) {
////                                Text(
////                                    text = "\uD83D\uDE21",  // 😡
////                                    style = TextStyle(fontSize = 25.5.sp, fontWeight = FontWeight.Medium)
////                                )
////                            }
////                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(18.dp))
//
//                    Column {
//                        Box {
//                            Text(
//                                text = "지금 뭐하는 중이에요?",
//                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
//                                color = LocalColorTheme.current.gray[700],
//                                modifier = Modifier.padding(start = 20.dp, end = 21.dp)
//                            )
//                        }
//
//                        Spacer(modifier = Modifier.height(14.dp))
//
//                        TaskSelector(selectedTask, onTaskChange)
////                        Column {
////                            Row(
////                                modifier = Modifier.padding(start = 30.dp, end = 29.dp)
////                            ) {
////                                // 윗줄 4가지 할 일 선택 버튼
////                                Box(
////                                    modifier = Modifier
////                                        .clip(RoundedCornerShape(10.dp))
////                                        .background(LocalColorTheme.current.gray[100])
////                                        .padding(15.dp, 10.5.dp)
////                                ) {
////                                    Text(
////                                        text = "영어 공부 중",
////                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
////                                    )
////                                }
////
////                                Spacer(modifier = Modifier.width(10.dp))
////
////                                Box(
////                                    modifier = Modifier
////                                        .clip(RoundedCornerShape(10.dp))
////                                        .background(LocalColorTheme.current.gray[100])
////                                        .padding(15.dp, 10.5.dp)
////                                ) {
////                                    Text(
////                                        text = "퇴근 중",
////                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
////                                    )
////                                }
////
////                                Spacer(modifier = Modifier.width(10.dp))
////
////                                Box(
////                                    modifier = Modifier
////                                        .clip(RoundedCornerShape(10.dp))
////                                        .background(LocalColorTheme.current.gray[100])
////                                        .padding(15.dp, 10.5.dp)
////                                ) {
////                                    Text(
////                                        text = "쇼핑 중",
////                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
////                                    )
////                                }
////
////                                Spacer(modifier = Modifier.width(10.dp))
////
////                                Box(
////                                    modifier = Modifier
////                                        .clip(RoundedCornerShape(10.dp))
////                                        .background(LocalColorTheme.current.gray[100])
////                                        .padding(15.dp, 10.5.dp)
////                                ) {
////                                    Text(
////                                        text = "데이트 중",
////                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
////                                    )
////                                }
////                            }
////
////                            Spacer(modifier = Modifier.height(8.dp))
////
////                            Row(
////                                modifier = Modifier.padding(horizontal = 37.dp)
////                            ) {
////                                // 아랫줄 4가지 할 일 선택 버튼
////                                Box(
////                                    modifier = Modifier
////                                        .clip(RoundedCornerShape(10.dp))
////                                        .background(LocalColorTheme.current.gray[100])
////                                        .padding(15.dp, 10.5.dp)
////                                ) {
////                                    Text(
////                                        text = "통학 중",
////                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
////                                    )
////                                }
////
////                                Spacer(modifier = Modifier.width(10.dp))
////
////                                Box(
////                                    modifier = Modifier
////                                        .clip(RoundedCornerShape(10.dp))
////                                        .background(LocalColorTheme.current.gray[100])
////                                        .padding(15.dp, 10.5.dp)
////                                ) {
////                                    Text(
////                                        text = "요리 중",
////                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
////                                    )
////                                }
////
////                                Spacer(modifier = Modifier.width(10.dp))
////
////                                Box(
////                                    modifier = Modifier
////                                        .clip(RoundedCornerShape(10.dp))
////                                        .background(LocalColorTheme.current.gray[100])
////                                        .padding(15.dp, 10.5.dp)
////                                ) {
////                                    Text(
////                                        text = "드라이브 중",
////                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
////                                    )
////                                }
////
////                                Spacer(modifier = Modifier.width(10.dp))
////
////                                Box(
////                                    modifier = Modifier
////                                        .clip(RoundedCornerShape(10.dp))
////                                        .background(LocalColorTheme.current.gray[100])
////                                        .padding(15.dp, 10.5.dp)
////                                ) {
////                                    Text(
////                                        text = "야근 중",
////                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
////                                    )
////                                }
////                            }
////                        }
//                    }
//
//                    // 링크 추천 버튼
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(start = 96.dp, end = 96.dp, top = 15.dp, bottom = 20.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .width(220.dp)
//                                .height(48.dp)
//                                .clip(RoundedCornerShape(16.dp))
//                                .background(brush = buttonBrush)
//                                .clickable(enabled = recommendEnabled) { onRecommendClick() },
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                text = "링크 추천해줘!",
//                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center),
//                                color = LocalColorTheme.current.white
//                            )
//                        }
//                    }
//                }
//            }
            if (showRecommendations) {
                // 추천 모드: 선택 UI 숨기고 배지 요약만 표시
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 0.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val emotionIconMap = mapOf(
                            "즐거움" to R.drawable.ic_joy,
                            "평온" to R.drawable.ic_calm,
                            "설렘" to R.drawable.ic_excite,
                            "슬픔" to R.drawable.ic_sad,
                            "짜증" to R.drawable.ic_irritation,
                            "분노" to R.drawable.ic_anger
                        )

                        // 감정 배지
                        selectedEmotionLabel?.let { label ->
                            val iconRes = emotionIconMap[label]
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(brush = selectedBrush)
                                    .border(1.dp, brush = Basic.maincolor, RoundedCornerShape(10.dp))
                                    .padding(4.57.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    iconRes?.let {
                                        Image(
                                            painter = painterResource(id = it),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp) // 아이콘 크기 조정
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // 상황 배지
                        selectedTaskLabel?.let {label ->
                            Box(
                                modifier = Modifier
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(brush = selectedBrush)
                                    .border(1.dp, brush = Basic.maincolor, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 15.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                BrushText(
                                    text = label,
                                    brush = Basic.maincolor,
                                    color = LocalColorTheme.current.gray[700],
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFamily = LocalFontTheme.current.font
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                // 기존: 선택 UI + 추천 버튼
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 19.dp)
                    ) {
                        Column {
                            Text(
                                text = "${userName}님의 감정과 상황을 알려주세요!",
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = LocalFontTheme.current.font),
                                color = LocalColorTheme.current.black,
                                modifier = Modifier.padding(start = 20.dp, end = 21.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        Column {
                            Box {
                                Text(
                                    text = "오늘의 감정은 어때요?",
                                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = LocalFontTheme.current.font),
                                    color = LocalColorTheme.current.gray[700],
                                    modifier = Modifier.padding(start = 20.dp, end = 21.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                            EmotionSelector(selectedEmotion, onEmotionChange)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Column {
                            Box {
                                Text(
                                    text = "지금 뭐하는 중이에요?",
                                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = LocalFontTheme.current.font),
                                    color = LocalColorTheme.current.gray[700],
                                    modifier = Modifier.padding(start = 20.dp, end = 21.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            TaskSelector(
                                selectedTask = selectedTask,
                                onTaskChange = onTaskChange,
                                situations = situations
                            )
                        }

                        // 링크 추천 버튼
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 96.dp, end = 96.dp, top = 15.dp, bottom = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .width(220.dp)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(brush = buttonBrush)
                                    .clickable(enabled = recommendEnabled) { onRecommendClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "링크 추천해줘!",
                                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, fontFamily = LocalFontTheme.current.font),
                                    color = LocalColorTheme.current.white
                                )
                            }
                        }
                    }
                }
            }

            // 화살표 버튼 (접힌 상태에서만 표시)
            if (!isExpanded) {
                Spacer(modifier = Modifier.height(2.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    IconButton(onClick = onExpandRequest) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_down_arrow),
                            contentDescription = "펼치기",
                            modifier = Modifier.width(42.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmotionSelector(
    selectedEmotionId: Long?,
    onEmotionChange: (Long?) -> Unit
) {
    val emotions = listOf(
        R.drawable.ic_joy,
        R.drawable.ic_calm,
        R.drawable.ic_excite,
        R.drawable.ic_sad,
        R.drawable.ic_irritation,
        R.drawable.ic_anger
    )

    val emotionIds = listOf(1L, 2L, 3L, 4L, 5L, 6L)

    // 감정의 고유 key (이모지 대신 리소스 ID를 String으로)
    val emotionKeys = listOf(
        "joy", "calm", "excitement", "sadness", "irritation", "anger"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        emotions.forEachIndexed { idx, resId ->
            val id = emotionIds[idx]
            val isSelected = selectedEmotionId == id

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isSelected) LocalColorTheme.current.blue[50] else LocalColorTheme.current.gray[100]
                    )
                    .then(
                        if (isSelected) Modifier.border(
                            width = 1.dp,
                            brush = Basic.maincolor,
                            shape = RoundedCornerShape(18.dp)
                        ) else Modifier
                    )
                    .padding(8.dp)
                    .clickable {
                        onEmotionChange(if (isSelected) null else id)
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = null, // 감정 이름 필요하면 emotionKeys[idx] 등 넣어도 됨
                    modifier = Modifier.size(35.dp)
                )
            }

            Spacer(modifier = Modifier.width(7.dp))
        }
    }
}

@Composable
fun TaskSelector(
    selectedTask: Long?,
    onTaskChange: (Long?) -> Unit,
    situations: List<Situation>,
) {
//    val tasks = listOf("트렌드 확인", "과제 중", "쇼핑 중", "데이트 중", "통학 중", "알바 중", "휴식 중", "자기 전")
//
//    val taskIds = (1L..8L).toList()
    val firstRow = remember(situations) { situations.take(4) }
    val secondRow = remember(situations) { situations.drop(4) }

    Column {
        // 첫 줄: 4개
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
//            tasks.take(4).forEachIndexed { idx, task ->
//                val id = taskIds[idx]
//                val isSelected = selectedTaskId == id
//
//                Box(
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(10.dp))
//                        .background(
//                            if (isSelected) LocalColorTheme.current.purple[50] else LocalColorTheme.current.gray[100]
//                        )
//                        .then(
//                            if (isSelected) Modifier.border(
//                                width = 1.dp,
//                                brush = Basic.maincolor,
//                                shape = RoundedCornerShape(10.dp)
//                            ) else Modifier
//                        )
//                        .clickable {
//                            onTaskChange(if (isSelected) null else id)
//                        }
//                        .padding(horizontal = 15.dp, vertical = 10.5.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = task,
//                        style = TextStyle(
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = if (isSelected) LocalColorTheme.current.black else LocalColorTheme.current.gray[800]
//                        )
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(10.dp))
//            }
            firstRow.forEach { s ->
                val isSelected = selectedTask == s.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) LocalColorTheme.current.purple[50] else LocalColorTheme.current.gray[100]
                        )
                        .then(
                            if (isSelected) Modifier.border(
                                width = 1.dp,
                                brush = Basic.maincolor,
                                shape = RoundedCornerShape(10.dp)
                            ) else Modifier
                        )
                        .clickable { onTaskChange(if (isSelected) null else s.id) }
                        .padding(horizontal = 15.dp, vertical = 10.5.dp),
                    contentAlignment = Alignment.Center
                ) {
//                    Text(
//                        text = s.name,
//                        style = TextStyle(
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = if (isSelected) LocalColorTheme.current.black else LocalColorTheme.current.gray[800],
//                            fontFamily = LocalFontTheme.current.font
//                        )
//                    )
                    if (isSelected) {
                        BrushText(
                            text = s.name,
                            brush = Basic.maincolor, // 선택 시 그라데이션
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = LocalFontTheme.current.font
                            )
                        )
                    } else {
                        Text(
                            text = s.name,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = LocalColorTheme.current.gray[800],
                                fontFamily = LocalFontTheme.current.font
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 두 번째 줄: 나머지 4개
        Row(
            modifier = Modifier
                .padding(start = 17.dp, end = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
//            tasks.drop(4).forEachIndexed { idx, label ->
//                val id = taskIds[idx + 4]
//                val isSelected = selectedTaskId == id
//
//                Box(
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(10.dp))
//                        .background(
//                            if (isSelected) LocalColorTheme.current.purple[50] else LocalColorTheme.current.gray[100]
//                        )
//                        .then(
//                            if (isSelected) Modifier.border(
//                                width = 1.dp,
//                                brush = Basic.maincolor,
//                                shape = RoundedCornerShape(10.dp)
//                            ) else Modifier
//                        )
//                        .clickable {
//                            onTaskChange(if (isSelected) null else id)
//                        }
//                        .padding(horizontal = 15.dp, vertical = 10.5.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = label,
//                        style = TextStyle(
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = if (isSelected) LocalColorTheme.current.black else LocalColorTheme.current.gray[800]
//                        )
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(10.dp))
//            }
            secondRow.forEach { s ->
                val isSelected = selectedTask == s.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) LocalColorTheme.current.purple[50] else LocalColorTheme.current.gray[100]
                        )
                        .then(
                            if (isSelected) Modifier.border(
                                width = 1.dp,
                                brush = Basic.maincolor,
                                shape = RoundedCornerShape(10.dp)
                            ) else Modifier
                        )
                        .clickable { onTaskChange(if (isSelected) null else s.id) }
                        .padding(horizontal = 15.dp, vertical = 10.5.dp),
                    contentAlignment = Alignment.Center
                ) {
//                    Text(
//                        text = s.name,
//                        style = TextStyle(
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = if (isSelected) LocalColorTheme.current.black else LocalColorTheme.current.gray[800],
//                            fontFamily = LocalFontTheme.current.font
//                        )
//                    )
                    if (isSelected) {
                        BrushText(
                            text = s.name,
                            brush = Basic.maincolor, // ✅ 선택 시 그라데이션
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = LocalFontTheme.current.font
                            )
                        )
                    } else {
                        Text(
                            text = s.name,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = LocalColorTheme.current.gray[800],
                                fontFamily = LocalFontTheme.current.font
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
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