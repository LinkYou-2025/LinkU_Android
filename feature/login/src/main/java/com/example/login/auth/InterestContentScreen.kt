package com.example.login.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.login.R
import com.example.login.Paperlogy
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp


// 데이터 클래스
data class Content(val emoji: String, val label: String, val size: Float, val offset: DpOffset)

// 예시 리스트 (위치 정보 추가!) *위치 수정 필요 -> 피그마에 맞춤. 부족한 점 있으면 추후 수정 -*
val contents = listOf(
    Content("💼", "비즈니스/마케팅", 140f, DpOffset(-190.dp, 10.dp)),
    Content("🎨", "디자인/\n크리에이티브", 140f, DpOffset(-20.dp, 10.dp)),
    Content("💻", "IT/개발", 100f, DpOffset(140.dp, 50.dp)),
    Content("🚀", "스타트업/창업", 100f, DpOffset(230.dp, 140.dp)),
    Content("🌍", "사회/문화/환경", 140f, DpOffset(-50.dp, 310.dp)),
    Content("📚", "학업/리포트\n참고", 120f, DpOffset(-60.dp, 160.dp)),
    Content("✍️", "글쓰기/콘텐츠\n작성", 160f, DpOffset(70.dp, 160.dp)),
    Content("📓", "책/인사이트\n요약", 140f, DpOffset(350.dp, 200.dp)),
    Content("🧠", "심리/자기계발", 140f, DpOffset(-50.dp, 310.dp)),
    Content("📰", "시사/트렌드", 110f, DpOffset(100.dp, 330.dp)),

    Content("📂", "그냥 모아두고\n싶은 글들", 140f, DpOffset(220.dp, 260.dp)),

    Content("🎯", "커리어/채용", 100f, DpOffset(-150.dp, 280.dp))


)

// 관심 콘텐츠 라벨 → 서버 enum 코드 매핑
val contentLabelToCode = mapOf(
    "비즈니스/마케팅" to "BUSINESS",
    "디자인/\n크리에이티브" to "DESIGN",
    "IT/개발" to "IT",
    "스타트업/창업" to "STARTUP",
    "사회/문화/환경" to "SOCIETY",
    "학업/\n리포트 참고" to "STUDY",
    "글쓰기/콘텐츠\n작성" to "WRITING",
    "책/인사이트\n요약" to "INSIGHTS",
    "심리/자기계발" to "PSYCHOLOGY",
    "시사/트렌드" to "CURRENT_EVENTS",
    "그냥 모아두고\n싶은 글들" to "COLLECT",
    "커리어/채용" to "CAREER"
)

//줄바꿈 및 공백으로 서버에 empty로 들어가는 문제 방지
// 정규화 함수 (파일 위쪽 어딘가)
private fun normalizeLabel(raw: String) =
    raw.replace("\n", "").replace(" ", "")

// 정규화된 키로 보관하는 맵
val contentLabelToCodeNormalized: Map<String, String> =
    contentLabelToCode.entries.associate { (k, v) -> normalizeLabel(k) to v }

