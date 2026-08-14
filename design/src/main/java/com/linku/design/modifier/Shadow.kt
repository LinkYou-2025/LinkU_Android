package com.linku.design.modifier

import android.graphics.BlurMaskFilter
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ShadowStyle(
    val elevation: Dp,
    val shape: Shape = RectangleShape,
    val clip: Boolean = elevation > 0.dp,
    val ambientColor: Color = DefaultShadowColor,
    val spotColor: Color = DefaultShadowColor
)

fun Modifier.shadow(
    style: ShadowStyle
): Modifier = shadow(
    elevation = style.elevation,
    shape = style.shape,
    clip = style.clip,
    ambientColor = style.ambientColor,
    spotColor = style.spotColor
)

/**
 * Figma의 drop-shadow 필터를 Compose에서 재현하는 modifier.
 *
 * CSS/Figma: filter: drop-shadow(offsetX offsetY blur color)
 *
 * 사용 예) Figma 값 `drop-shadow(0 4px 15px rgba(0,0,0,0.03))` 적용:
 * ```
 * Modifier.dropShadow(
 *     shape = RoundedCornerShape(18.dp),
 *     color = Color(0x08000000), // rgba(0,0,0,0.03) → alpha=0.03*255≈8=0x08
 *     blur = 15.dp,
 *     offsetY = 4.dp,
 * )
 * ```
 */
fun Modifier.dropShadow(
    shape: Shape,
    color: Color,
    blur: Dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
): Modifier = drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                this.color = color.toArgb()
                if (blur.toPx() > 0f) {
                    maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
                }
            }
        }
        canvas.save()
        canvas.translate(offsetX.toPx(), offsetY.toPx())
        val outline = shape.createOutline(size, layoutDirection, this)
        canvas.drawOutline(outline, paint)
        canvas.restore()
    }
}
