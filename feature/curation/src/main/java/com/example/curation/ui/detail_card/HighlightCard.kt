package com.example.curation.ui.detail_card


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.curation.Paperlogy
import com.example.design.theme.LocalColorTheme
import com.example.curation.R
import com.example.curation.CurationDetailUiState
import com.example.curation.ui.util.rememberScaleFactor
import com.example.curation.ui.util.shimmer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


//큐레이션 디테일 화면 맨 위에 있는 카드

@Composable
fun HighlightCard(
    nickname: String,
    monthLabel: String,
    onBack: () -> Unit,
    detailState: CurationDetailUiState,
    liked: Boolean,
    likeBusy: Boolean,
    onToggleLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .graphicsLayer { //TODO : 피그마 대로 구현하니 그림자 컬러가 안드로이드랑 안 맞아 그림자가 안보이는 문제가 발생.
                //TODO : 오늘 다현이랑 조절하기!
                shadowElevation = 16.dp.toPx()        // Blur 강함
                shape = RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)
                clip = false
            }
            .background(
                color = Color(0xFFCB59EB),
                shape = RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)
            )
    ){

        /** 오른쪽 하단 로고 */
        Image(
            painter = painterResource(id = R.drawable.ic_logo_light),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 22.5.dp, y = (-60).dp)
                .size(150.dp)
                .graphicsLayer(alpha = 0.65f),
            contentScale = ContentScale.Fit
        )

        /** ---------------------- 상단 바 ---------------------- */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 59.dp)
        ) {
            // 뒤로가기
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = "뒤로",
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 22.dp)
                    .size(18.dp)
                    .align(Alignment.CenterStart)
                    .clickable { onBack() }
            )

            // 제목
            Text(
                text = "큐레이션 콘텐츠",
                color = Color.White,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        /** ---------------------- 본문 콘텐츠 ---------------------- */
        Column(
            modifier = Modifier
                .padding(start = 22.dp, end = 22.dp)
                .padding(top = 111.dp)
        ) {

            if (detailState.loading) {

                TextSkeleton(width = 180.dp, height = 20.dp)
                Spacer(Modifier.height(13.dp))

                TextSkeleton(width = 250.dp, height = 16.dp)
                Spacer(Modifier.height(6.dp))
                TextSkeleton(width = 220.dp, height = 16.dp)
                Spacer(Modifier.height(6.dp))
                TextSkeleton(width = 180.dp, height = 16.dp)

                Spacer(Modifier.height(24.dp))

                TextSkeleton(width = 200.dp, height = 14.dp)
                Spacer(Modifier.height(10.dp))

                Row {
                    repeat(3) {
                        ChipSkeleton()
                        Spacer(Modifier.width(10.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))

            } else {

                Text(
                    text = "링큐 큐레이션 ㅣ ${formatMonthTag(detailState.month)}",
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )

                Spacer(Modifier.height(13.dp))

                Text(
                    text = detailState.headerMent
                        ?.replace("(닉네임)", nickname)
                        ?.replace("{닉네임}", nickname)
                        ?: "생각은 많은데 정리가 안 되죠.\n${nickname}님의 머릿속을 환기시켜줄 콘텐츠들을 모았어요!",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        fontFamily = Paperlogy
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "${nickname}님의 ${monthLabel} 상황/감정태그 요약",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(Modifier.height(10.dp))

                EmotionTags(detailState)

                Spacer(Modifier.height(22.dp))
            }
        }

        /** ---------------------- 좋아요 버튼 ---------------------- */
        HeartToggleButton(
            liked = liked,
            busy = likeBusy,
            onClick = onToggleLike,
            modifier = Modifier
                .padding(end = 22.dp, top = 111.dp)
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun EmotionTags(detailState: CurationDetailUiState) {
    when {
        detailState.loading -> {
            Row {
                repeat(3) {
                    Box(
                        Modifier
                            .width(165.dp)
                            .height(26.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                    )
                    Spacer(Modifier.width(10.dp))
                }
            }
        }

        detailState.topTags.isNotEmpty() -> {
            Row {
                detailState.topTags.take(3).forEach {
                    EmotionChip("#$it")
                    Spacer(Modifier.width(10.dp))
                }
            }
        }

        else -> {
            Row {
                listOf("#슬픔", "#커리어고민", "#짜증").forEach {
                    EmotionChip(it)
                    Spacer(Modifier.width(10.dp))
                }
            }
        }
    }
}

private fun formatMonthTag(month: String?): String {
    if (month.isNullOrBlank()) return ""
    return try {
        val (y, m) = month.split("-")
        "${y}년 ${m.toInt()}월호"
    } catch (_: Exception) {
        month
    }
}

@Composable
private fun HeartToggleButton(
    modifier: Modifier = Modifier,
    liked: Boolean,
    busy: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
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

@Composable
private fun EmotionChip(text: String) {
    Box(
        modifier = Modifier
            .height(26.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF0E6FF))
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFF9A3AB5),
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = Paperlogy,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1
        )
    }
}


@Composable
fun TextSkeleton(width: Dp, height: Dp = 16.dp) {
    val brush = rememberShimmerBrush()

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(6.dp))
            .background(brush)
    )
}

@Composable
fun ChipSkeleton() {
    val brush = rememberShimmerBrush()

    Box(
        modifier = Modifier
            .height(26.dp)
            .width(70.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(brush)
    )
}

@Composable
private fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")

    val shift = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ),
        label = "shift"
    ).value

    val colors = listOf(
        Color(0xFFF8DFFF), // 밝은 핑크-라벤더
        Color(0xFFFBEEFF), // 현재 긍정카드 배경과 동일
        Color(0xFFF3D4FF)  // 살짝 더 진한 음영 //TODO :임의로 넣은 컬러이니 다현이에게 꼭 컬러 코드 물어보기!
    )

    return Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(shift, shift)
    )
}






@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 390)
@Composable
private fun PreviewHighlightCard_Phone() {
    val demoState = CurationDetailUiState(
        loading = false,
        topTags = listOf("설렘", "통학 중", "공부 중"),
        headerMent = "세나님의 하루가 반짝였던 순간이에요. 그 감정에 어울리는 콘텐츠를 추천해요.",
        footerMent = "설렘은 가장 강력한 동기부여예요. 지금, 그 에너지를 믿어보세요.",
        month = "2025-07"
    )

    HighlightCard(
        nickname = "세나",
        monthLabel = "7월",
        onBack = {},
        detailState = demoState,
        liked = true,
        likeBusy = false,
        onToggleLike = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 800)
@Composable
private fun PreviewHighlightCard_Tablet() {
    val demoState = CurationDetailUiState(
        loading = false,
        topTags = listOf("설렘", "성장", "커리어 고민"),
        headerMent = "요즘 ${"세나"}님의 마음에 가장 많이 떠오른 감정이에요. 이 감정에 어울리는 콘텐츠를 추천할게요!",
        footerMent = "너무 잘 하고 있어요. 지금의 고민도 세나님만의 길이 될 거예요.",
        month = "2025-07"
    )

    HighlightCard(
        nickname = "세나",
        monthLabel = "7월",
        onBack = {},
        detailState = demoState,
        liked = false,
        likeBusy = false,
        onToggleLike = {}
    )
}