@Composable
fun InterestContentScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel ?= null
) {
    val selectedContents = remember { mutableStateListOf<String>() }
    val canProceed = selectedContents.isNotEmpty()

    // 🔥 Column → Scaffold + LazyColumn
    //  - 본문은 세로 스크롤
    //  - 하단 버튼은 bottomBar에 고정
    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 14.dp)
                    .navigationBarsPadding()
                    .imePadding()
                    .height(48.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (canProceed)
                                listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                            else
                                listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    // 🔕 버튼 리플/회색 프레스 제거
                    .clickable(
                        enabled = canProceed,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        signUpViewModel?.interestList = selectedContents
                            .mapNotNull { contentLabelToCodeNormalized[normalizeLabel(it)] }
                            .distinct()

                        android.util.Log.d("Interest", "selected=${selectedContents}")
                        android.util.Log.d("Interest", "interestList=${signUpViewModel?.interestList}")

                        navigator.navigate("welcome")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "다음",
                    color = Color.White,
                    fontFamily = Paperlogy,
                    fontSize = 16.sp
                )
            }

        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
            contentPadding = PaddingValues(
                start = 0.dp, end = 0.dp,
                //start = 32.dp, end = 32.dp,
                top = 52.dp,
                bottom = 96.dp // bottomBar(48) + 외곽 패딩(14*2) 만큼 여유
            )
        ) {
            item { ContentStepIndicator(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Text(
                    buildAnnotatedString {
                        //color = Color(0xFFE5ACF4)
                        // ✍️ 사용하신 텍스트 그대로 유지 (“괸심” 포함)
                        append("어떤 분야의 콘텐츠를\n괸심 있으신가요? ")
                        withStyle(
                            SpanStyle(
                                color = Color(0xFFE5ACF4),   // 연보라색
                                fontSize = 16.sp,           // 16sp
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append("(복수 선택 가능)")
                        }
                    },
                    fontSize = 22.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                InterestCloudScrollable(
                    contents = contents,
                    selected = selectedContents,
                    onToggle = { label ->
                        if (selectedContents.contains(label)) selectedContents.remove(label)
                        else selectedContents.add(label)
                    },
                    height = 500.dp
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}



//@Composable
//fun InterestContentScreen(
//    navigator: NavHostController,
//    signUpViewModel: SignUpViewModel ?= null
//) {
//    val selectedContents = remember { mutableStateListOf<String>() }
//
//    val canProceed = selectedContents.isNotEmpty()
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 40.dp),
//        horizontalAlignment = Alignment.Start
//    ) {
//        ContentStepIndicator()
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(
//            buildAnnotatedString {
//                append("어떤 분야의 콘텐츠를\n괸심 있으신가요? ")
//                withStyle(SpanStyle(color = Color(0xFFE5ACF4), fontSize = 12.sp)) {
//                    append("(복수 선택 가능)")
//                }
//            },
//            fontSize = 22.sp,
//            fontFamily = Paperlogy,
//            fontWeight = FontWeight.Bold,
//        )
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        InterestCloudScrollable(
//            contents = contents,
//            selected = selectedContents,
//            onToggle = { label ->
//                if (selectedContents.contains(label)) selectedContents.remove(label)
//                else selectedContents.add(label)
//            },
//            height = 500.dp
//        )
//
//        //
////        Row(
////            modifier = Modifier
////                .fillMaxWidth()
////                .height(500.dp)
////                .horizontalScroll(rememberScrollState())
////        ) {
////
////            Box(
////                modifier = Modifier
////                    .width(1000.dp) // 충분히 넓게 확보 (필요 시 늘리세요)
////                    .height(500.dp)
////            ) {
////                contents.forEach { content ->
////                    ContentItem(
////                        content = content,
////                        isSelected = selectedContents.contains(content.label),
////                        onClick = {
////                            if (selectedContents.contains(content.label)) {
////                                selectedContents.remove(content.label)
////                            } else {
////                                selectedContents.add(content.label)
////                            }
////                        },
////                        modifier = Modifier.offset(content.offset.x, content.offset.y)
////                    )
////                }
////            }
////        }
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(48.dp)
//                .background(
//                    brush = Brush.horizontalGradient(
//                        colors = if (canProceed)
//                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                        else
//                            listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
//                    ),
//                    shape = RoundedCornerShape(24.dp)
//                )
//                .clickable(enabled = canProceed) {
//                    signUpViewModel?.interestList = selectedContents
//                        .mapNotNull { contentLabelToCodeNormalized[normalizeLabel(it)] }
//                        .distinct()
//
//                    //선택이 잘 들어가는지 로그 확인
//                    android.util.Log.d("Interest", "selected=${selectedContents}")
//                    android.util.Log.d("Interest", "interestList=${signUpViewModel?.interestList}")
//
////                .clickable(enabled = canProceed) {
////                    //  라벨을 서버 ENUM 코드로 변환 후 ViewModel에 저장
////                    // 줄 바꿈으로 서버 인식 불가 -> 변경.
////                    //signUpViewModel?.interestList = selectedContents.mapNotNull { contentLabelToCode[it] }
////                    signUpViewModel?.interestList = selectedContents.mapNotNull {
////                        contentLabelToCode[it.replace("\n", "")]
////                    }
//
//                    navigator.navigate("welcome")
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                "다음",
//                color = Color.White,
//                fontFamily = Paperlogy,
//                fontSize = 16.sp
//            )
//        }
//    }
//}

// 원형 아이템 -> * 수정하기 *
@Composable
fun ContentItem(
    content: Content,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(content.size.dp)
            .border(
                width = 1.dp,
                brush = Brush.sweepGradient(listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))),
                shape = CircleShape
            )
            .background(
                brush = if (isSelected)
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2C6FFF).copy(alpha = 0.4f), // 왼쪽 연파랑
                            Color(0xFFC800FF).copy(alpha = 0.4f)  // 오른쪽 연분홍
                        )
                    )
                else SolidColor(Color.White),
                shape = CircleShape
            )
            // 🔕 리플/회색 프레스 제거
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = content.emoji, fontFamily = Paperlogy, fontSize = 24.sp)
            Text(
                text = content.label,
                fontSize = 14.sp,
                fontFamily = Paperlogy,
                textAlign = TextAlign.Center,
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }
}
//@Composable
//fun ContentItem(
//    content: Content,
//    isSelected: Boolean,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Box(
//        modifier = modifier
//            .size(content.size.dp)
//            .border(
//                width = 1.dp,
//                brush = Brush.sweepGradient(listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))),
//                shape = CircleShape
//            )
//            .background(
//                brush = if (isSelected)
//                    Brush.horizontalGradient(
//                        colors = listOf(
//                            Color(0xFF2C6FFF).copy(alpha = 0.4f), // 연한 파랑 (왼쪽)
//                            Color(0xFFC800FF).copy(alpha = 0.4f)  // 연한 분홍 (오른쪽)
//                        )
//                    )
//                else
//                    SolidColor(Color.White),
//                shape = CircleShape
//            )
//            .clickable { onClick() },
//        contentAlignment = Alignment.Center
//    ) {
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Text(
//                text = content.emoji,
//                fontFamily = Paperlogy,
//                fontSize = 24.sp
//            )
//            Text(
//                text = content.label,
//                fontSize = 14.sp,
//                fontFamily = Paperlogy,
//                textAlign = TextAlign.Center,
//                color = if (isSelected) Color.White else Color.Black
//            )
//        }
//    }
//}

@Composable
private fun PurposeCloudScrollable(
    purposes: List<Purpose>,
    selected: SnapshotStateList<String>,
    onToggle: (String) -> Unit,
    height: Dp = 500.dp
) {
    // 좌표 음수 보정(왼쪽으로 삐져나간 아이템 있으면 전체를 우측으로 이동)
    val minX = remember(purposes) { purposes.minOfOrNull { it.offset.x } ?: 0.dp }
    val shiftX = if (minX < 0.dp) (-minX) else 0.dp

    // 우측 끝 좌표로 캔버스 폭 계산(여유 80dp)
    val canvasWidth = remember(purposes, shiftX) {
        val right = purposes.maxOfOrNull { it.offset.x + it.size.dp + shiftX } ?: 0.dp
        right + 80.dp
    }

    // 초기 가로 스크롤 오프셋(진입 시만 적용)
    val density = LocalDensity.current
    val initialOffsetPx = remember { with(density) { 90.dp.roundToPx() } }
    val scrollState = rememberScrollState(initial = initialOffsetPx)

    LaunchedEffect(canvasWidth) {
        if (scrollState.value == 0) scrollState.scrollTo(initialOffsetPx)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .horizontalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(canvasWidth)
                .height(height)
        ) {
            purposes.forEach { p ->
                val isSelected = p.label in selected
                PurposeItem(
                    purpose = p,
                    isSelected = isSelected,
                    onClick = { onToggle(p.label) },
                    modifier = Modifier.offset(p.offset.x + shiftX, p.offset.y)
                )
            }
        }
    }
}


