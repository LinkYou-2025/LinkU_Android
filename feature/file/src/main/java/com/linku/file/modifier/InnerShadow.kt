package com.linku.file.modifier

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.innerShadow(
    color: Color = Color.Black,
    alpha: Float = 0.3f,
    blur: Dp = 10.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    cornerRadius: Dp = 0.dp
): Modifier = this.then(
    Modifier.drawWithContent {
        // 1. 원본 그리기
        drawContent()

        // 2. 내부 그림자 그리기
        val shadowColor = color.copy(alpha = alpha)
        val pxBlur = blur.toPx()
        val pxOffsetX = offsetX.toPx()
        val pxOffsetY = offsetY.toPx()
        val pxRadius = cornerRadius.toPx()

        // 3. 내부에 마스킹
        val shadowPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    cornerRadius = CornerRadius(pxRadius, pxRadius)
                )
            )
        }

        // 4. BlurMaskFilter로 그림자 처리 (Android API 전용)
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                this.color = shadowColor
                this.asFrameworkPaint().apply {
                    isAntiAlias = true
                    maskFilter = BlurMaskFilter(pxBlur, BlurMaskFilter.Blur.NORMAL)
                }
            }
            canvas.saveLayer(Rect(0f, 0f, size.width, size.height), paint)
            // 바깥쪽은 투명하게 처리
            canvas.drawPath(shadowPath, paint)
            canvas.restore()
        }

        // 5. 오프셋만큼 이동
        if (pxOffsetX != 0f || pxOffsetY != 0f) {
            drawIntoCanvas { canvas ->
                canvas.translate(pxOffsetX, pxOffsetY)
            }
        }
    }
)
