package com.linku.curation.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.linku.curation.ui.effect.highlight.RadialGradientCircle
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

/**
 * 스크롤되는 화면 컨텐츠 뒤에 고정되는 핑크·파랑 그라데이션 원 배경 (#44-1-1).
 *
 * 스크롤 컨테이너를 [content]에 넣으면, 원은 스크롤 대상이 아니라서
 * 화면을 내려도 계속 같은 자리에 고정된 채로 보임.
 *
 * @param content 배경 위에 그릴 스크롤 컨텐츠.
 */
@Composable
internal fun CurationGradientCircleBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val colorTheme = MaterialTheme.linkuColors

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorTheme.white),
    ) {
        RadialGradientCircle(
            color = colorTheme.accentColor,
            alpha = 0.16f,
            size = 640.scaler,
            modifier = Modifier.offset(x = (-191).scaler, y = 59.scaler),
        )
        RadialGradientCircle(
            color = colorTheme.blue[200],
            alpha = 0.16f,
            size = 661.scaler,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 57.scaler, y = (-28).scaler),
        )
        content() // 여기가 핵심입니당
    }
}

@Preview(name = "큐레이션 카드 상세 그라데이션 원 고정 배경 (#44-1-1)", showBackground = true)
@Composable
private fun CurationGradientCircleBackgroundPreview() {
    LinkuPreview {
        CurationGradientCircleBackground()
    }
}
