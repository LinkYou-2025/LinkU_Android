package com.linku.curation.ui.main_card

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.linku.curation.R
import com.linku.design.util.scaler
import java.time.LocalDate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.linku.design.theme.font.Paperlogy
import androidx.compose.foundation.layout.Row
import com.linku.curation.ui.item.CurationCheckOutButton

// 카드 컨텐츠 데이터 클래스(api 연동 아님.)
data class CurationCardContent(
    val title: String,
    val description: String
)

// 카드별 고정 컨텐츠 (1번은 동적으로 연도/월 계산)
fun getCurationCardContents(): List<CurationCardContent> {
    val prevMonth = LocalDate.now().minusMonths(1)
    val year = prevMonth.year.toString()
    val month = prevMonth.monthValue

    return listOf(
        // 현재 기준 바로 직전 지난 달을 보여줌.
        CurationCardContent(
            title = "${year}\n월간 큐레이션 ${month}월호",
            description = "이번 달을 위한 링크, 링큐가 준비했어요"
        ),
        CurationCardContent(
            title = "나와 비슷한 사람들은\n이런 링크를 봤어요",
            description = "이번 달, 같은 일상을 사는 모두의 관심 키워드"
        ),
        CurationCardContent(
            title = "잊고 있던 '나중에 보기',\n오늘이 그날이에요!",
            description = "저장만 해두기엔 아까운 링크들이 기다려요"
        )
    )
}

// CurationMainCardPager.kt
// private 키워드 제거!
@Composable
fun CurationCardItem(
    imageUrl: String?,
    page: Int = 0,
    totalPage: Int = 3,
    onCheckOutClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    @DrawableRes fallbackImage: Int = R.drawable.img_curation_example // 테스트용 기본값
) {
    val resolvedImageUrl = imageUrl
        ?.takeIf { it.isNotBlank() && it != "null" }
    val contents = getCurationCardContents()
    val content = contents.getOrNull(page) ?: contents[0]

    Box(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                //shape = RoundedCornerShape(24.scaler),
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(24.scaler))
            .background(Color(0xFFF2F2F2))
    ) {
        // 배경 이미지
        if (resolvedImageUrl == null) {
            Image(
                painter = painterResource(id = fallbackImage),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        } else {
            SubcomposeAsyncImage(
                model = resolvedImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0x00000000),
                            1.0f to Color(0x66000000)
                        )
                    )
                )
        )

        // 텍스트 오버레이
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 262.scaler)
        ) {
            // 제목
            Text(
                text = content.title,
                style = TextStyle(
                    fontSize = 24.sp,
                    lineHeight = 30.sp,
                    fontFamily = Paperlogy.font,
                    fontWeight = FontWeight(700),
                    color = Color(0xFFFFFFFF),
                ),
                modifier = Modifier.padding(start = 35.scaler)
            )

            Spacer(modifier = Modifier.height(18.scaler))

            // 소개글
            Text(
                text = content.description,
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontFamily = Paperlogy.font,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                ),
                modifier = Modifier.padding(start = 35.scaler)
            )

            Spacer(modifier = Modifier.height(18.scaler))

            // 01 | 03 + 보러가기 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 35.scaler,
                        end = 35.scaler
                    )
            ) {
                // 페이지 표시
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format("%02d", page + 1),
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            fontFamily = Paperlogy.font,
                            fontWeight = FontWeight(500),
                            color = Color(0xFFD7D9DF),
                            textAlign = TextAlign.Center,
                        )
                    )
                    Text(
                        text = " | ",
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            fontFamily = Paperlogy.font,
                            fontWeight = FontWeight(500),
                            color = Color(0xFFD7D9DF),
                            textAlign = TextAlign.Center,
                        )
                    )
                    Text(
                        text = String.format("%02d", totalPage),
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            fontFamily = Paperlogy.font,
                            fontWeight = FontWeight(500),
                            color = Color(0xFFD7D9DF),
                            textAlign = TextAlign.Center,
                        )
                    )
                }

                // 보러가기 버튼
                CurationCheckOutButton(
                    onClick = onCheckOutClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}