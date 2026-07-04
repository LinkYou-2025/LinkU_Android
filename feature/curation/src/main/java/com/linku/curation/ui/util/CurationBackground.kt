package com.linku.curation.ui.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.linku.curation.R
import com.linku.curation.ui.effect.highlight.RadialGradientCircle
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

@Composable
fun CurationBackground(
    modifier: Modifier = Modifier,
    showLogo: Boolean = false,
) {
    val colorTheme = MaterialTheme.linkuColors

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // 상태바 위로 삐져나가면 창(window) 밖이라 그려질 공간이 없어 잘리므로,
        // y는 박스 상단 경계(0) 안쪽으로 당기고 x(좌우)만 Figma 값 그대로 유지함.
        RadialGradientCircle(
            color = colorTheme.accentColor,
            modifier = Modifier.offset(x = (-88).scaler)
        )
        RadialGradientCircle(
            color = colorTheme.blue[200],
            modifier = Modifier.offset(x = 24.scaler)
        )
        if (showLogo) {
            Image(
                painter = painterResource(id = R.drawable.img_curation_background_logo),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 41.scaler, y = 95.scaler)
                    .size(width = 197.scaler, height = 140.scaler)
            )
        }
    }
}

@Preview(name = "Pixel 8 Size", widthDp = 412, heightDp = 915, showBackground = true)
@Composable
fun PreviewCurationBackground() {
    LinkuPreview {
        CurationBackground(
            modifier = Modifier.fillMaxSize(),
            showLogo = true
        )
    }
}
