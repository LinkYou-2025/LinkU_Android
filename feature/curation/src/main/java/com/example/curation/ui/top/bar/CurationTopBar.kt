package com.example.curation.ui.top.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.curation.CurationDetailUiState
import com.example.curation.CurationLinksUiState
import com.example.curation.Paperlogy
import com.example.curation.R

import com.example.design.theme.LocalColorTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Suppress("UnusedBoxWithConstraintsScope")
@Composable
private fun HighlightCard(
    nickname: String,
    monthLabel: String,
    onBack: () -> Unit,
    detailState: CurationDetailUiState,
    liked: Boolean,
    likeBusy: Boolean,
    onToggleLike: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(268.dp)
    ) {
        // 배경 단색 (피그마 CB59EB)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(412.dp)
                .height(266.dp)
                .background(
                    color = Color(0xFFCB59EB),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 22.dp,
                        bottomEnd = 22.dp
                    )
                )
        )


        // 오른쪽 하단 로고 (기기 폭 비례로 큼직하게)
        val logoSize = (maxWidth * 0.46f).coerceIn(112.dp, 192.dp)
        Image(
            painter = painterResource(R.drawable.ic_logo_light),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 10.dp) // 오른쪽으로 10 이동
                .padding(bottom = 16.dp)
                .size(logoSize)
                .graphicsLayer(alpha = 0.60f)
        )

        // 하트 토글 버튼 (로고 위 겹치기)

        // 상단 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 59.dp)   // ⬅ Figma 기준 위치
                .align(Alignment.TopStart)
        ) {
            // 뒤로가기 아이콘
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "뒤로",
                tint = LocalColorTheme.current.white,
                modifier = Modifier
                    .padding(0.dp)
                    .width(10.dp)
                    .height(16.25.dp)
                    .align(Alignment.CenterStart)
                    .clickable { onBack() }
            )

            // 가운데 타이틀 문구
            Text(
                text = "큐레이션 콘텐츠",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium, // 500
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        HeartToggleButton(
            liked = liked,
            busy = likeBusy,
            onClick = onToggleLike,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 111.dp, end = 22.dp)
        )

        // 현재 날짜에서 전달 구하기
        val prevMonthLabel = LocalDate.now()
            .minusMonths(1)
            .format(DateTimeFormatter.ofPattern("M월", Locale.KOREAN))

        // 본문
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 72.dp)
                .padding(start = 16.dp, end = 16.dp, top = 36.dp)
        ) {
            // 제목 + 하트 (같은 줄, 7월호 기준 우측 36dp, 위로 2dp 올림)
            val HEART_GAP = 36.dp         // ← 더 오른쪽으로 (원래 30dp였으면 여길 조절)
            val HEART_NUDGE_Y = (-5).dp   // ← 살짝 위로 올리기(필요 시 -1 ~ -4dp 튜닝)
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "링큐 큐레이션 ㅣ ${formatMonthLabel(detailState.month)}",
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 30.sp,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Bold, // 700
                        color = Color.White
                    ),
                    modifier = Modifier
                        .padding(start = 8.dp) // Figma 기준 왼쪽 20dp
                )

                //Spacer(Modifier.width(40.dp))
                Spacer(Modifier.width(HEART_GAP))


            }




            Spacer(Modifier.height(13.dp))

            Text(
                text = run {
                    val fromApi = replaceNickname(detailState.headerMent, nickname)
                    if (fromApi.isNotBlank()) fromApi
                    else "생각은 많은데 정리가 안 되죠.\n${nickname}님의 머릿속을 환기시켜줄 콘텐츠들을 모았어요!"
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = Paperlogy, fontWeight = FontWeight.Medium, fontSize = 16.sp
                ),
                modifier = Modifier
                    .padding(start = 8.dp),
                color = LocalColorTheme.current.white
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "${nickname}님의 ${monthLabel} 상황/감정태그 요약",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                ),
                modifier = Modifier
                    .padding(start = 8.dp),
                color = LocalColorTheme.current.white
            )

            Spacer(Modifier.height(8.dp))
            when {
                detailState.loading -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(3) {
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(LocalColorTheme.current.white.copy(alpha = 0.25f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) { Text(" ", color = LocalColorTheme.current.white) }
                            if (it != 2) Spacer(Modifier.width(8.dp))
                        }
                    }
                }
                detailState.topTags.isNotEmpty() -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Modifier
                            .padding(start = 8.dp)
                        detailState.topTags.take(3).forEachIndexed { idx, tag ->
                            EmotionChip("#$tag")
                            if (idx != detailState.topTags.lastIndex) Spacer(Modifier.width(8.dp))
                        }

                    }
                }
                else -> {
                    // 폴백: 기존 더미 유지
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EmotionChip("#슬픔")
                        Spacer(Modifier.width(8.dp))
                        EmotionChip("#커리어고민")
                        Spacer(Modifier.width(8.dp))
                        EmotionChip("#짜증")
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }


    }
}

//큐레이션 디테일 페이지 하트 버튼
@Composable
private fun HeartToggleButton(
    modifier: Modifier = Modifier,
    liked: Boolean,
    busy: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color.White,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(1.dp)
            .size(width = 18.dp, height = 16.dp)
            .clickable(enabled = !busy) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(
                id = if (liked) R.drawable.ic_heart else R.drawable.ic_heart_outline
            ),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.fillMaxSize()
        )
    }
}

//이모지 칩
@Composable
private fun EmotionChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(LocalColorTheme.current.white)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF9A3AB5),
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = Paperlogy,
                fontSize = 12.sp
            )
        )
    }
}


private fun replaceNickname(text: String?, nickname: String): String {
    if (text.isNullOrBlank()) return ""
    return text.replace("(닉네임)", nickname)
        .replace("{닉네임}", nickname)
        .replace("\$닉네임", nickname)
}


private fun formatMonthLabel(month: String?): String {
    if (month.isNullOrBlank()) return ""
    return try {
        val parts = month.split("-") // "2025-06" → ["2025","06"]
        val year = parts[0]
        val monthNum = parts[1].toInt()
        "${year}년 ${monthNum}월호"
    } catch (e: Exception) {
        month // 파싱 실패 시 그대로
    }
}

@Preview(
    name = "Highlight Card Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 300,
    showSystemUi = false
)
@Composable
private fun HighlightCardPreview() {

    val demoDetail = CurationDetailUiState(
        loading = false,
        month = "2025-07",
        topTags = listOf("설렘", "통학 중", "공부 중"),
        headerMent = "세나님의 하루가 반짝였던 순간이에요. 그 감정에 어울리는 콘텐츠를 추천해요.",
        footerMent = "설렘은 가장 강력한 동기부여예요. 지금 그 에너지를 믿어보세요."
    )

    Surface(color = Color.White) {
        HighlightCard(
            nickname = "세나",
            monthLabel = "7월",
            onBack = {},
            detailState = demoDetail,
            liked = false,
            likeBusy = false,
            onToggleLike = {}
        )
    }
}
