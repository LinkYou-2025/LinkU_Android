package com.example.linku_android.auth

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
import com.example.linku_android.component.Paperlogy


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
    Purpose("💻", "블로그/콘텐츠 작성 참고용", 110f, DpOffset(330.dp, 10.dp)),
    Purpose("🧠", "인사이트 모으기", 120f, DpOffset(340.dp, 300.dp)),
    Purpose("🎓", "취업·커리어 준비", 140f, DpOffset(-70.dp, 40.dp))
)

@Preview(showBackground = true)
@Composable
fun InterestPurposeScreen() {
    val isPreview = LocalInspectionMode.current //폰트 표시

    val selectedPurposes = remember { mutableStateListOf<String>() }

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
            fontSize = 18.sp,
            fontFamily = if (isPreview) FontFamily.Serif else Paperlogy,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .horizontalScroll(rememberScrollState()) // 좌우 스크롤 가능
        ) {
            purposes.forEach { purpose ->
                PurposeItem(
                    purpose = purpose,
                    isSelected = selectedPurposes.contains(purpose.label),
                    onClick = {
                        if (selectedPurposes.contains(purpose.label)) {
                            selectedPurposes.remove(purpose.label)
                        } else {
                            selectedPurposes.add(purpose.label)
                        }
                    },
                    modifier = Modifier.offset(purpose.offset.x, purpose.offset.y)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("다음", color = Color.White, fontSize = 13.sp)
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
            .background(if (isSelected) Color(0xFFE5ACF4) else Color.White, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = purpose.emoji, fontSize = 24.sp)
            Text(text = purpose.label, fontSize = 12.sp, textAlign = TextAlign.Center)
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
            fontFamily = if (isPreview) FontFamily.Serif else Paperlogy,
            fontWeight = FontWeight.Light

        )
    }
}