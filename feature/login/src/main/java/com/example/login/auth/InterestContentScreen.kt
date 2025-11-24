package com.example.login.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.login.Paperlogy
import androidx.compose.ui.unit.Dp
import com.example.login.R

/**
 * 관심사 선택 화면의 버블 데이터 클래스
 */
data class Content(val emoji: String, val label: String, val size: Float, val offset: DpOffset)

/**
 * 관심사 버블 리스트 (동그라미 위치, 크기, 라벨 등)
 */
val contents = listOf(
    Content("\uD83D\uDCC8", "비즈니스/마케팅", 159.29f, DpOffset(-208.dp, 261.dp)),
    Content("\uD83C\uDFA8", "디자인/\n크리에이티브", 181.72f, DpOffset(-32.dp, 243.dp)),
    Content("\uD83D\uDCDA", "학업/\n리포트 참고", 145.82f, DpOffset(-89.dp, 420.dp)),
    Content("\u270D\uFE0F", "글쓰기/콘텐츠\n작성", 188.45f, DpOffset(72.dp, 410.dp)),
    Content("\uD83D\uDCBB", "IT/개발", 107.69f, DpOffset(165.dp, 297.dp)),
    Content("\uD83C\uDF0D", "사회/문화/환경", 187f, DpOffset(392.dp, 250.dp)),
    Content("\uD83D\uDE80", "스타트업/창업", 141.34f, DpOffset(260.dp, 365.dp)),
    Content("\uD83D\uDCC2", "그냥 모아두고\n싶은 글들", 187f, DpOffset(260.dp, 519.dp)),
    Content("\uD83D\uDCF0", "시사/트렌드", 118f, DpOffset(136.dp, 613.dp)),
    Content("\uD83E\uDDE0", "심리/자기계발", 161.53f, DpOffset(-39.dp, 574.dp)),
    Content("\uD83C\uDFAF", "커리어/채용", 125f, DpOffset(-179.18.dp, 553.dp)),
    Content("\uD83D\uDCD3", "책/인사이트\n요약", 159.29f, DpOffset(442.dp, 448.dp))
)

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

private fun normalizeLabel(raw: String) = raw.replace("\n", "").replace(" ", "")
val contentLabelToCodeNormalized: Map<String, String> =
    contentLabelToCode.entries.associate { (k, v) -> normalizeLabel(k) to v }

/**
 * 관심사 선택 메인 화면 컴포저블
 */
@Composable
fun InterestContentScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel? = null
) {
    val selectedContents = remember { mutableStateListOf<String>() }
    val canProceed = selectedContents.isNotEmpty()

    Scaffold(
        containerColor = Color.White, // 항상 흰색 배경
        bottomBar = {
            val density = LocalDensity.current
            val imeBottomPx = WindowInsets.ime.getBottom(density)
            val isImeVisible = imeBottomPx > 0
            val bottomGapWhenIme = 4.dp
            val bottomGapDefault = 16.dp
            val bottomPadding = if (isImeVisible) bottomGapWhenIme else bottomGapDefault

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = bottomPadding)
                    .height(48.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (canProceed)
                                listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                            else
                                listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable(
                        enabled = canProceed,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        signUpViewModel?.interestList = selectedContents
                            .mapNotNull { contentLabelToCodeNormalized[normalizeLabel(it)] }
                            .distinct()
                        navigator.navigate("welcome")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "다음",
                    color = Color.White,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 32.dp, end = 32.dp,
                top = 40.dp,
                bottom = 96.dp
            )
        ) {
            item { ContentStepIndicator() }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    buildAnnotatedString {
                        append("어떤 분야의 콘텐츠를\n관심 있으신가요? ")
                        withStyle(SpanStyle(color = Color(0xFFE5ACF4), fontSize = 16.sp,fontWeight = FontWeight.Medium )) {
                            append("(복수 선택 가능)")
                        }
                    },
                    fontSize = 22.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Bold,
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

/**
 * 관심사 버블(동그라미) 하나를 그리는 컴포저블
 */
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
                            Color(0xFF2C6FFF).copy(alpha = 0.4f),
                            Color(0xFFC800FF).copy(alpha = 0.4f)
                        )
                    )
                else SolidColor(Color.White),
                shape = CircleShape
            )
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

