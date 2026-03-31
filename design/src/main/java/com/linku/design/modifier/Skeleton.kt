package com.linku.design.modifier

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linku.design.theme.linkuColors
import com.linku.design.theme.linkuFont

/**
 * UI 구성 요소에 스켈레톤(Skeleton) 로딩 효과를 적용합니다.
 *
 * [isLoading]이 true일 때, 선형 그라데이션 애니메이션을 콘텐츠 위에 덮어씌워
 * 데이터가 로딩 중임을 나타내는 플레이스홀더 효과를 생성합니다.
 *
 * @param isLoading 스켈레톤 효과를 활성화할지 여부입니다.
 * @param baseColor 스켈레톤의 기본 배경색입니다.
 * @param highlightColor 스켈레톤 위를 지나가는 하이라이트(반짝임)의 색상입니다.
 * @param durationMillis 하이라이트 애니메이션이 한 번 반복되는 데 걸리는 시간(밀리초)입니다.
 * @return 스켈레톤 효과가 적용된 [Modifier]를 반환합니다.
 *
 * @sample
 * val isLoading = remember { mutableStateOf(false) }
 * Box(
 *     modifier = Modifier
 *         .fillMaxSize()
 *         .skeleton(isLoading = isLoading)
 * )
 */
fun Modifier.skeleton(
    isLoading: Boolean,
    baseColor: Color = Color.LightGray.copy(alpha = 0.3f),
    highlightColor: Color = Color.White.copy(alpha = 0.6f),
    durationMillis: Int = 1000
): Modifier = composed {

    if (!isLoading) return@composed this

    // 애니메이션 상태
    val transition = rememberInfiniteTransition(label = "skeleton")

    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing)
        ),
        label = "progress"
    )

    this.drawWithCache {

        val width = size.width
        val height = size.height

        val brush = Brush.linearGradient(
            colors = listOf(baseColor, highlightColor, baseColor),
            start = Offset(progress.value * width - width, 0f),
            end = Offset(progress.value * width, height)
        )

        onDrawWithContent {
            // 기존 UI를 덮어버림 (skeleton 상태)
            drawRect(brush)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SkeletonPreview() {
    val isLoading = remember { mutableStateOf(false) }

    Text(
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable {
                isLoading.value = !isLoading.value
            }
            .skeleton(
                isLoading = isLoading.value
            ),
        text = "Click",
        textAlign = TextAlign.Center,
        fontSize = 25.sp,
        fontFamily = MaterialTheme.linkuFont.font,
        color = MaterialTheme.linkuColors.black
    )
}