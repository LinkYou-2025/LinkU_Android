package com.linku.curation.ui.effect.highlight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.linku.design.util.LocalFigmaDimens
import com.linku.design.util.rememberFigmaDimens
import com.linku.design.util.scaler

/**
 * Figma Ellipse 541
 * - Size: 321 x 321 (Figma 기준)
 * - Radial Gradient
 * - Color: #2C6FFF (12% → 0%)
 * - Background decorative component
 */
@Composable
fun RadialGradientCircle(
    modifier: Modifier = Modifier,
    color: Color,
    alpha: Float = 0.12f,   // 기본값: 피그마 12%
    size: Dp = 321.scaler
) {
    val radiusPx = with(LocalDensity.current) {
        (size / 2).toPx()
    }

    Box(
        modifier = modifier
            .requiredSize(size)   // 부모 constraint 무시하고 강제로 정사각형 유지
            .alpha(alpha)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0f to color,          // 중심 100%
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
                .background(Color(0xFFEDEDED)),
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