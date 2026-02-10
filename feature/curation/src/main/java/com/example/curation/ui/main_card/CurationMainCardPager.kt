package com.example.curation.ui.main_card

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.example.curation.R
import com.example.design.util.scaler
import kotlin.math.absoluteValue
import androidx.compose.animation.core.snap


/**
 * 큐레이션 메인 카드 페이저
 * - 중앙: 346 x 432
 * - 양옆: 137.5 x 230
 * - 간격: 12dp
 */
@Composable
fun CurationMainCardPager(
    pagerState: PagerState,
    imageUrls: List<String?>, // 3개의 이미지 URL
    isDetailOpen: Boolean,
    modifier: Modifier = Modifier,
    onCardClick: (index: Int, imageUrl: String?) -> Unit = { _, _ -> } // 추가
) {


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = !isDetailOpen,
            contentPadding = PaddingValues(horizontal = 33.scaler), // 양옆 여백으로 미리보기
            pageSpacing = 12.scaler,
            modifier = Modifier
                .fillMaxWidth()
                .height(432.scaler)
        ) { page ->
            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue

            // 0 = 중앙(선택), 1 = 양옆
            val isSelected = pageOffset < 0.5f

            // 애니메이션으로 크기 전환
            val cardWidth by animateDpAsState(
                targetValue = if (isSelected) 346.scaler else 288.scaler,
                animationSpec = if (isDetailOpen) snap() else tween(300),
                label = "cardWidth"
            )

            val cardHeight by animateDpAsState(
                targetValue = if (isSelected) 432.scaler else 360.scaler,
                animationSpec = if (isDetailOpen) snap() else tween(300),
                label = "cardHeight"
            )


            // 스케일 기반 부드러운 전환 (선택사항)
            val scale = 1f - (pageOffset * 0.3f).coerceIn(0f, 0.3f)

            Box(
                modifier = Modifier
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                CurationCardItem(
                    imageUrl = imageUrls[page],
                    modifier = Modifier
                        .width(cardWidth)
                        .height(cardHeight)
                        .clickable { onCardClick(page, imageUrls[page]) }
                )

            }
        }

        Spacer(modifier = Modifier.height(18.scaler))

        // 인디케이터
        CurationPagerIndicator(
            pageCount = imageUrls.size,
            currentPage = pagerState.currentPage
        )
    }
}


@Composable
private fun CurationPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage

            Box(
                modifier = Modifier
                    .width(if (isSelected) 28.dp else 7.dp)
                    .height(7.dp)
                    .background(
                        color = if (isSelected) Color(0xFF87898F) else Color(0xFFD7D9DF),
                        shape = RoundedCornerShape(if (isSelected) 3.5.dp else 7.dp)
                    )
            )
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
private fun PreviewCurationMainCardPager() {
    CurationMainCardPager(
        imageUrls = listOf(null, null, null)
    )
}*/