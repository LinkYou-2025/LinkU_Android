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


// 데이터 클래스
data class Content(val emoji: String, val label: String, val size: Float, val offset: DpOffset)

// 예시 리스트 (위치 정보 추가!) *위치 수정 필요 -> 피그마에 맞춤. 부족한 점 있으면 추후 수정 -*
val contents = listOf(
    Content("💼", "비즈니스/마케팅", 140f, DpOffset(-190.dp, 10.dp)),
    Content("🎨", "디자인/\n크리에이티브", 140f, DpOffset(-20.dp, 10.dp)),
    Content("💻", "IT/개발", 100f, DpOffset(140.dp, 50.dp)),
    Content("🚀", "스타트업/창업", 120f, DpOffset(230.dp, 130.dp)),
    Content("🌍", "사회/문화/환경", 140f, DpOffset(-50.dp, 310.dp)),
    Content("📚", "학업/\n리포트 참고", 120f, DpOffset(-80.dp, 150.dp)),
    Content("✍️", "글쓰기/콘텐츠\n작성", 160f, DpOffset(60.dp, 150.dp)),
    Content("📓", "책/인사이트\n요약", 140f, DpOffset(350.dp, 200.dp)),
    Content("🧠", "심리/자기계발", 140f, DpOffset(-50.dp, 310.dp)),
    Content("📰", "시사/트렌드", 110f, DpOffset(100.dp, 330.dp)),
    Content("📂", "그냥 모아두고\n싶은 글들", 140f, DpOffset(210.dp, 260.dp)),
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


@Composable
fun InterestContentScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel ?= null
) {
    val selectedContents = remember { mutableStateListOf<String>() }

    val canProceed = selectedContents.isNotEmpty()
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.Start
    ) {
        ContentStepIndicator()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            buildAnnotatedString {
                append("어떤 목적으로 콘텐츠를\n저장하고 싶으신가요? ")
                withStyle(SpanStyle(color = Color(0xFFE5ACF4), fontSize = 12.sp)) {
                    append("(복수 선택 가능)")
                }
            },
            fontSize = 22.sp,
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(32.dp))

        //
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .horizontalScroll(rememberScrollState())
        ) {

            Box(
                modifier = Modifier
                    .width(1000.dp) // 충분히 넓게 확보 (필요 시 늘리세요)
                    .height(500.dp)
            ) {
                contents.forEach { content ->
                    ContentItem(
                        content = content,
                        isSelected = selectedContents.contains(content.label),
                        onClick = {
                            if (selectedContents.contains(content.label)) {
                                selectedContents.remove(content.label)
                            } else {
                                selectedContents.add(content.label)
                            }
                        },
                        modifier = Modifier.offset(content.offset.x, content.offset.y)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

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
                    //  라벨을 서버 ENUM 코드로 변환 후 ViewModel에 저장
                    signUpViewModel?.interestList = selectedContents.mapNotNull { contentLabelToCode[it] }

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
}

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
                text = content.emoji,
                fontFamily = Paperlogy,
                fontSize = 24.sp
            )
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

// 상단 관심사 단계 표시
@Composable
fun ContentStepIndicator() {
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
fun InterestContentScreenPreview() {
    val fakeNavController = rememberNavController()

    InterestContentScreen(
        navigator = fakeNavController,

    )
}