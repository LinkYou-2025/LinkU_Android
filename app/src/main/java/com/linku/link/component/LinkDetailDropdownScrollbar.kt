package com.linku.link.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.linku.design.theme.linkuColors

/**
 * 링크 상세 드롭다운의 현재 스크롤 위치를 표시하는 세로 스크롤바입니다.
 *
 * 실제 뷰포트 높이와 [ScrollState.maxValue]를 사용해 thumb의 높이와 이동 거리를 계산하므로
 * 항목 높이나 글꼴 크기가 달라져도 목록의 스크롤 위치와 동일한 비율로 움직입니다.
 * 스크롤할 내용이 없을 때는 아무것도 그리지 않습니다.
 *
 * @param scrollState 드롭다운 목록과 공유하는 스크롤 상태입니다.
 * @param topInset 스크롤바 위쪽 여백입니다.
 * @param bottomInset 스크롤바 아래쪽 여백입니다.
 * @param endInset 드롭다운 오른쪽 끝과 스크롤바 사이의 여백입니다.
 */
@Composable
internal fun BoxScope.LinkDetailDropdownScrollbar(
    scrollState: ScrollState,
    topInset: Dp,
    bottomInset: Dp,
    endInset: Dp = 9.dp,
) {
    val colors = MaterialTheme.linkuColors

    Canvas(modifier = Modifier.matchParentSize()) {
        val maxScrollValue = scrollState.maxValue
        if (maxScrollValue <= 0) return@Canvas

        val scrollbarWidthPx = 4.dp.toPx()
        val topInsetPx = topInset.toPx()
        val bottomInsetPx = bottomInset.toPx()
        val endInsetPx = endInset.toPx()
        val trackHeightPx = (size.height - topInsetPx - bottomInsetPx).coerceAtLeast(0f)
        if (trackHeightPx <= 0f) return@Canvas

        // 전체 콘텐츠 대비 현재 뷰포트 비율만큼 thumb 높이를 정합니다.
        val viewportHeightPx = size.height
        val contentHeightPx = viewportHeightPx + maxScrollValue
        val thumbHeightPx = trackHeightPx * (viewportHeightPx / contentHeightPx)
        val thumbTravelPx = (trackHeightPx - thumbHeightPx).coerceAtLeast(0f)
        val scrollProgress =
            scrollState.value.toFloat() / maxScrollValue.toFloat()
        val thumbTopPx = topInsetPx + thumbTravelPx * scrollProgress
        val thumbLeftPx = size.width - endInsetPx - scrollbarWidthPx

        drawRoundRect(
            color = colors.scrollColor,
            topLeft = Offset(thumbLeftPx, thumbTopPx),
            size = Size(scrollbarWidthPx, thumbHeightPx),
            cornerRadius = CornerRadius(scrollbarWidthPx / 2f),
        )
    }
}
