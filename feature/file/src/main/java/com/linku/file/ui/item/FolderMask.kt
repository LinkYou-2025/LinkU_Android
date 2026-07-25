package com.linku.file.ui.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush

private val FolderMaskShape = GenericShape { size, _ ->
    val sx = size.width / 174f
    val sy = size.height / 116f

    fun x(value: Float) = value * sx
    fun y(value: Float) = value * sy

    moveTo(x(0f), y(25.77f))
    cubicTo(x(0f), y(11.96f), x(11.19f), y(0.77f), x(25f), y(0.77f))
    lineTo(x(46.8f), y(0.77f))
    cubicTo(x(51.82f), y(0.77f), x(56.56f), y(3.05f), x(59.68f), y(6.98f))
    cubicTo(x(62.81f), y(10.91f), x(67.55f), y(13.2f), x(72.57f), y(13.2f))
    lineTo(x(149f), y(13.2f))
    cubicTo(x(162.81f), y(13.2f), x(174f), y(24.39f), x(174f), y(38.2f))
    lineTo(x(174f), y(91f))
    cubicTo(x(174f), y(104.81f), x(162.81f), y(116f), x(149f), y(116f))
    lineTo(x(25f), y(116f))
    cubicTo(x(11.19f), y(116f), x(0f), y(104.81f), x(0f), y(91f))

    close()
}

@Composable
internal fun FolderMask(
    modifier: Modifier,
    brush: Brush,
) {
    Box(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(FolderMaskShape)
                .drawWithCache {
                    onDrawBehind {
                        drawRect(brush = brush)
                    }
                }
        )
    }
}
