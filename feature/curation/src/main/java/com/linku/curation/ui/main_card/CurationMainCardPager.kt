package com.linku.curation.ui.main_card

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import kotlin.math.absoluteValue

/**
 * 큐레이션 메인 카드 페이저
 *
 * 3장의 카드를 좌우로 스와이프할 수 있는 HorizontalPager.
 * 중앙 카드는 원본 크기(1.0)로, 양옆 카드는 83% 스케일로 표시되며
 * 스케일 전환 시 텍스트·이미지가 함께 축소된다 (graphicsLayer 사용). -> 다인 언니 수정했어 히히
 *
 * @param modifier 외부에서 전달하는 Modifier
 * @param pagerState HorizontalPager 스크롤 상태. 외부에서 rememberPagerState로 생성해서 전달
 * @param imageUrls 각 카드에 표시할 이미지 URL 리스트. blank / "null" 문자열이면 fallback 이미지 표시
 * @param onCardClick 카드 하단 체크아웃 버튼 클릭 시 호출. (페이지 인덱스, 이미지 URL) 전달
 */
@Composable
fun CurationMainCardPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    imageUrls: List<String>,
    onCardClick: (index: Int, imageUrl: String) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 33.scaler),
            pageSpacing = 12.scaler,
            modifier = Modifier
                .fillMaxWidth()
                .height(432.scaler)
        ) { page ->
            // 현재 페이지 기준으로 이 카드가 얼마나 떨어져 있는지 (0 = 중앙, 1 = 한 칸 옆)
            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue

            // offset 0.5 미만이면 중앙 카드로 판별
            val isSelected = pageOffset < 0.5f

            // 중앙: 1.0, 양옆: 0.832 — graphicsLayer로 레이아웃 크기는 고정하고 시각적으로만 축소
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.832f,
                animationSpec = spring( // 스프링이 쫀득한 대신, 시간 조절이 안됩니다. 디자이너, pm이 원해도 애니메이션 시간 지정 불가함....
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "cardScale"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(432.scaler),
                contentAlignment = Alignment.Center
            ) {
                CurationCardItem(
                    imageUrl = imageUrls[page],
                    page = page,
                    totalPage = imageUrls.size,
                    onCheckOutClick = { onCardClick(page, imageUrls[page]) },
                    modifier = Modifier
                        .width(346.scaler)
                        .height(432.scaler)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.scaler))

        CurationPagerIndicator(
            pageCount = imageUrls.size,
            currentPage = pagerState.currentPage
        )
    }
}

/**
 * 페이저 하단 인디케이터 도트
 *
 * 선택된 페이지는 가로로 길게 늘어난 pill 형태, 나머지는 원형 도트로 표시.
 */
@Composable
fun CurationPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    val colorTheme = MaterialTheme.linkuColors

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
                        color = if (isSelected) colorTheme.gray[600] else colorTheme.gray[300],
                        shape = RoundedCornerShape(if (isSelected) 3.5.dp else 7.dp)
                    )
            )
        }
    }
}
