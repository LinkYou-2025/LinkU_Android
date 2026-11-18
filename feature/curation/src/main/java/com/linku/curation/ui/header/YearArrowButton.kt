package com.linku.curation.ui.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linku.curation.R
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

/**
 * 연도 선택 화살표의 방향.
 *
 * 화살표 이미지는 오른쪽(>) 하나뿐이라, [PREV]는 같은 이미지를 좌우 반전해서 그린다.
 */
internal enum class YearArrowDirection { PREV, NEXT }

/**
 * 연도 선택용 원형 화살표 버튼.
 *
 * 활성이면 Gray 400, 비활성이면 Gray 200 배경의 원 위에 흰색 화살표를 그린다.
 * 비활성일 때는 클릭 콜백이 호출되지 않는다.
 *
 * @param direction 화살표 방향. [YearArrowDirection.PREV]는 왼쪽, [YearArrowDirection.NEXT]는 오른쪽
 * @param enabled 활성 여부. false면 연한 색으로 그려지고 클릭되지 않음
 * @param onClick 클릭 콜백
 * @param modifier 이 버튼에 적용할 Modifier
 */
@Composable
internal fun YearArrowButton(
    direction: YearArrowDirection,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorTheme = MaterialTheme.linkuColors

    Box(
        modifier = modifier
            .size(26.scaler)
            .clip(CircleShape)
            .background(if (enabled) colorTheme.gray[400] else colorTheme.gray[200])
            .noRippleClickable(enabled = enabled, onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_curation_year_chevron),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // 이미지는 오른쪽(>) 방향 하나뿐이라 PREV는 좌우 반전
                    scaleX = if (direction == YearArrowDirection.PREV) -1f else 1f
                }
        )
    }
}

@Preview(name = "연도 화살표 버튼", showBackground = true)
@Composable
private fun YearArrowButtonPreview() {
    LinkuPreview {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            YearArrowButton(direction = YearArrowDirection.PREV, enabled = true, onClick = {})
            YearArrowButton(direction = YearArrowDirection.NEXT, enabled = false, onClick = {})
        }
    }
}
