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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.design.theme.font.Paperlogy
import androidx.compose.ui.unit.Dp
import com.example.core.model.auth.Purpose
import com.example.design.theme.LocalColorTheme
import com.example.login.ui.item.StepIndicator
import com.example.login.ui.item.BottomGradientButton
import com.example.login.viewmodel.SignUpViewModel


// ui 전면 변경 예정으로, 리펙토링 진행하지 않음.(수정 1월말~2월 초)
//--------------------------------------------------------------------------
// 목적 ui data 클래스
data class PurposeUI(
    val emoji: String,
    val purpose: Purpose,
    val size: Float,
    val offset: DpOffset
)

/**
 * 퍼포즈 버블 리스트 (피그마 측정값 원본): x, y, size/w/h 전부 실측값으로!
 */
val purposeUIList = listOf(
    PurposeUI("🎓", Purpose.CAREER, 159.29f, DpOffset(-102.dp, 293.29.dp)),
    PurposeUI("📅", Purpose.LATER_READING, 219.86f, DpOffset(-66.79.dp, 499.49.dp)),
    PurposeUI("💡", Purpose.SIDE_PROJECT, 181.72f, DpOffset(59.68.dp, 335.6.dp)),
    PurposeUI("❓", Purpose.OTHERS, 107.69f, DpOffset(167.56.dp, 514.58.dp)),
    PurposeUI("🧠", Purpose.SELF_DEVELOPMENT, 145.82f, DpOffset(220.88.dp, 243.dp)),
    PurposeUI("📝", Purpose.STUDY, 141.34f, DpOffset(256.08.dp, 401.92.dp)),
    PurposeUI("💼", Purpose.WORK, 186.21f, DpOffset(274.18.dp, 551.79.dp)),
    PurposeUI("💻", Purpose.CREATION_REFERENCE, 188.45f, DpOffset(374.77.dp, 272.17.dp)),
    PurposeUI("🧠", Purpose.INSIGHTS, 161.53f, DpOffset(444.17.dp, 465.29.dp)),
)


@Composable
private fun PurposeCloudScrollable(
    purposeUIList: List<PurposeUI>,
    selectedPurposes: SnapshotStateList<Purpose>,
    onToggle: (Purpose) -> Unit,
    height: Dp = 320.dp,
    leftGutter: Dp = 20.dp,
    rightGutter: Dp = 20.dp
) {
    // 4a14c5 y좌표 전체를 minY로 보정해서 top에 맞게 이동시키기!
    val minY = purposeUIList.minOfOrNull { it.offset.y } ?: 0.dp
    val shiftY = 0.dp // 필요하면 위쪽 여백(10~20dp) 추가
    val shiftedPurposes = purposeUIList.map { p ->
        p.copy(offset = DpOffset(p.offset.x, (p.offset.y - minY) + shiftY))
    }

    val minX = shiftedPurposes.minOfOrNull { it.offset.x } ?: 0.dp
    val shiftX = -minX
    val contentRight = shiftedPurposes.maxOfOrNull { it.offset.x + it.size.dp } ?: 0.dp
    val canvasWidth = leftGutter + contentRight + shiftX + rightGutter

    // X� initial scroll as before
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
            shiftedPurposes.forEach { p ->
                val isSelected = selectedPurposes.contains(p.purpose)
                CircleItem(
                    emoji = p.emoji,
                    text = p.purpose.displayName,
                    sizeDp = p.size,
                    selected = isSelected,
                    onClick = { onToggle(p.purpose) },
                    modifier = Modifier.offset(
                        leftGutter + p.offset.x + shiftX,
                        p.offset.y
                    )
                )
            }
        }
    }
}


//--------------------------------------------------------------------------
/**
 * 실제 퍼포즈 저장 목적 화면 컴포저블: 기존 기능 완전 유지 + 리팩토링 구조
 */
@Composable
fun InterestPurposeScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel //null 불가.
    //signUpViewModel: SignUpViewModel = hiltViewModel()  // Preview에서는 null, 실제 앱에서는 Hilt로 주입
) {

    // 2. 디자인 모듈의 폰트 패밀리 가져오기
    val paperlogyFamily = Paperlogy.font
    val colorTheme = LocalColorTheme.current


    // Purpose enum을 담는 리스트
    val selectedPurposes = remember {
        mutableStateListOf<Purpose>().apply {
            // 기존 선택된 목적이 있으면 복원
            signUpViewModel.signUpForm.purposeList.let { addAll(it) }
        }
    }


    val canProceed = selectedPurposes.isNotEmpty()

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomGradientButton(
                text = "다음",
                enabled = canProceed,
                activeGradient = colorTheme.maincolor,
                inactiveGradient = colorTheme.inactiveColor,
                onClick = {
                    if (selectedPurposes.isEmpty()) return@BottomGradientButton

                    // Purpose enum 리스트를 그대로 전달
                    signUpViewModel.onPurposeListChanged(selectedPurposes.toList())
                    navigator.navigate("sign_up_interest")
                }
            )
        }
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 0.dp,
                    end = 0.dp,
                    top = 52.dp,
                    bottom = 0.dp
                )
                .padding(innerPadding)   // Scaffold inset 유지
                .background(Color.White)
        ) {
            // 고정 영역 (절대 스크롤 X)
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                StepIndicator(
                    currentStep = 3,
                    totalSteps = 3,
                    label = "관심사 설정"
                )
            }

            Spacer(Modifier.height(36.dp))


            Text(
                buildAnnotatedString {
                    append("어떤 목적으로 링크를\n저장하고 싶으신가요? ")
                    withStyle(
                        SpanStyle(
                            color = Color(0xFFE5ACF4),
                            fontSize = 16.sp,
                            fontFamily = paperlogyFamily,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append("(복수 선택 가능)")
                    }
                },
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontFamily = paperlogyFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 핵심
            ) {
                PurposeCloudScrollable(
                    purposeUIList = purposeUIList,
                    selectedPurposes = selectedPurposes,
                    onToggle = { purpose ->
                        if (selectedPurposes.contains(purpose)) {
                            selectedPurposes.remove(purpose)
                        } else {
                            selectedPurposes.add(purpose)
                        }
                    },
                    height = 495.dp
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}





