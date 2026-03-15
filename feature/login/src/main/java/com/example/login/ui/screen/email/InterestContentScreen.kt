package com.example.login.ui.screen.email

import CircleItem
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.design.theme.font.Paperlogy
import androidx.compose.ui.unit.Dp
import com.example.core.model.auth.Interest
import com.example.design.theme.LocalColorTheme
import com.example.login.ui.item.BottomGradientButton
import com.example.login.ui.item.StepIndicator
import com.example.login.viewmodel.SignUpViewModel

/**
 * 관심사 선택 화면의 버블 데이터 클래스
 */

// ui 전면 변경 예정으로, ui 리펙토링 진행하지 않음.(수정 1월말~2월 초)
data class InterestUI(
    val emoji: String,
    val interest: Interest,
    val size: Float,
    val offset: DpOffset
)

/**
 * 관심사 버블 리스트 (동그라미 위치, 크기, 라벨 등)
 */
val interestUIList = listOf(
    InterestUI("\uD83D\uDCC8", Interest.BUSINESS, 159.29f, DpOffset(-208.dp, 261.dp)),
    InterestUI("\uD83C\uDFA8", Interest.DESIGN, 181.72f, DpOffset(-32.dp, 243.dp)),
    InterestUI("\uD83D\uDCDA", Interest.STUDY, 145.82f, DpOffset(-89.dp, 420.dp)),
    InterestUI("\u270D\uFE0F", Interest.WRITING, 188.45f, DpOffset(72.dp, 410.dp)),
    InterestUI("\uD83D\uDCBB", Interest.IT, 107.69f, DpOffset(165.dp, 297.dp)),
    InterestUI("\uD83C\uDF0D", Interest.SOCIETY, 187f, DpOffset(392.dp, 250.dp)),
    InterestUI("\uD83D\uDE80", Interest.STARTUP, 141.34f, DpOffset(260.dp, 365.dp)),
    InterestUI("\uD83D\uDCC2", Interest.COLLECT, 187f, DpOffset(260.dp, 519.dp)),
    InterestUI("\uD83D\uDCF0", Interest.CURRENT_EVENTS, 118f, DpOffset(136.dp, 613.dp)),
    InterestUI("\uD83E\uDDE0", Interest.PSYCHOLOGY, 161.53f, DpOffset(-39.dp, 574.dp)),
    InterestUI("\uD83C\uDFAF", Interest.CAREER, 125f, DpOffset(-179.18.dp, 553.dp)),
    InterestUI("\uD83D\uDCD3", Interest.INSIGHTS, 159.29f, DpOffset(442.dp, 448.dp))
)


/**
 * 관심사 선택 메인 화면 컴포저블
 */
@Composable
fun InterestContentScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel
) {

    // 2. 디자인 모듈의 폰트 패밀리 가져오기
    val paperlogyFamily = Paperlogy.font
    val colorTheme = LocalColorTheme.current


    // Interest enum을 담는 리스트
    val selectedInterests = remember {
        mutableStateListOf<Interest>().apply {
            // 기존 선택된 관심사가 있으면 복원
            signUpViewModel?.signUpForm?.interestList?.let { addAll(it) }
        }
    }

    val canProceed = selectedInterests.isNotEmpty()

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomGradientButton(
                text = "다음",
                enabled = canProceed,
                activeGradient = colorTheme.maincolor,
                inactiveGradient = colorTheme.inactiveColor,
                onClick = {
                    if (selectedInterests.isEmpty()) return@BottomGradientButton

                    // Interest enum 리스트를 그대로 전달
                    signUpViewModel?.onInterestListChanged(selectedInterests.toList())
                    navigator.navigate("welcome")
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
                    fontFamily = paperlogyFamily,
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
                    interestUIList = interestUIList,  // ✅ 파라미터명 수정
                    selectedInterests = selectedInterests,  // ✅ 파라미터명 수정
                    onToggle = { interest ->
                        if (selectedInterests.contains(interest)) {
                            selectedInterests.remove(interest)
                        } else {
                            selectedInterests.add(interest)
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
    interestUIList: List<InterestUI>,
    selectedInterests: SnapshotStateList<Interest>,
    onToggle: (Interest) -> Unit,
    height: Dp = 500.dp,
    leftGutter: Dp = 20.dp,
    rightGutter: Dp = 20.dp
) {
    // 1) 전체 y 기준 보정 (top 정렬)
    val minY = interestUIList.minOfOrNull { it.offset.y } ?: 0.dp
    val shiftY = 0.dp
    val shiftedContents = interestUIList.map { c ->
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
                val isSelected = selectedInterests.contains(item.interest)
                CircleItem(
                    emoji = item.emoji,
                    text = item.interest.displayName, //enum의 displayName 사용
                    sizeDp = item.size,
                    selected = isSelected,
                    onClick = {onToggle(item.interest) }, //enum에 관심사 전달
                    modifier = Modifier.offset(
                        leftGutter + item.offset.x + shiftX,
                        item.offset.y
                    )
                )
            }
        }
    }
}

