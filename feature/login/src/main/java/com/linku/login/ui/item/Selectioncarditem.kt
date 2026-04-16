package com.linku.login.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.font.Paperlogy
import com.linku.login.R

/**
 * 회원가입 목적/관심사 선택 카드 아이템
 *
 * 피그마 스펙:
 * - 기본: 150x150dp, radius=22dp, 배경 흰색, 테두리 없음
 * - 선택됨: shadow + maincolor 그라데이션 테두리(1dp)
 * - 상단 이미지 자리: 26x26dp 회색 사각형 (추후 png로 교체)
 * - 체크: 선택 시 우상단 26dp 원 - maincolor 그라데이션 배경 + CheckIndicator
 * - padding: start=20, top=25, end=20, bottom=25
 */
@Composable
internal fun SelectionCardItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    //디자인 모듈.
    val colorTheme = LocalColorTheme.current

    val cardModifier = if (isSelected) {
        modifier
            .shadow(
                elevation = 15.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = colorTheme.shadowColor,
                ambientColor = colorTheme.shadowColor
            )
            .border(
                width = 1.dp,
                brush = colorTheme.maincolor,
                shape = RoundedCornerShape(22.dp)
            )
    } else {
        modifier
    }

    Box(
        modifier = cardModifier
            .width(150.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(start = 20.dp, top = 25.dp, end = 20.dp, bottom = 25.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 상단: 이미지 자리 (추후 png로 교체), 그래서 컬러테마로 구현하지 않음.
            Box(
                modifier = Modifier
                    .width(26.dp)
                    .height(26.dp)
                    .background(color = Color(0xFFD9D9D9), shape = RoundedCornerShape(size = 10.dp))
            )

            // 하단: 텍스트
            Text(
                text = text,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontFamily = Paperlogy.font, // 프리뷰 확인을 위해 넣음.
                fontWeight = FontWeight.SemiBold,
                color = colorTheme.black
            )
        }

        // 선택 시 우상단 체크 아이콘
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(brush = colorTheme.maincolor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_login_check),
                    contentDescription = "checked",
                    modifier = Modifier
                        .width(12.dp)  // 지난 번, 피그마와 크기 차이로 인해 CheckIndicator를 따랐음(실제 10,7)
                        .height(9.dp)
                )
            }
        }
    }
}



// 1. 기본 (미선택)
@Preview(showBackground = true, name = "카드 - 미선택")
@Composable
fun SelectionCardItemPreview() {
    SelectionCardItem(
        text = "취업\n& 커리어 준비",
        isSelected = false,
        onClick = {}
    )
}

// 2. 선택됨
@Preview(showBackground = true, name = "카드 - 선택됨")
@Composable
fun SelectionCardItemSelectedPreview() {
    SelectionCardItem(
        text = "취업\n& 커리어 준비",
        isSelected = true,
        onClick = {}
    )
}

// 3. 긴 텍스트
@Preview(showBackground = true, name = "카드 - 긴 텍스트")
@Composable
fun SelectionCardItemLongTextPreview() {
    SelectionCardItem(
        text = "블로그/콘텐츠 작성 참고용",
        isSelected = false,
        onClick = {}
    )
}

// 4. 두 카드 나란히 (미선택 vs 선택)
@Preview(showBackground = true, name = "카드 - 나란히 비교")
@Composable
fun SelectionCardItemComparePreview() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        SelectionCardItem(
            text = "사이드 프로젝트\n& 창업",
            isSelected = false,
            onClick = {}
        )
        SelectionCardItem(
            text = "인사이트 모으기",
            isSelected = true,
            onClick = {}
        )
    }
}