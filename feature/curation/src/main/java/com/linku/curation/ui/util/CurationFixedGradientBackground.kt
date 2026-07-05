package com.linku.curation.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors

/**
 * 스크롤되는 화면 컨텐츠 뒤에 고정되는 그라데이션 배경.
 *
 * 스크롤 컨테이너(LazyColumn 등)를 [content]에 넣으면, 배경은 스크롤 대상이 아니라서
 * 화면을 내려도 그라데이션이 계속 고정된 채로 보임.
 *
 * @param brush 배경 그라데이션. 카드별로 다른 그라데이션(#42-2, #42-3 등)을 쓸 때 교체 가능.
 * @param content 배경 위에 그릴 스크롤 컨텐츠.
 */
@Composable
fun CurationFixedGradientBackground(
    modifier: Modifier = Modifier,
    brush: Brush = MaterialTheme.linkuColors.curationFirstCardBackground, // 나중에 확장 가능성을 고려해서 파라미터 받는 것으로 짬.
    content: @Composable BoxScope.() -> Unit = {}, // 스크롤 해도 배경은 고정하기 위해서 추가함. 더 좋은 의견이 있다면 언제든 환영입니다.
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush),
        content = content,
    )
}

@Preview(name = "큐레이션 카드 상세 고정 배경 (#42-1)", showBackground = true)
@Composable
private fun CurationFixedGradientBackgroundPreview() {
    LinkuPreview {
        CurationFixedGradientBackground()
    }
}
