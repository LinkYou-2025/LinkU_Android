package com.linku.curation.ui.effect.highlight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.linku.design.theme.linkuColors
import com.linku.design.util.LocalFigmaDimens
import com.linku.design.util.rememberFigmaDimens
import com.linku.design.util.scaler

// 큐레이션 대신 해주는데 기본으로 쓰는 값들은 넣어 드려야지....
@Composable
fun RadialGradientCircle(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.linkuColors.accentColor,
    alpha: Float = 0.12f,
    size: Dp = 321.scaler
) {
    val radiusPx = with(LocalDensity.current) {
        (size / 2).toPx()
    }

    Box(
        modifier = modifier
            .requiredSize(size)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0f to color.copy(alpha = alpha),
                        1f to Color.Transparent
                    ),
                    center = Offset(radiusPx, radiusPx),
                    radius = radiusPx
                )
            )
    )
}
@Preview(showBackground = true)
@Composable
private fun RadialGradientCirclePreview() {
    CompositionLocalProvider(
        LocalFigmaDimens provides rememberFigmaDimens()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEDEDED)), //귀찮아서 프리뷰는 하드 코딩 그대로 놓음... 귀찮아...
            contentAlignment = Alignment.Center
        ) {
            RadialGradientCircle(
                color = Color(0xFF2C6FFF),
                modifier = Modifier.offset(
                    x = (-88).scaler,
                    y = (-12).scaler
                )
            )

        }
    }
}