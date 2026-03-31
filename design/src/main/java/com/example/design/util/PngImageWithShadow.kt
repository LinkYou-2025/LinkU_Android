package com.example.design.util

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun OuterShadowResourceImage(
    resId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shadowOffsetX: Int = 0,
    shadowOffsetY: Int = -4,
    shadowBlurDp: Int = 10,
    shadowColor: Color = Color.Black.copy(alpha = 0.05f),
) {
    val bitmap = ImageBitmap.imageResource(id = resId)
    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
    val density = LocalDensity.current

    val shadowPadding = remember(shadowOffsetX, shadowOffsetY, shadowBlurDp) {
        // 블러 퍼짐 + 오프셋으로 인해 필요한 여유 공간
        max(
            shadowBlurDp * 3,
            max(abs(shadowOffsetX), abs(shadowOffsetY)) + shadowBlurDp * 3
        )
    }.dp

    BoxWithConstraints(
        modifier = modifier.aspectRatio(aspectRatio)
    ) {
        val imageWidth = maxWidth
        val imageHeight = maxHeight

        val shadowLayerWidth = imageWidth + shadowPadding * 2
        val shadowLayerHeight = imageHeight + shadowPadding * 3

        val shadowPaddingPx = with(density) { shadowPadding.roundToPx() }

        // 1) 바깥으로 퍼질 수 있게 "더 큰" 그림자 레이어
        Box(
            modifier = Modifier
                .size(shadowLayerWidth, shadowLayerHeight)
                .align(Alignment.Center)
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                    translationX = shadowOffsetX.dp.toPx()
                    translationY = shadowOffsetY.dp.toPx()
                }
                .blur(shadowBlurDp.dp)
        ) {
            // 그림자 실루엣용 이미지
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .size(imageWidth, imageHeight)
                    .align(Alignment.Center),
                colorFilter = ColorFilter.tint(shadowColor)
            )

            // 실루엣 내부를 지워서 "바깥 그림자"만 남김
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawImage(
                    image = bitmap,
                    dstOffset = IntOffset(shadowPaddingPx, shadowPaddingPx),
                    dstSize = IntSize(
                        width = imageWidth.roundToPx(),
                        height = imageHeight.roundToPx()
                    ),
                    blendMode = BlendMode.Clear
                )
            }
        }

        // 2) 실제 원본 이미지
        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

/*@Composable
fun OuterShadowResourceImage(
    resId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shadowOffsetX: Int = 0,
    shadowOffsetY: Int = -4,
    shadowBlurDp: Int = 10,
    shadowColor: Color = Color.Black.copy(alpha = 0.05f),
) {
    val bitmap = ImageBitmap.imageResource(id = resId)
    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()

    Box(
        modifier = modifier
            // 원래 Image가 하던 "비율에 맞는 높이 계산"을 직접 해줌
            .aspectRatio(aspectRatio)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                // Clear가 정상 동작하려면 오프스크린 합성이 필요함
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
        ) {
            // 1. 그림자 실루엣
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .blur(shadowBlurDp.dp)
                    .graphicsLayer {
                        translationX = shadowOffsetX.dp.toPx()
                        translationY = shadowOffsetY.dp.toPx()
                    },
                colorFilter = ColorFilter.tint(shadowColor)
            )

            // 2. 원본 내부 제거
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                drawImage(
                    image = bitmap,
                    dstOffset = IntOffset.Zero,
                    dstSize = IntSize(
                        width = size.width.roundToInt(),
                        height = size.height.roundToInt()
                    ),
                    blendMode = BlendMode.Clear
                )
            }
        }

        // 3. 실제 원본 이미지
        Image(
            painter = painterResource(resId),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxWidth()
                //.matchParentSize()
        )
    }
}*/

@Composable
fun PngImageWithShadow(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        // 그림자 레이어
        Image(
            painter = painter,
            contentDescription = contentDescription + "의 그림자",
            modifier = Modifier
                .matchParentSize() // 🔥 핵심: 부모 크기 그대로 따라감
                .graphicsLayer {
                    scaleX = 1.05f
                    scaleY = 1.05f
                }
                .offset(y = (-4).dp)
                .blur(10.dp),
                colorFilter = ColorFilter.tint(
                    Color.Black.copy(alpha = 0.05f)
                )
        )

        // 원본 이미지
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.matchParentSize()
        )
    }
}