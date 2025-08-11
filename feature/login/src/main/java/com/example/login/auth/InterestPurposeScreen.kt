package com.example.login.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
data class Purpose(val emoji: String, val label: String, val size: Float, val offset: DpOffset)

// 예시 리스트 (위치 정보 추가!) *위치 수정 필요 -> 피그마에 맞춤. 부족한 점 있으면 추후 수정 -*
val purposes = listOf(
    Purpose("📚", "자기계발\n정보 수집", 130f, DpOffset(190.dp, 10.dp)),
    Purpose("📝", "학업/리포트 정리", 140f, DpOffset(250.dp, 190.dp)),
    Purpose("💼", "업무자료 아카이브", 150f, DpOffset(220.dp, 350.dp)),
    Purpose("💡", "사이트 프로젝트\n창업 준비", 180f, DpOffset(60.dp, 120.dp)),
    Purpose("📅", "그냥 나중에\n보고 싶은 글 저장", 220f, DpOffset(-70.dp, 290.dp)),
    Purpose("❓", "기타", 70f, DpOffset(160.dp, 310.dp)),
    Purpose("💻", "블로그/콘텐츠 작성 참고용", 110f, DpOffset(330.dp, 50.dp)),
    Purpose("🧠", "인사이트 모으기", 120f, DpOffset(380.dp, 300.dp)),
    Purpose("🎓", "취업·커리어 준비", 140f, DpOffset(-70.dp, 40.dp))
)

//매핑용 Map
val purposeLabelToCode = mapOf(
    "자기계발\n정보 수집" to "SELF_DEVELOPMENT",
    "사이드 프로젝트\n창업 준비" to "SIDE_PROJECT",
    "기타" to "OTHERS",
    "그냥 나중에\n보고 싶은 글 저장" to "LATER_READING",
    "취업·커리어 준비" to "CAREER",
    "블로그/콘텐츠 작성 참고용" to "CREATION_REFERENCE",
    "인사이트 모으기" to "INSIGHTS",
    "업무자료 아카이브" to "WORK",
    "학업/리포트 정리" to "STUDY"
)
//공백, 줄바꿈으로 서버에 empty로 들어가는 문제 보완을 위해서.
// 라벨 정규화: 줄바꿈/공백 제거 + 흔한 오타(사이트→사이드) 보정
private fun normalizePurpose(raw: String) =
    raw.replace("\n","").replace(" ","")
        .replace("사이트프로젝트", "사이드프로젝트")

// 정규화된 키로 보관하는 맵
val purposeLabelToCodeNormalized: Map<String, String> =
    purposeLabelToCode.entries.associate { (k, v) -> normalizePurpose(k) to v }


@Composable
fun InterestPurposeScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    val isPreview = LocalInspectionMode.current //폰트 표시

    val selectedPurposes = remember { mutableStateListOf<String>() }

    // 선택한 항목 개수 >= 1 → 다음 버튼 활성화
    val canProceed = selectedPurposes.isNotEmpty()



    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.Start
    ) {
        InterestStepIndicator()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            buildAnnotatedString {
                append("어떤 목적으로 링크를\n저장하고 싶으신가요? ")
                withStyle(SpanStyle(color = Color(0xFFE5ACF4), fontSize = 12.sp)) {
                    append("(복수 선택 가능)")
                }
            },
            fontSize = 22.sp,
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(32.dp))

        PurposeCloudScrollable(
            purposes = purposes,
            selected = selectedPurposes,
            onToggle = { label ->
                if (selectedPurposes.contains(label)) selectedPurposes.remove(label)
                else selectedPurposes.add(label)
            },
            height = 500.dp
        )

//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(500.dp)
//                .horizontalScroll(rememberScrollState())
//        ) {
//            Box(modifier = Modifier.width(1000.dp)) { // 충분한 너비 확보
//                purposes.forEach { purpose ->
//                    PurposeItem(
//                        purpose = purpose,
//                        isSelected = selectedPurposes.contains(purpose.label),
//                        onClick = {
//                            if (selectedPurposes.contains(purpose.label)) {
//                                selectedPurposes.remove(purpose.label)
//                            } else {
//                                selectedPurposes.add(purpose.label)
//                            }
//                        },
//                        modifier = Modifier.offset(purpose.offset.x, purpose.offset.y)
//                    )
//                }
//            }
//        }

        Spacer(modifier = Modifier.weight(1f))

        // 다음 버튼 (비활성/활성 상태 구분)
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                .clickable(enabled = canProceed) {
                    val codes = selectedPurposes
                        .mapNotNull { purposeLabelToCodeNormalized[normalizePurpose(it)] }
                        .distinct()

                    if (codes.isEmpty()) {
                        // 극히 예외(선택했는데 매핑 실패) 방지
                        android.widget.Toast.makeText(
                            navigator.context,
                            "선택한 항목을 다시 확인해 주세요.",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        return@clickable
                    }

                    signUpViewModel.purposeList = codes
//                .clickable(enabled = canProceed) {
////                    signUpViewModel.purposeList = selectedPurposes.mapNotNull {
////                        purposeLabelToCode[it]
////                    }
//                    signUpViewModel.purposeList = selectedPurposes.mapNotNull {
//                        purposeLabelToCode[it.replace("\n", "")]
//                    }
                    navigator.navigate("sign_up_interest")
                },
            contentAlignment = Alignment.Center
        ) {
            Text("다음", color = Color.White, fontFamily = Paperlogy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// 원형 아이템 -> * 수정하기 *
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
                            Color(0xFF2C6FFF).copy(alpha = 0.4f), // 연한 파랑 (왼쪽)
                            Color(0xFFC800FF).copy(alpha = 0.4f)  // 연한 분홍 (오른쪽)
                        )
                    )
                else
                    SolidColor(Color.White),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = purpose.emoji,
                fontFamily = Paperlogy,
                fontSize = 24.sp
            )
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

// 상단 관심사 단계 표시
@Composable
fun InterestStepIndicator() {
    val isPreview = LocalInspectionMode.current //폰트 표시

    Column(horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 1번 체크
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
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
            modifier = Modifier.padding(start = 128.dp, top = 4.dp),
            fontSize = 12.sp,
            color = Color(0xFFCB59EB),
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Light

        )
    }
}

@Preview(showBackground = true)
@Composable
fun InterestPurposeScreenPreview() {
    val fakeNavController = rememberNavController()
    val fakeViewModel = remember { SignUpViewModel() }

    InterestPurposeScreen(
        navigator = fakeNavController,
        signUpViewModel = fakeViewModel
    )
}

@Composable
private fun PurposeCloudScrollable(
    purposes: List<Purpose>,
    selected: SnapshotStateList<String>,
    onToggle: (String) -> Unit,
    height: Dp = 500.dp
) {
    // 왼쪽으로 삐져나간 버블 있을 때 전체를 오른쪽으로 이동
    val minX = remember(purposes) { purposes.minOfOrNull { it.offset.x } ?: 0.dp }
    val shiftX = if (minX < 0.dp) (-minX) else 0.dp

    // 우측 끝 좌표로 스크롤 캔버스 폭 계산 (여유 80dp)
    val canvasWidth = remember(purposes, shiftX) {
        val right = purposes.maxOfOrNull { it.offset.x + it.size.dp + shiftX } ?: 0.dp
        right + 80.dp
    }

    val scroll = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .horizontalScroll(scroll)
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