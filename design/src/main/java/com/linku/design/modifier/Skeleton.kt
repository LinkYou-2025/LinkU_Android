package com.linku.design.modifier

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

/**
 * 로딩 상태의 컴포저블에 스켈레톤 shimmer 효과를 적용하는 [Modifier]입니다.
 *
 * 이 modifier는 대상 컴포저블의 실제 콘텐츠를 먼저 그린 뒤, 그 콘텐츠가 차지하는 픽셀 영역만
 * 그라데이션으로 덮어 shimmer처럼 보이게 만듭니다. 따라서 이 modifier를 적용한 컴포저블의
 * 크기, 모양, clip, alpha는 그대로 스켈레톤 마스크의 기준이 됩니다.
 *
 * [isLoading]이 `false`이면 추가 draw 단계와 무한 애니메이션을 만들지 않고 기존 [Modifier]
 * 체인을 그대로 반환합니다. 로딩이 끝난 뒤 실제 UI를 보여줄 때도 같은 modifier 체인을 유지할 수
 * 있도록 하기 위한 동작입니다.
 *
 * 내부에서는 [CompositingStrategy.Offscreen]으로 별도 버퍼에 대상 컴포저블을 먼저 렌더링한 뒤,
 * [BlendMode.SrcIn]을 사용해 실제로 그려진 픽셀 영역에만 [Brush.linearGradient]를 합성합니다.
 * 이 처리가 없으면 shimmer 그라데이션이 현재 컴포저블 바깥이나 다른 컴포저블의 배경까지 영향을
 * 줄 수 있습니다.
 *
 * 사용 예:
 * ```
 * Box(
 *     modifier = Modifier
 *         .size(width = 120.dp, height = 16.dp)
 *         .clip(RoundedCornerShape(4.dp))
 *         .skeleton(isLoading = uiState.isLoading)
 * )
 * ```
 *
 * @param isLoading `true`이면 shimmer 스켈레톤을 표시하고, `false`이면 원본 modifier를 그대로 사용합니다.
 * @param baseColor 스켈레톤의 기본 배경색입니다. 그라데이션의 양 끝 색상으로 사용됩니다.
 * @param highlightColor shimmer가 지나가는 중앙 하이라이트 색상입니다.
 * @param durationMillis shimmer 그라데이션이 한 번 이동하는 데 걸리는 시간입니다.
 */
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
             * 현재 컴포저블을 별도 버퍼에 먼저 그립니다.
             *
             * BlendMode가 화면 전체나 다른 컴포저블의 배경에 영향을 주지 않고,
             * 현재 컴포저블 내부에서만 동작하도록 하기 위해 필요합니다.
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
                 * 먼저 원본 컴포저블을 그립니다.
                 * 이 원본의 알파 값이 그라데이션을 입힐 마스크가 됩니다.
                 */
                drawContent()

                /*
                 * 원본 컴포저블이 실제로 그려진 픽셀 영역에만 그라데이션을 합성합니다.
                 */
                drawRect(
                    brush = brush,
                    blendMode = BlendMode.SrcIn
                )
            }
        }
}
