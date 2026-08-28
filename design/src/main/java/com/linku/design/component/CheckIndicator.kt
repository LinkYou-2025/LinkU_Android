package com.linku.design.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linku.design.R
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors

/**
 * 체크 상태를 표현하는 공통 인디케이터의 시각적 스타일입니다.
 */
enum class CheckIndicatorStyle {
    /** 비선택 상태에서도 회색 배경과 체크 아이콘을 표시하는 기본 스타일입니다. */
    Filled,

    /** 비선택 상태를 흰색 배경과 회색 테두리로 표시하는 스타일입니다. */
    Outlined,
}

/**
 * 선택 여부를 체크 아이콘과 컨테이너 색상으로 표현하는 공통 인디케이터입니다.
 *
 * 이 컴포넌트는 시각적 상태만 담당합니다. 클릭 처리와 체크박스 역할을 나타내는
 * 접근성 semantics는 인디케이터를 사용하는 상위 컴포넌트에서 제공합니다.
 *
 * @param checked 현재 선택 여부입니다.
 * @param modifier 외부 레이아웃과 상호작용을 조정하는 [Modifier]입니다.
 * @param style 인디케이터의 크기와 비선택 상태 표현을 결정하는 스타일입니다.
 */
@Composable
fun CheckIndicator(
    checked: Boolean,
    modifier: Modifier = Modifier,
    style: CheckIndicatorStyle = CheckIndicatorStyle.Filled,
) {
    val colors = MaterialTheme.linkuColors
    val containerSize = when (style) {
        CheckIndicatorStyle.Filled -> 18.dp
        CheckIndicatorStyle.Outlined -> 20.dp
    }
    val shape = RoundedCornerShape(
        when (style) {
            CheckIndicatorStyle.Filled -> 5.dp
            CheckIndicatorStyle.Outlined -> 6.dp
        },
    )
    val showCheckIcon = checked || style == CheckIndicatorStyle.Filled

    Box(
        modifier = modifier
            .size(containerSize)
            .clip(shape)
            .background(
                color = if (checked) {
                    colors.purple[200]
                } else {
                    when (style) {
                        CheckIndicatorStyle.Filled -> colors.gray[300]
                        CheckIndicatorStyle.Outlined -> colors.white
                    }
                },
            )
            .then(
                if (!checked && style == CheckIndicatorStyle.Outlined) {
                    Modifier.border(
                        width = 1.dp,
                        color = colors.gray[200],
                        shape = shape,
                    )
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (showCheckIcon) {
            Image(
                modifier = Modifier.size(
                    width = if (style == CheckIndicatorStyle.Filled) 11.dp else 12.dp,
                    height = if (style == CheckIndicatorStyle.Filled) 8.dp else 9.dp,
                ),
                painter = painterResource(R.drawable.ic_check_indicator_mark),
                contentDescription = null,
            )
        }
    }
}

/** 공통 체크 인디케이터의 스타일별 선택 상태를 확인하는 프리뷰입니다. */
@Preview(
    name = "CheckIndicator - States",
    showBackground = true,
)
@Composable
private fun CheckIndicatorPreview() {
    LinkuPreview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CheckIndicator(checked = true)
            CheckIndicator(checked = false)
            CheckIndicator(
                checked = true,
                style = CheckIndicatorStyle.Outlined,
            )
            CheckIndicator(
                checked = false,
                style = CheckIndicatorStyle.Outlined,
            )
        }
    }
}
