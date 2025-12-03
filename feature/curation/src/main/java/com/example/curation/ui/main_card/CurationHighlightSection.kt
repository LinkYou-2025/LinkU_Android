package com.example.curation.ui.main_card

import androidx.compose.ui.unit.lerp
import coil3.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.curation.CurationViewModel
import com.example.curation.R
import com.example.curation.Paperlogy
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalInspectionMode
import com.example.curation.ui.util.rememberScaleFactor
import com.example.curation.ui.util.shimmer

//디테일 화면 X, 큐레이션 메인 페이지 에 있는 하이라이트 섹션입니다.


@Composable
fun CurationHighlightSection(
    modifier: Modifier = Modifier,
    viewModel: CurationViewModel,
    onOpenDetail: (() -> Unit)? = null
) {
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val recentCuration by viewModel.recentCuration.collectAsStateWithLifecycle()
    val highlightLiked by viewModel.highlightLiked.collectAsStateWithLifecycle()
    val likeBusy by viewModel.likeBusy.collectAsStateWithLifecycle()

    LaunchedEffect(recentCuration, isGenerating) {
        if (recentCuration == null && !isGenerating) viewModel.loadMonthlyCuration()
    }

    val isEmptySuccess = recentCuration == null && !isGenerating && errorMessage == null

    when {
        recentCuration != null -> {
            HighlightCurationCard(
                imageUrl = recentCuration!!.thumbnailUrl,
                title = "링큐 큐레이션",
                date = recentCuration!!.month ?: "",
                liked = highlightLiked ?: false,
                likeBusy = likeBusy,
                onClickCard = { onOpenDetail?.invoke() },
                onToggleLike = { viewModel.toggleHighlightLike() },
                modifier = modifier
            )
        }

        isGenerating -> {
            HighlightCurationSkeleton(
                modifier = modifier
            ) //스켈레톤 + 쉬머로 변경함.
            //Text(text = "큐레이션 생성 중...", modifier = Modifier.padding(horizontal = 20.dp))
        }

        isEmptySuccess -> {
            HighlightCardWithFallback(
                imageRes = R.drawable.img_curation_null,
                modifier = modifier
            )
        }

        errorMessage != null -> {
            HighlightCardWithFallback(
                imageRes = R.drawable.img_curation_null,
                modifier = modifier
            )
        }

        else -> {
            HighlightCardWithFallback(
                imageRes = R.drawable.img_curation_null,
                modifier = modifier
            )
        }
    }
}

@Composable
fun HighlightCurationCard(
    imageUrl: String?,
    title: String,
    date: String,
    liked: Boolean,
    likeBusy: Boolean,
    onClickCard: () -> Unit = {},
    onToggleLike: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isPreview = LocalInspectionMode.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClickCard() }
    ) {
        val w = maxWidth
        val isTablet = w >= 600.dp
        val scale = rememberScaleFactor()

        /** 글자 크기: 폰 고정 / 태블릿 업스케일 */
        val titleFont = if (isTablet) (22 * scale).sp else 22.sp
        val dateFont = if (isTablet) (16 * scale).sp else 16.sp
        val gotoFont = if (isTablet) (13 * scale).sp else 13.sp

        val heartPadDp = if (isTablet) 18.dp * scale else 18.dp
        val corner = if (isTablet) 12.dp * scale else 12.dp

        /** 카드 높이 반응형 */
        val targetHeight = when {
            w <= 430.dp -> 210.dp
            w >= 600.dp -> 260.dp
            else -> lerp(
                210.dp,
                260.dp,
                ((w - 430.dp) / (600.dp - 430.dp)).coerceIn(0f, 1f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(targetHeight)
        ) {
            val cardW = w
            val cardH = targetHeight

            val titleX = cardW * (26f / 372f)
            val titleY = cardH * (140f / 210f)

            val dateX = cardW * (26f / 372f)
            val dateY = cardH * (170f / 210f)

            val gotoY = cardH * (171f / 210f)
            val gotoX = cardW * (1f - (34f + 68f) / 372f)

            /** 배경 이미지 */
            Image(
                painter = if (isPreview || imageUrl == null)
                    painterResource(R.drawable.img_trump_card_main)
                else rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            /** 하트 */
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(heartPadDp)
                    .size(
                        width = if (isTablet) 16.dp * scale else 16.dp,
                        height = if (isTablet) 15.dp * scale else 15.dp
                    )
                    .clickable(enabled = !likeBusy) { onToggleLike() }
            ) {
                Image(
                    painter = painterResource(
                        if (liked) R.drawable.ic_heart else R.drawable.ic_heart_outline
                    ),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            /** 제목 */
            Text(
                text = title,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                fontSize = titleFont,
                color = Color.White,
                modifier = Modifier.offset(titleX, titleY)
            )

            /** 날짜 */
            val displayDate = if (date.matches(Regex("\\d{4}-\\d{2}"))) {
                val y = date.substring(0, 4)
                val m = date.substring(5, 7).toInt()
                "${y}년 ${m}월호"   // ← 공백 하나 직접 삽입
            } else date

            Text(
                text = displayDate,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                fontSize = dateFont,
                color = Color.White,
                modifier = Modifier.offset(dateX, dateY)
            )

            /** 보러가기 */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .offset(gotoX + (22.dp * if (isTablet) scale else 1f), gotoY)
                    .clickable { onClickCard() }
            ) {
                Text(
                    text = "보러가기",
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium,
                    fontSize = gotoFont,
                    color = Color.White
                )

                Spacer(modifier = Modifier.width(6.dp * (if (isTablet) scale else 1f)))

                Image(
                    painter = painterResource(R.drawable.ic_curation_vector),
                    contentDescription = null,
                    modifier = Modifier.size(10.dp * (if (isTablet) scale else 1f))
                )
            }
        }
    }
}
@Composable
fun HighlightCardWithFallback(
    imageRes: Int,
    modifier: Modifier = Modifier
) {
    val scale = rememberScaleFactor()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = (16 * scale).dp)
            .clip(RoundedCornerShape((12 * scale).dp))
            .aspectRatio(16f / 9f)
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun HighlightCurationSkeleton(modifier: Modifier = Modifier) {
    val scale = rememberScaleFactor()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(372f / 210f)   // 실제 카드와 동일 비율
            .clip(RoundedCornerShape((12 * scale).dp))
            .shimmer()
            .background(Color(0xFFEDEDED))
    )
}

@Preview(showBackground = true, widthDp = 390)
@Composable
fun PreviewHighlight() {
    HighlightCurationCard(
        imageUrl = null,
        title = "링큐 큐레이션",
        date = "2025-08",
        liked = true,
        likeBusy = false
    )
}