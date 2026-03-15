package com.example.login.ui.screen.social

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.navigation.NavHostController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.model.auth.Interest
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.font.Paperlogy
import com.example.login.ui.item.BottomGradientButton
import com.example.login.ui.item.StepIndicator
import com.example.login.viewmodel.SocialAuthViewModel

/* =========================
 * UI 데이터 그대로 사용
 * ========================= */

data class InterestUI(
    val emoji: String,
    val interest: Interest,
    val size: Float,
    val offset: DpOffset
)

val interestUIList = listOf(
    InterestUI("📈", Interest.BUSINESS, 159.29f, DpOffset(-208.dp, 261.dp)),
    InterestUI("🎨", Interest.DESIGN, 181.72f, DpOffset(-32.dp, 243.dp)),
    InterestUI("📚", Interest.STUDY, 145.82f, DpOffset(-89.dp, 420.dp)),
    InterestUI("✍️", Interest.WRITING, 188.45f, DpOffset(72.dp, 410.dp)),
    InterestUI("💻", Interest.IT, 107.69f, DpOffset(165.dp, 297.dp)),
    InterestUI("🌍", Interest.SOCIETY, 187f, DpOffset(392.dp, 250.dp)),
    InterestUI("🚀", Interest.STARTUP, 141.34f, DpOffset(260.dp, 365.dp)),
    InterestUI("📂", Interest.COLLECT, 187f, DpOffset(260.dp, 519.dp)),
    InterestUI("📰", Interest.CURRENT_EVENTS, 118f, DpOffset(136.dp, 613.dp)),
    InterestUI("🧠", Interest.PSYCHOLOGY, 161.53f, DpOffset(-39.dp, 574.dp)),
    InterestUI("🎯", Interest.CAREER, 125f, DpOffset(-179.dp, 553.dp)),
    InterestUI("📓", Interest.INSIGHTS, 159.29f, DpOffset(442.dp, 448.dp))
)

/* =========================
 * SocialInterestScreen
 * ========================= */

@Composable
fun SocialInterestScreen(
    navigator: NavHostController,
    viewModel: SocialAuthViewModel,
    onComplete: () -> Unit
) {
    val paperlogyFamily = Paperlogy.font
    val colorTheme = LocalColorTheme.current

    // 🔹 Social VM 상태
    val savedInterests by viewModel.interests.collectAsStateWithLifecycle()

    val selectedInterests = remember {
        mutableStateListOf<Interest>().apply {
            addAll(savedInterests)
        }
    }

    val canProceed = selectedInterests.isNotEmpty()

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomGradientButton(
                text = "완료",
                enabled = canProceed,
                activeGradient = colorTheme.maincolor,
                inactiveGradient = colorTheme.inactiveColor,
                onClick = {
                    if (selectedInterests.isEmpty()) return@BottomGradientButton

                    viewModel.updateInterests(selectedInterests.toList())
                    onComplete()   // 네비게이션은 바깥에서
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 52.dp)
                .padding(innerPadding)
                .background(Color.White)
        ) {

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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                InterestCloudScrollable(
                    interestUIList = interestUIList,
                    selectedInterests = selectedInterests,
                    onToggle = { interest ->
                        if (selectedInterests.contains(interest)) {
                            selectedInterests.remove(interest)
                        } else {
                            selectedInterests.add(interest)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun InterestCloudScrollable(
    interestUIList: List<InterestUI>,
    selectedInterests: SnapshotStateList<Interest>,
    onToggle: (Interest) -> Unit,
    height: Dp = 500.dp,
    leftGutter: Dp = 20.dp,
    rightGutter: Dp = 20.dp
) {
    val minY = interestUIList.minOfOrNull { it.offset.y } ?: 0.dp
    val shifted = interestUIList.map {
        it.copy(offset = DpOffset(it.offset.x, it.offset.y - minY))
    }

    val minX = shifted.minOfOrNull { it.offset.x } ?: 0.dp
    val shiftX = -minX
    val contentRight = shifted.maxOfOrNull { it.offset.x + it.size.dp } ?: 0.dp
    val canvasWidth = leftGutter + contentRight + shiftX + rightGutter

    val density = LocalDensity.current
    val initialOffsetPx = remember {
        with(density) { (90.dp + leftGutter).roundToPx() }
    }

    val scroll = rememberScrollState(initial = initialOffsetPx)

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
            shifted.forEach { item ->
                val isSelected = selectedInterests.contains(item.interest)
                CircleItem(
                    emoji = item.emoji,
                    text = item.interest.displayName,
                    sizeDp = item.size,
                    selected = isSelected,
                    onClick = { onToggle(item.interest) },
                    modifier = Modifier.offset(
                        leftGutter + item.offset.x + shiftX,
                        item.offset.y
                    )
                )
            }
        }
    }
}
