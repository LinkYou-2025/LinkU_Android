package com.linku.curation.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors

/**
 * 스크롤되는 화면 컨텐츠 뒤에 고정되는 그라데이션 배경.
 *
 * 스크롤 컨테이너(LazyColumn 등)를 [content]에 넣으면, 배경은 스크롤 대상이 아니라서
 * 화면을 내려도 그라데이션이 계속 고정된 채로 보임.
 *
 * MainScreen의 Scaffold가 이 화면에도 하단 시스템 내비게이션 바 높이만큼 padding을 이미
 * 적용해 두어서, 그 여백에는 Scaffold의 흰색 배경이 그대로 비쳐 안드로이드 내비게이션 바와
 * 대비가 심하게 보임. 배경 레이어만 그 여백 아래로 확장해 내비게이션 바 뒤까지 이어지게 하고,
 * [content]는 원래 안전 영역 안에 그대로 둠(버튼 등이 내비게이션 바에 가려지지 않도록).
 *
 * @param brush 배경 그라데이션. 카드별로 다른 그라데이션(#42-2, #42-3 등)을 쓸 때 교체 가능.
 * @param content 배경 위에 그릴 스크롤 컨텐츠.
 */
@Composable
internal fun CurationFixedGradientBackground(
    modifier: Modifier = Modifier,
    brush: Brush = MaterialTheme.linkuColors.curationFirstCardBackground, // 나중에 확장 가능성을 고려해서 파라미터 받는 것으로 짬.
    content: @Composable BoxScope.() -> Unit = {}, // 스크롤 해도 배경은 고정하기 위해서 추가함. 더 좋은 의견이 있다면 언제든 환영입니다.
) {
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(modifier = modifier.fillMaxSize()) {
        // Modifier.padding()에는 음수 값을 못 넣으므로(IllegalArgumentException), 배경만
        // navigationBarHeight만큼 더 크게 측정한 뒤 그 높이 그대로 배치해서 아래로 넘치게(bleed)
        // 그림. 리포트되는 크기는 원래 칸(H)만큼으로 유지해서 형제인 content()의 배치는 그대로 둠.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .layout { measurable, constraints ->
                    val extraHeight = navigationBarHeight.roundToPx()
                    val placeable = measurable.measure(
                        constraints.copy(
                            minHeight = (constraints.minHeight + extraHeight).coerceAtLeast(0),
                            maxHeight = if (constraints.hasBoundedHeight) {
                                (constraints.maxHeight + extraHeight).coerceAtLeast(0)
                            } else {
                                constraints.maxHeight
                            }
                        )
                    )
                    layout(placeable.width, placeable.height - extraHeight) {
                        placeable.placeRelative(0, 0)
                    }
                }
                .background(brush)
        )
        content()
    }
}

@Preview(name = "큐레이션 카드 상세 고정 배경 (#42-1)", showBackground = true)
@Composable
private fun CurationFixedGradientBackgroundPreview() {
    LinkuPreview {
        CurationFixedGradientBackground()
    }
}
