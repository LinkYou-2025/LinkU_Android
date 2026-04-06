package com.linku.curation.ui.util

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun Modifier.shimmer(): Modifier {
    val transition = rememberInfiniteTransition()

    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f, // 충분히 크게
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    return this.then(
        Modifier.drawWithCache {
            val brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFEDEDED),
                    Color(0xFFF7F7F7),
                    Color(0xFFEDEDED),
                ),
                start = Offset(shift - size.width, 0f),
                end = Offset(shift, size.height)
            )

            onDrawBehind {
                drawRect(brush)
            }
        }
    )
}