package com.linku.curation.ui.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
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
fun BoxScope.CurationBackground(
    showLogo: Boolean = false
) {
    val colorTheme = MaterialTheme.linkuColors

    RadialGradientCircle(
        color = colorTheme.accentColor,
        modifier = Modifier.offset(x = (-88).scaler, y = (-12).scaler)
    )
    RadialGradientCircle(
        color = colorTheme.blue[200],
        modifier = Modifier.offset(x = 24.scaler, y = (-6).scaler)
    )
    if (showLogo) {
        Image(
            painter = painterResource(id = R.drawable.img_curation_logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 9.scaler, y = 95.scaler)
                .size(width = 197.scaler, height = 140.scaler)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCurationBackground() {
    LinkuPreview {
        Box(modifier = Modifier.fillMaxSize()) {
            CurationBackground(showLogo = true)
        }
    }
}
