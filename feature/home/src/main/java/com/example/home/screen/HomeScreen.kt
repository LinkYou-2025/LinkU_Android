package com.example.home.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
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

@Composable
fun HomeScreen(
    userName: String
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var isTopBarExpanded by remember { mutableStateOf(true) }
    var selectedEmotion by remember { mutableStateOf<String?>(null) }
    var selectedTask by remember { mutableStateOf<String?>(null) }

    // 🔹 샘플 링크 데이터 (향후 실제 데이터로 대체)
    // 예: listOf("https://example.com", "https://another.com")
    val linkList = remember {  // 데이터가 있는 경우
        mutableStateOf(
            listOf(
                LinkItem(
                    imageResId = R.drawable.sample_drive,
                    title = "서울 근교 드라이브 코스 TOP5",
                    tags = listOf("드라이브", "서울근교"),
                    siteIconResId = R.drawable.ic_naver,
                    siteName = "NAVER",
                    aiSummarized = false
                ),
                LinkItem(
                    imageResId = null,
                    title = "글램핑 예약, 누구보다 싸게하기",
                    tags = listOf("여행", "글램핑"),
                    siteIconResId = R.drawable.ic_naverblog,
                    siteName = "BLOG",
                    aiSummarized = true
                )
            )
        )
    }
    // 데이터가 없는 경우
//    val linkList = remember { mutableStateOf(listOf()) }

    // 스크롤 변화 감지해서 TopBar 접기/펼치기
    LaunchedEffect(listState.firstVisibleItemScrollOffset, listState.firstVisibleItemIndex) {
        isTopBarExpanded =
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.gray[100]),
        state = listState
    ) {
        item {
            TopBar(
                isExpanded = isTopBarExpanded,
                selectedEmotion = selectedEmotion,
                selectedTask = selectedTask,
                onEmotionChange = { selectedEmotion = it },
                onTaskChange = { selectedTask = it },
                onExpandRequest = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                    isTopBarExpanded = true
                },
                userName = userName
            )
        }

        item {
            Column(
                modifier = Modifier.padding(20.dp, 24.dp)
            ) {

                // AI 추천 분류 개발 필요 (멘트는 "세나님의 오늘에 어울리는 콘텐츠예요")
                if (linkList.value.isEmpty()) {
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
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                            color = LocalColorTheme.current.gray[600]
                        )
                    }
                } else {
                    Text(
                        text = "${userName}님이 최근에 열람한 링크",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        color = LocalColorTheme.current.black
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    linkList.value.forEach { link ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(LocalColorTheme.current.white)
                                .padding(10.dp)
                        ) {
                            Box() {
                                Image(
                                    painter = painterResource(id = link.imageResId ?: R.drawable.img_default),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(85.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                // AI 요약 뱃지
                                if (link.aiSummarized) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_ai_summarize),
                                        contentDescription = "AI 요약됨",
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(20.dp)
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
                                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
                                        color = LocalColorTheme.current.black
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
//
                                    Row {
                                        link.tags.forEach { tag ->
                                            Box(
                                                modifier = Modifier
                                                    .background(LocalColorTheme.current.gray[100], RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = "$tag",
                                                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[600])
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(5.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
//
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = link.siteIconResId),
                                            contentDescription = "사이트 아이콘",
                                            modifier = Modifier.size(22.dp)
                                        )

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Text(
                                            text = link.siteName,
                                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LocalColorTheme.current.gray[800])
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar(
    isExpanded: Boolean,
    selectedEmotion: String?,
    selectedTask: String?,
    onEmotionChange: (String?) -> Unit,
    onTaskChange: (String?) -> Unit,
    onExpandRequest: () -> Unit,
    userName: String
) {
//    val deactive_maincolor = Brush.horizontalGradient(
//        listOf(
//            Color(0xFF2C6FFF).copy(alpha = 0.2f),
//            Color(0xFFC800FF).copy(alpha = 0.2f)
//        )
//    )

    val buttonBrush =
        if (selectedEmotion != null && selectedTask != null) Basic.maincolor
        else Brush.horizontalGradient(
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
            .padding(bottom = if (!isExpanded) 13.5.dp else 0.dp) // 하단 여백 확보
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 상단 로고 및 알림 아이콘
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.38.dp, start = 16.dp, end = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = Res.drawable.ic_linkukor),
                    contentDescription = null,
                    modifier = Modifier
                        .height(24.dp)
                        .padding(start = 19.dp)
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
            Row(
                modifier = Modifier
                    .padding(top = 15.dp, start = 16.dp, end = 16.dp)
                    .height(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp)) // 모서리 둥글게
                        .background(brush = Basic.maincolor) // 그라데이션 Brush 적용
                        .padding(horizontal = 18.51.dp, vertical = 15.dp) // 내부 여백
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logo_white),
                        contentDescription = null,
                        modifier = Modifier
                            .height(17.dp),
                        tint = LocalColorTheme.current.white
                    )

                    Text(
                        text = "빠른 링크 검색",
                        color = LocalColorTheme.current.white,
                        modifier = Modifier
                            .padding(start = 36.98.dp)
                    )
                }
            }

            // 화살표 버튼 (접힌 상태에서만 표시)
            if (!isExpanded) {
                Spacer(modifier = Modifier.height(14.dp))

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

            // 토글 되는 부분
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
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                            color = LocalColorTheme.current.black,
                            modifier = Modifier.padding(start = 20.dp, end = 21.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Column {
                        Box {
                            Text(
                                text = "오늘의 감정은 어때요?",
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
                                color = LocalColorTheme.current.gray[700],
                                modifier = Modifier.padding(start = 20.dp, end = 21.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(15.dp))

                        EmotionSelector(selectedEmotion, onEmotionChange)
//                        Row {
//                            // 6가지 감정 선택 버튼
//                            Box(
//                                modifier = Modifier
//                                    .size(56.dp)
//                                    .clip(RoundedCornerShape(18.dp))
//                                    .background(LocalColorTheme.current.gray[100])
//                                    .padding(12.dp, 14.dp)
//                            ) {
//                                Text(
//                                    text = "\uD83D\uDE00",  // 😃
//                                    style = TextStyle(fontSize = 25.5.sp, fontWeight = FontWeight.Medium)
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.width(7.dp))
//
//                            Box(
//                                modifier = Modifier
//                                    .size(56.dp)
//                                    .clip(RoundedCornerShape(18.dp))
//                                    .background(LocalColorTheme.current.gray[100])
//                                    .padding(12.dp, 14.dp)
//                            ) {
//                                Text(
//                                    text = "\uD83D\uDE10",  // 😐
//                                    style = TextStyle(fontSize = 25.5.sp, fontWeight = FontWeight.Medium)
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.width(7.dp))
//
//                            Box(
//                                modifier = Modifier
//                                    .size(56.dp)
//                                    .clip(RoundedCornerShape(18.dp))
//                                    .background(LocalColorTheme.current.gray[100])
//                                    .padding(12.dp, 14.dp)
//                            ) {
//                                Text(
//                                    text = "\uD83D\uDE0D",  // 😍
//                                    style = TextStyle(fontSize = 25.5.sp, fontWeight = FontWeight.Medium)
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.width(7.dp))
//
//                            Box(
//                                modifier = Modifier
//                                    .size(56.dp)
//                                    .clip(RoundedCornerShape(18.dp))
//                                    .background(LocalColorTheme.current.gray[100])
//                                    .padding(12.dp, 14.dp)
//                            ) {
//                                Text(
//                                    text = "\uD83E\uDD72",  // 🥲
//                                    style = TextStyle(fontSize = 25.5.sp, fontWeight = FontWeight.Medium)
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.width(7.dp))
//
//                            Box(
//                                modifier = Modifier
//                                    .size(56.dp)
//                                    .clip(RoundedCornerShape(18.dp))
//                                    .background(LocalColorTheme.current.gray[100])
//                                    .padding(12.dp, 14.dp)
//                            ) {
//                                Text(
//                                    text = "\uD83D\uDE2B",  // 😫
//                                    style = TextStyle(fontSize = 25.5.sp, fontWeight = FontWeight.Medium)
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.width(7.dp))
//
//                            Box(
//                                modifier = Modifier
//                                    .size(56.dp)
//                                    .clip(RoundedCornerShape(18.dp))
//                                    .background(LocalColorTheme.current.gray[100])
//                                    .padding(12.dp, 14.dp)
//                            ) {
//                                Text(
//                                    text = "\uD83D\uDE21",  // 😡
//                                    style = TextStyle(fontSize = 25.5.sp, fontWeight = FontWeight.Medium)
//                                )
//                            }
//                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Column {
                        Box {
                            Text(
                                text = "지금 뭐하는 중이에요?",
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
                                color = LocalColorTheme.current.gray[700],
                                modifier = Modifier.padding(start = 20.dp, end = 21.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        TaskSelector(selectedTask, onTaskChange)
//                        Column {
//                            Row(
//                                modifier = Modifier.padding(start = 30.dp, end = 29.dp)
//                            ) {
//                                // 윗줄 4가지 할 일 선택 버튼
//                                Box(
//                                    modifier = Modifier
//                                        .clip(RoundedCornerShape(10.dp))
//                                        .background(LocalColorTheme.current.gray[100])
//                                        .padding(15.dp, 10.5.dp)
//                                ) {
//                                    Text(
//                                        text = "영어 공부 중",
//                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
//                                    )
//                                }
//
//                                Spacer(modifier = Modifier.width(10.dp))
//
//                                Box(
//                                    modifier = Modifier
//                                        .clip(RoundedCornerShape(10.dp))
//                                        .background(LocalColorTheme.current.gray[100])
//                                        .padding(15.dp, 10.5.dp)
//                                ) {
//                                    Text(
//                                        text = "퇴근 중",
//                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
//                                    )
//                                }
//
//                                Spacer(modifier = Modifier.width(10.dp))
//
//                                Box(
//                                    modifier = Modifier
//                                        .clip(RoundedCornerShape(10.dp))
//                                        .background(LocalColorTheme.current.gray[100])
//                                        .padding(15.dp, 10.5.dp)
//                                ) {
//                                    Text(
//                                        text = "쇼핑 중",
//                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
//                                    )
//                                }
//
//                                Spacer(modifier = Modifier.width(10.dp))
//
//                                Box(
//                                    modifier = Modifier
//                                        .clip(RoundedCornerShape(10.dp))
//                                        .background(LocalColorTheme.current.gray[100])
//                                        .padding(15.dp, 10.5.dp)
//                                ) {
//                                    Text(
//                                        text = "데이트 중",
//                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
//                                    )
//                                }
//                            }
//
//                            Spacer(modifier = Modifier.height(8.dp))
//
//                            Row(
//                                modifier = Modifier.padding(horizontal = 37.dp)
//                            ) {
//                                // 아랫줄 4가지 할 일 선택 버튼
//                                Box(
//                                    modifier = Modifier
//                                        .clip(RoundedCornerShape(10.dp))
//                                        .background(LocalColorTheme.current.gray[100])
//                                        .padding(15.dp, 10.5.dp)
//                                ) {
//                                    Text(
//                                        text = "통학 중",
//                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
//                                    )
//                                }
//
//                                Spacer(modifier = Modifier.width(10.dp))
//
//                                Box(
//                                    modifier = Modifier
//                                        .clip(RoundedCornerShape(10.dp))
//                                        .background(LocalColorTheme.current.gray[100])
//                                        .padding(15.dp, 10.5.dp)
//                                ) {
//                                    Text(
//                                        text = "요리 중",
//                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
//                                    )
//                                }
//
//                                Spacer(modifier = Modifier.width(10.dp))
//
//                                Box(
//                                    modifier = Modifier
//                                        .clip(RoundedCornerShape(10.dp))
//                                        .background(LocalColorTheme.current.gray[100])
//                                        .padding(15.dp, 10.5.dp)
//                                ) {
//                                    Text(
//                                        text = "드라이브 중",
//                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
//                                    )
//                                }
//
//                                Spacer(modifier = Modifier.width(10.dp))
//
//                                Box(
//                                    modifier = Modifier
//                                        .clip(RoundedCornerShape(10.dp))
//                                        .background(LocalColorTheme.current.gray[100])
//                                        .padding(15.dp, 10.5.dp)
//                                ) {
//                                    Text(
//                                        text = "야근 중",
//                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalColorTheme.current.gray[800])
//                                    )
//                                }
//                            }
//                        }
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
                                .background(brush = buttonBrush),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "링크 추천해줘!",
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center),
                                color = LocalColorTheme.current.white
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmotionSelector(
    selectedEmotion: String?,
    onEmotionChange: (String?) -> Unit
) {
    val emotions = listOf(
        R.drawable.ic_joy,
        R.drawable.ic_calm,
        R.drawable.ic_excite,
        R.drawable.ic_sad,
        R.drawable.ic_irritation,
        R.drawable.ic_anger
    )

    // 감정의 고유 key (이모지 대신 리소스 ID를 String으로)
    val emotionKeys = listOf(
        "joy", "calm", "excitement", "sadness", "irritation", "anger"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        emotions.forEachIndexed { idx, resId ->
            val key = emotionKeys[idx]
            val isSelected = selectedEmotion == key

            Box(
                modifier = Modifier
                    .size(56.dp)
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
                        onEmotionChange(if (isSelected) null else key)
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = null, // 감정 이름 필요하면 emotionKeys[idx] 등 넣어도 됨
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(7.dp))
        }
    }
}

@Composable
fun TaskSelector(
    selectedTask: String?,
    onTaskChange: (String?) -> Unit
) {
    val tasks = listOf("트렌드 확인", "과제 중", "쇼핑 중", "데이트 중", "통학 중", "알바 중", "휴식 중", "자기 전")

    Column {
        // 첫 줄: 4개
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            tasks.take(4).forEach { task ->
                val isSelected = selectedTask == task
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
                        .clickable {
                            onTaskChange(if (isSelected) null else task)
                        }
                        .padding(horizontal = 15.dp, vertical = 10.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = task,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) LocalColorTheme.current.black else LocalColorTheme.current.gray[800]
                        )
                    )
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
            tasks.drop(4).forEach { task ->
                val isSelected = selectedTask == task
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
                        .clickable {
                            onTaskChange(if (isSelected) null else task)
                        }
                        .padding(horizontal = 15.dp, vertical = 10.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = task,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) LocalColorTheme.current.black else LocalColorTheme.current.gray[800]
                        )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(userName = "세나")
}