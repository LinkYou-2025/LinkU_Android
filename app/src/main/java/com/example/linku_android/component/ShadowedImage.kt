package com.example.linku_android.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShadowedImage(
    @DrawableRes id: Int,
    contentDescription: String?,
    width: Dp,
    height: Dp,
    shadowColor: Color = Color.Black.copy(alpha = 0.25f),
    shadowBlur: Dp = 4.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 2.dp,
) {
    Box {
        Icon(
            painter = painterResource(id = id),
            contentDescription = null,
            modifier = Modifier
                .width(width)
                .height(height)
                .offset(x = offsetX, y = offsetY)
                .blur(
                    radius = shadowBlur,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                ),
            tint = shadowColor,
        )
        Image(
            painter = painterResource(id = id),
            contentDescription = contentDescription,
            modifier = Modifier
                .width(width)
                .height(height)
        )
    }
}
