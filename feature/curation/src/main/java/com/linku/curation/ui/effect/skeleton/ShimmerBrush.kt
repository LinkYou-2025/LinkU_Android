package com.linku.curation.ui.effect.skeleton

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
private fun shimmerAnimation(): Float {
    val transition = rememberInfiniteTransition(label = "shimmer")

    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    return translate
}

/** 기본 그레이 쉬머 */
@Composable
fun grayShimmerBrush(): Brush {
    val translate = shimmerAnimation()

    return Brush.linearGradient(
        colors = listOf(
            Color(0xFFF4F5F7),
            Color(0xFFE9EAEE),
            Color(0xFFF4F5F7)
        ),
        start = Offset(translate - 300f, 0f),
        end = Offset(translate, 0f)
    )
}

/** 밝은 핑크 쉬머 */
@Composable
fun pinkShimmerBrush(): Brush {
    val translate = shimmerAnimation()

    return Brush.linearGradient(
        colors = listOf(
            Color(0xFFFBEEFF),
            Color(0xFFE3A3F5),
            Color(0xFFFBEEFF)
        ),
        start = Offset(translate - 300f, 0f),
        end = Offset(translate, 0f)
    )
}
