package com.example.login.auth

import CircleItem
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.login.R
import com.example.login.ui.item.BottomGradientButton
import com.example.login.ui.item.StepIndicator

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
    val isPreview = LocalInspectionMode.current
    // ViewModel 기존 선택값 복원 (뒤로가기 해도 유지됨)
    val selectedContents = remember {
        mutableStateListOf<String>().apply {
            if (!isPreview && signUpViewModel != null) {
                addAll(
                    signUpViewModel.interestList.mapNotNull { code ->
                        contentLabelToCodeNormalized.entries
                            .firstOrNull { it.value == code }
                            ?.key
                    }
                )
            } else {
                // Preview 기본값
                add("IT/개발")
                add("비즈니스/마케팅")
            }
        }
    }
    val canProceed = selectedContents.isNotEmpty()

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomGradientButton(
                text = "다음",
                enabled = canProceed,
                activeGradient = listOf(
                    Color(0xFF2C6FFF),
                    Color(0xFFC800FF)
                ),
                inactiveGradient = listOf(
                    Color(0xFF9BCBFF),
                    Color(0xFFF4AFFF)
                ),
                onClick = {
                    val codes = selectedContents
                        .mapNotNull { contentLabelToCodeNormalized[normalizeLabel(it)] }
                        .distinct()

                    if (codes.isNotEmpty()) {
                        signUpViewModel?.interestList = codes
                        navigator.navigate("welcome")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 0.dp,
                    end = 0.dp,
                    top = 52.dp,
                    bottom = 0.dp
                )
                .padding(innerPadding)
                .background(Color.White)
        ) {

            //  고정 영역 (스크롤 불가)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                StepIndicator(
                    currentStep = 3,
                    totalSteps = 3,
                    label = "관심사 설정"
                )

                Spacer(Modifier.height(36.dp))

                Text(
                    buildAnnotatedString {
                        append("어떤 분야의 콘텐츠를\n관심 있으신가요? ")
                        withStyle(
                            SpanStyle(
                                color = Color(0xFFE5ACF4),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append("(복수 선택 가능)")
                        }
                    },
                    fontSize = 22.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(50.dp))
            }

            // 스크롤 영역 (여기부터 스크롤)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // ⭐ 핵심
            ) {
                InterestCloudScrollable(
                    contents = contents,
                    selected = selectedContents,
                    onToggle = { label ->
                        if (selectedContents.contains(label)) {
                            selectedContents.remove(label)
                        } else {
                            selectedContents.add(label)
                        }
                    },
                    height = 500.dp
                )
            }
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
    height: Dp = 500.dp,
    leftGutter: Dp = 20.dp,
    rightGutter: Dp = 20.dp
) {
    // 1) 전체 y 기준 보정 (top 정렬)
    val minY = contents.minOfOrNull { it.offset.y } ?: 0.dp
    val shiftY = 0.dp
    val shiftedContents = contents.map { c ->
        c.copy(offset = DpOffset(c.offset.x, (c.offset.y - minY) + shiftY))
    }

    // 2) 전체 x 좌표 보정 (음수 x 제거)
    val minX = shiftedContents.minOfOrNull { it.offset.x } ?: 0.dp
    val shiftX = -minX

    // 3) 전체 width 계산
    val contentRight = shiftedContents.maxOfOrNull { it.offset.x + it.size.dp } ?: 0.dp
    val canvasWidth = leftGutter + contentRight + shiftX + rightGutter

    // 4) 초기 스크롤 위치 설정
    val density = LocalDensity.current
    val initialOffsetDp = 90.dp + leftGutter
    val initialOffsetPx = remember { with(density) { initialOffsetDp.roundToPx() } }

    val scroll = rememberScrollState(initial = initialOffsetPx)
    LaunchedEffect(canvasWidth) {
        if (scroll.value == 0) scroll.scrollTo(initialOffsetPx)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .horizontalScroll(scroll)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(canvasWidth)
                .height(height)
        ) {
            shiftedContents.forEach { item ->
                val isSelected = item.label in selected
                CircleItem(
                    emoji = item.emoji,
                    text = item.label,
                    sizeDp = item.size,
                    selected = isSelected,
                    onClick = { onToggle(item.label) },
                    modifier = Modifier.offset(
                        leftGutter + item.offset.x + shiftX,
                        item.offset.y
                    )
                )
            }
        }
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
