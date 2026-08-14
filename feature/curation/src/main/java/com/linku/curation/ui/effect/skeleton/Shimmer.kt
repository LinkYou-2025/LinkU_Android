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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.linku.design.theme.linkuColors

/**
 * 로딩 플레이스홀더용 쉬머(shimmer) 애니메이션 배경을 그려주는 Modifier.
 *
 * [shape]에 clip한 뒤, 정지된 베이스 그라데이션([baseColors]) 위에 밝은 하이라이트 띠([highlightColor])가
 * 대각선으로 흐르며 로딩 중임을 알린다. 베이스만 있으면(예: gray100~gray200처럼 명도 차가 작은 조합)
 * 애니메이션이 있어도 거의 정지된 것처럼 보이기 때문에, 눈에 띄는 별도의 하이라이트 레이어를 얹는다.
 *
 * 원형(CircleShape), 라운드 사각형(RoundedCornerShape), 사각형(RectangleShape) 등 어디에나 적용할 수 있다.
 *
 * @param shape 스켈레톤 영역의 모양 (기본값: RectangleShape)
 * @param baseColors 정지 상태의 베이스 그라데이션 색상 목록. [MaterialTheme.grayShimmerColors]/[MaterialTheme.pinkShimmerColors]로 교체 가능 (기본값: 회색 계열)
 * @param highlightColor 흐르는 하이라이트 띠의 색상 (기본값: 테마 white)
 */
@Composable
fun Modifier.shimmer(
    shape: Shape = RectangleShape,
    baseColors: List<Color> = MaterialTheme.grayShimmerColors,
    highlightColor: Color = MaterialTheme.linkuColors.white
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
        .drawBehind {
            drawRect(Brush.linearGradient(baseColors))

            val highlightBrush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    highlightColor.copy(alpha = 0.9f),
                    Color.Transparent
                ),
                start = Offset(shift - size.width, 0f),
                end = Offset(shift, size.height)
            )
            drawRect(highlightBrush)
        }
}

/**
 * 기본 그레이 쉬머 베이스 색상 (카드, 텍스트 라인 등 범용).
 * gray100~200(F4F5F7~E9EAEE)은 흰 하이라이트와 명도 차가 거의 없어 움직임이 묻히므로,
 * [com.linku.design.modifier.skeleton]의 LightGray 베이스를 참고해 더 진한 gray300~400을 쓴다.
 */
internal val MaterialTheme.grayShimmerColors: List<Color>
    @Composable get() = listOf(linkuColors.gray[300], linkuColors.gray[400])

/** 밝은 핑크 쉬머 베이스 색상 (큐레이션 디테일 화면용) */
internal val MaterialTheme.pinkShimmerColors: List<Color>
    @Composable get() = listOf(linkuColors.purple[50], linkuColors.purple[100])