/**
 * 관심사 버블 클라우드(동그라미 배치 및 스크롤) 컴포저블
 * - 각 Content의 offset을 그대로 사용해 기존 위치와 동일하게 배치
 */
@Composable
private fun InterestCloudScrollable(
    contents: List<Content>,
    selected: SnapshotStateList<String>,
    onToggle: (String) -> Unit,
    height: Dp = 500.dp
) {
    // 1. 피그마 기준 전체 bounding 박스 상단 좌표 사용 (y=243)
    val minX = 72.dp  // 글쓰기/콘텐츠 작성 기준 x
    val minY = 243.dp // 전체 동그라미 박스의 top y(피그마 기준)
    val shiftX = 20.dp
    val shiftY = 20.dp // 여백감 추가

    val shiftedContents = contents.map { c ->
        c.copy(offset = DpOffset((c.offset.x - minX) + shiftX, (c.offset.y - minY) + shiftY))
    }

    val canvasWidth = remember(shiftedContents) {
        shiftedContents.maxOf { it.offset.x + it.size.dp } + 20.dp
    }
    val canvasHeight = remember(shiftedContents) {
        shiftedContents.maxOf { it.offset.y + it.size.dp } + 20.dp // 동적 계산
    }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.coerceAtLeast(canvasHeight)) // 필요 시 자동 늘림
            .horizontalScroll(scrollState)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(canvasWidth)
                .height(height.coerceAtLeast(canvasHeight))
        ) {
            shiftedContents.forEach { content ->
                val isSelected = content.label in selected
                ContentItem(
                    content = content,
                    isSelected = isSelected,
                    onClick = { onToggle(content.label) },
                    modifier = Modifier.offset(content.offset.x, content.offset.y)
                )
            }
        }
    }
}
//@Composable
//private fun InterestCloudScrollable(
//    contents: List<Content>,
//    selected: SnapshotStateList<String>,
//    onToggle: (String) -> Unit,
//    height: Dp = 500.dp
//) {
//    // 좌표 보정: 음수 좌표가 있으면 전체를 우측으로 이동
//    val minX = remember(contents) { contents.minOfOrNull { it.offset.x } ?: 0.dp }
//    val shiftX = if (minX < 0.dp) (-minX) else 0.dp
//    val canvasWidth = remember(contents, shiftX) {
//        val right = contents.maxOfOrNull { it.offset.x + it.size.dp + shiftX } ?: 0.dp
//        right + 80.dp
//    }
//    val density = LocalDensity.current
//    val initialOffsetPx = remember { with(density) { 200.dp.roundToPx() } } // 기존처럼 200dp
//    val scrollState = rememberScrollState()
//    LaunchedEffect(Unit) {
//        scrollState.scrollTo(initialOffsetPx)
//    }
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(height)
//            .horizontalScroll(scrollState)
//            .background(Color.White),
//        contentAlignment = Alignment.Center
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
//            }
//        }
//    }
//}

/**
 * 상단 단계 인디케이터 컴포저블
 */
@Composable
fun ContentStepIndicator() {
    val isPreview = LocalInspectionMode.current
    Column(horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(30.dp)
                    .background(Color(0xFFE5ACF4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_level_check),
                    contentDescription = "완료",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(4.2.dp)
                        .background(Color(0xFFCB59EB), CircleShape)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color(0xFFE5ACF4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_level_check),
                    contentDescription = "완료",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(4.2.dp)
                        .background(Color(0xFFCB59EB), CircleShape)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
            Spacer(modifier = Modifier.width(6.dp))
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            text = "관심사 설정",
            modifier = Modifier.padding(start = 122.dp, top = 6.dp),
            fontSize = 13.sp,
            color = Color(0xFFCB59EB),
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Preview: 화면 ui 확인용. 그 이상도 이하도 아닌... 코드!
 */
@Preview(showBackground = true)
@Composable
fun InterestContentScreenPreview() {
    val fakeNavController = rememberNavController()
    InterestContentScreen(
        navigator = fakeNavController
    )
}
