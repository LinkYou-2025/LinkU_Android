package com.linku.curation.ui.util

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors

private const val SWEEP_ANGLE = 270f

/**
 * 큐레이션 전용 그라디언트 서클 프로그레스 바 (로딩 스피너)
 *
 * [maincolor] 브러시(blue → purple 수평 그라디언트) 아크가 트랙 위를 무한 회전한다.
 *
 * @param size 컴포넌트 전체 크기
 * @param strokeWidth 링 두께
 * @param trackColor 배경 트랙 색상. 미지정 시 gray[200] 사용
 */
@Composable
fun CurationCircleProgressBar(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    strokeWidth: Dp = 4.dp,
    trackColor: Color = Color.Unspecified,
) {
    val colors = MaterialTheme.linkuColors
    val resolvedTrackColor = trackColor.takeOrElse { colors.gray[200] }

    val infiniteTransition = rememberInfiniteTransition(label = "circleProgress")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.size(size)) {
        val strokePx = strokeWidth.toPx()
        val inset = strokePx / 2f
        val arcSize = Size(
            width = this.size.width - strokePx,
            height = this.size.height - strokePx
        )
        val topLeft = Offset(inset, inset)

        // 배경 트랙
        drawArc(
            color = resolvedTrackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )

        // maincolor 그라디언트 프로그레스 아크 (무한 회전)
        drawArc(
            brush = colors.maincolor,
            startAngle = rotation - 90f,
            sweepAngle = SWEEP_ANGLE,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCurationCircleProgressBar() {
    LinkuPreview {
        CurationCircleProgressBar()
    }
}