// 상단 관심사 단계 표시
@Composable
fun ContentStepIndicator(modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current //폰트 표시

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 1번 체크
            Box(
                modifier = Modifier
                    .padding(start = 0.dp)
                    .size(28.dp)
                    .background(Color(0xFFE5ACF4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "완료",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(Color(0xFFCB59EB), CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 2번 체크
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFFE5ACF4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "완료",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(Color(0xFFCB59EB), CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 3번 숫자 활성 원
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFFCB59EB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "3",
                    fontFamily = Paperlogy,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = "관심사 설정",
            modifier = Modifier.padding(start = 128.dp, top = 6.dp),
            fontSize = 12.sp,
            color = Color(0xFFCB59EB),
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Light

        )
    }
}

@Preview(showBackground = true)
@Composable
fun InterestContentScreenPreview() {
    val fakeNavController = rememberNavController()

    InterestContentScreen(
        navigator = fakeNavController,

    )
}

//버블 섹션 좌우 스크롤 되게 수정
@Composable
private fun InterestCloudScrollable(
    contents: List<Content>,
    selected: SnapshotStateList<String>,
    onToggle: (String) -> Unit,
    height: Dp = 500.dp
) {
    // 좌표 보정
    val minX = remember(contents) { contents.minOfOrNull { it.offset.x } ?: 0.dp }
    val shiftX = if (minX < 0.dp) (-minX) else 0.dp

    // 캔버스 너비 계산
    val canvasWidth = remember(contents, shiftX) {
        val right = contents.maxOfOrNull { it.offset.x + it.size.dp + shiftX } ?: 0.dp
        right + 80.dp
    }

    // ▶ 초기 스크롤을 150dp로 설정
    val density = LocalDensity.current
    val initialOffsetDp = 10.dp
    val initialOffsetPx = remember { with(density) { initialOffsetDp.roundToPx() } }

    val scroll = rememberScrollState(initial = initialOffsetPx)

    // 측정/재컴포지션 때 0으로 돌아가는 걸 방지(사용자가 이미 스크롤했다면 건드리지 않음)
    LaunchedEffect(canvasWidth) {
        if (scroll.value == 0) {
            scroll.scrollTo(initialOffsetPx)
        }
    }

    val scrollState = rememberScrollState()

    //val density = LocalDensity.current

    LaunchedEffect(Unit) {

        scrollState.scrollTo(with(density) { 200.dp.roundToPx() })
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .horizontalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .width(canvasWidth)
                .height(height)
        ) {
            contents.forEach { content ->
                val isSelected = content.label in selected
                ContentItem(
                    content = content,
                    isSelected = isSelected,
                    onClick = { onToggle(content.label) },
                    modifier = Modifier.offset(content.offset.x + shiftX, content.offset.y)
                )
//    // 좌표 보정: 음수 x가 있으면 전체를 +shiftX만큼 이동
//    val minX = remember(contents) { contents.minOfOrNull { it.offset.x } ?: 0.dp }
//    val shiftX = if (minX < 0.dp) (-minX) else 0.dp
//
//    // 우측 끝 계산해서 캔버스 폭 확보 (여유 80dp)
//    val canvasWidth = remember(contents, shiftX) {
//        val right = contents.maxOfOrNull { it.offset.x + it.size.dp + shiftX } ?: 0.dp
//        (right + 80.dp) //여기 스크롤 수정
//    }
//    val scroll = rememberScrollState()
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(height)
//            .horizontalScroll(rememberScrollState())
//            //.horizontalScroll(scroll)
//        //contentAlignment = Alignment.Center
//
//    ) {
//        Box(
//            modifier = Modifier
//                .width(canvasWidth)
//                .height(height)
//        ) {
//            contents.forEach { content ->
//                val isSelected = content.label in selected
//                ContentItem(
//                    content = content,
//                    isSelected = isSelected,
//                    onClick = { onToggle(content.label) },
//                    modifier = Modifier.offset(content.offset.x + shiftX, content.offset.y)
//                )
//        Box(
//            modifier = Modifier
//                .width(canvasWidth)
//                .height(height)
//        ) {
//            contents.forEach { content ->
//                val isSelected = content.label in selected
//                ContentItem(
//                    content = content,
//                    isSelected = isSelected,
//                    onClick = { onToggle(content.label) },
//                    modifier = Modifier.offset(content.offset.x + shiftX, content.offset.y)
//                )
            }
        }
    }
}