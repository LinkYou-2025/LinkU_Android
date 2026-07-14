package com.linku.design.modifier

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.skeleton(
    isLoading: Boolean,
    baseColor: Color = Color.LightGray.copy(alpha = 0.3f),
    highlightColor: Color = Color.White.copy(alpha = 0.6f),
    durationMillis: Int = 1000
): Modifier = composed {

    if (!isLoading) {
        return@composed this
    }

    val transition = rememberInfiniteTransition(
        label = "skeleton"
    )

    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            )
        ),
        label = "skeletonProgress"
    )

    this
        .graphicsLayer {
            /*
             * 컴포넌트를 별도의 버퍼에 그립니다.
             *
             * BlendMode가 화면의 다른 컴포넌트나 배경에 영향을 주지 않고
             * 현재 컴포넌트 안에서만 동작하도록 하기 위해 필요합니다.
             */
            compositingStrategy = CompositingStrategy.Offscreen
        }
        .drawWithCache {
            val width = size.width
            val height = size.height

            val brush = Brush.linearGradient(
                colors = listOf(
                    baseColor,
                    highlightColor,
                    baseColor
                ),
                start = Offset(
                    x = progress.value * width - width,
                    y = 0f
                ),
                end = Offset(
                    x = progress.value * width,
                    y = height
                )
            )

            onDrawWithContent {
                /*
                 * 먼저 원본 컴포넌트를 그립니다.
                 * 이 원본의 알파값이 마스크 역할을 합니다.
                 */
                drawContent()

                /*
                 * 원본 컴포넌트가 실제로 그려진 픽셀에만
                 * 그라데이션을 남깁니다.
                 */
                drawRect(
                    brush = brush,
                    blendMode = BlendMode.SrcIn
                )
            }
        }
}