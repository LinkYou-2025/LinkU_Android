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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.login.Paperlogy
import androidx.compose.ui.unit.Dp
import com.example.login.R

//--------------------------------------------------------------------------
/**
 * 퍼포즈(저장 목적) 데이터 클래스 ─ 모든 좌표/크기 이모지 피그마 계측값 반영
 */
data class Purpose(val emoji: String, val label: String, val size: Float, val offset: DpOffset)

/**
 * 퍼포즈 버블 리스트 (피그마 측정값 원본): x, y, size/w/h 전부 실측값으로!
 */
val purposes = listOf(
    Purpose("🎓", "취업 커리어 준비", 159.29f, DpOffset(-102.dp, 293.29.dp)),
    Purpose("📅", "그냥 나중에\n읽고 싶은 글 저장", 219.86f, DpOffset(-66.79.dp, 499.49.dp)),
    Purpose("💡", "사이드 프로젝트\n/창업준비", 181.72f, DpOffset(59.68.dp, 335.6.dp)),
    Purpose("❓", "기타", 107.69f, DpOffset(167.56.dp, 514.58.dp)),
    Purpose("🧠", "자기개발\n /정보수집", 145.82f, DpOffset(220.88.dp, 243.dp)),
    Purpose("📝", "학업/리포트 정리", 141.34f, DpOffset(256.08.dp, 401.92.dp)),
    Purpose("💼", "업무자료 아카이빙", 186.21f, DpOffset(274.18.dp, 551.79.dp)),
    Purpose("💻", "블로그/콘텐츠~", 188.45f, DpOffset(374.77.dp, 272.17.dp)),
    Purpose("🧠", "인사이트 모으기", 161.53f, DpOffset(444.17.dp, 465.29.dp)),
)

/**
 * 라벨-코드 맵. 서버에 전송할 때 사용 -> 절대절대절대 수정 불가!!
 */
val purposeLabelToCode = mapOf(
    "취업 커리어 준비" to "CAREER",
    "그냥 나중에\n읽고 싶은 글 저장" to "LATER_READING",
    "사이드 프로젝트~" to "SIDE_PROJECT",
    "기타" to "OTHERS",
    "자기개발~" to "SELF_DEVELOPMENT",
    "학업/리포트 정리" to "STUDY",
    "업무자료 아카이빙" to "WORK",
    "블로그/콘텐츠~" to "CREATION_REFERENCE",
    "인사이트 모으기" to "INSIGHTS",
)
private fun normalizePurpose(raw: String) = raw.replace("\n"," ").replace(" ","")
val purposeLabelToCodeNormalized: Map<String, String> =
    purposeLabelToCode.entries.associate { (k, v) -> normalizePurpose(k) to v }

//--------------------------------------------------------------------------
/**
 * @Composable 퍼포즈(저장 목적) 옵션 구름형 선택 UI - 실제 좌표 보정 포함
 * @param purposes 버블 리스트
 * @param selected 선택된 라벨
 * @param onToggle 토글 람다
 * @param height 버블 클라우드 전체 높이
 * @param leftGutter 좌측 여백(20dp 권장)
 * @param rightGutter 우측 여백(20dp 권장) -> 이건 편하게 ui 부분이라 변경해도 됩니당
 */
@Composable
private fun PurposeCloudScrollable(
    purposes: List<Purpose>,
    selected: SnapshotStateList<String>,
    onToggle: (String) -> Unit,
    height: Dp = 320.dp, // 4a1 추천: 320~360dp로, 원래 500~495은 바로 화면 하단 밀림
    leftGutter: Dp = 20.dp,
    rightGutter: Dp = 20.dp
) {
    // 4a14c5 y좌표 전체를 minY로 보정해서 top에 맞게 이동시키기!
    val minY = purposes.minOfOrNull { it.offset.y } ?: 0.dp
    val shiftY = 0.dp // 필요하면 위쪽 여백(10~20dp) 추가
    val shiftedPurposes = purposes.map { p ->
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
                val isSelected = p.label in selected
                PurposeItem(
                    purpose = p,
                    isSelected = isSelected,
                    onClick = { onToggle(p.label) },
                    modifier = Modifier.offset(leftGutter + p.offset.x + shiftX, p.offset.y)
                )
            }
        }
    }
}
//--------------------------------------------------------------------------
/**
 * 개별 동그라미(버블) 퍼포즈 아이템 렌더
 */
@Composable
fun PurposeItem(
    purpose: Purpose,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(purpose.size.dp)
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
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = purpose.emoji, fontFamily = Paperlogy, fontSize = 24.sp)
            Text(
                text = purpose.label,
                fontSize = 14.sp,
                fontFamily = Paperlogy,
                textAlign = TextAlign.Center,
                color = if (isSelected) Color.White else Color.Black
            )
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
    signUpViewModel: SignUpViewModel? = null // Preview에서는 null, 실제 앱에서는 Hilt로 주입
) {
    val selectedPurposes = remember { mutableStateListOf<String>() }
    val canProceed = selectedPurposes.isNotEmpty()
    Scaffold(
        bottomBar = {
            val density = LocalDensity.current
            val imeBottomPx = WindowInsets.ime.getBottom(density)
            val isImeVisible = imeBottomPx > 0
            val bottomGapWhenIme = 4.dp
            val bottomGapDefault = 16.dp
            val navBottomDp = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
            val extraNavPadding = if (isImeVisible) 0.dp else navBottomDp
            val bottomPadding = (if (isImeVisible) bottomGapWhenIme else bottomGapDefault) + extraNavPadding
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
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
                        val codes = selectedPurposes.mapNotNull { purposeLabelToCodeNormalized[normalizePurpose(it)] }.distinct()
                        if (codes.isEmpty()) {
                            android.widget.Toast.makeText(
                                navigator.context,
                                "선택한 항목을 다시 확인해 주세요.",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                            return@clickable
                        }
                        signUpViewModel?.purposeList = codes
                        navigator.navigate("sign_up_interest")
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
                .padding(innerPadding)
                .background(Color.White),
            contentPadding = PaddingValues(
                start = 0.dp, end = 0.dp,
                top = 52.dp, bottom = 96.dp
            )
        ) {
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    InterestStepIndicator()
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
            item {
                Text(
                    buildAnnotatedString {
                        append("어떤 목적으로 링크를\n저장하고 싶으신가요? ")
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
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item {
                PurposeCloudScrollable(
                    purposes = purposes,
                    selected = selectedPurposes,
                    onToggle = { label ->
                        if (selectedPurposes.contains(label)) selectedPurposes.remove(label)
                        else selectedPurposes.add(label)
                    },
                    height = 495.dp // 박스 영역 맞춤
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}


//--------------------------------------------------------------------------
/**
 * 상단 단계 인디케이터 (그림 그대로)
 */
@Composable
fun InterestStepIndicator() {
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

//--------------------------------------------------------------------------
/**
 * Preview - 미리보기
 */
@Preview(showBackground = true)
@Composable
fun InterestPurposeScreenPreview() {
    val fakeNavController = rememberNavController()
    InterestPurposeScreen(
        navigator = fakeNavController // ViewModel 없이 호출!
    )
}