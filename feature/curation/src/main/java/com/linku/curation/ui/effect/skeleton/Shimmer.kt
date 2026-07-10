package com.linku.curation.ui.effect.skeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.linku.design.theme.linkuColors

/**
 * 로딩 플레이스홀더용 쉬머(shimmer) 애니메이션 배경을 그려주는 Modifier.
 *
 * [shape]에 clip한 뒤 애니메이션 그라데이션을 그리기 때문에 원형(CircleShape),
 * 라운드 사각형(RoundedCornerShape), 사각형(RectangleShape) 등 어디에나 적용할 수 있다.
 *
 * @param shape 스켈레톤 영역의 모양 (기본값: RectangleShape)
 * @param colors 쉬머 그라데이션 색상 목록. [grayShimmerColors]/[pinkShimmerColors]로 교체 가능 (기본값: 회색 계열)
 */
@Composable
fun Modifier.shimmer(
    shape: Shape = RectangleShape,
    colors: List<Color> = grayShimmerColors()
): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing), // 여기서 이징 써보겠따!!
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerShift"
    )

    return this
        .clip(shape)
        .drawWithCache {
            val brush = Brush.linearGradient(
                colors = colors,
                start = Offset(shift - size.width, 0f),
                end = Offset(shift, size.height)
            )

            onDrawBehind {
                drawRect(brush)
            }
        }
}

/** 기본 그레이 쉬머 색상 (카드, 텍스트 라인 등 범용) */
@Composable
fun grayShimmerColors(): List<Color> {
    val colorTheme = MaterialTheme.linkuColors
    return listOf(colorTheme.gray[100], colorTheme.gray[200], colorTheme.gray[100])
}

/** 밝은 핑크 쉬머 색상 (큐레이션 디테일 화면용) */
@Composable
fun pinkShimmerColors(): List<Color> {
    val colorTheme = MaterialTheme.linkuColors
    return listOf(colorTheme.purple[50], colorTheme.purple[100], colorTheme.purple[50])
}
