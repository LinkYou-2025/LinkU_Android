package com.linku.design.modifier

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ShadowStyle(
    val elevation: Dp,
    val shape: Shape = RectangleShape,
    val clip: Boolean = elevation > 0.dp,
    val ambientColor: Color = DefaultShadowColor,
    val spotColor: Color = DefaultShadowColor
)

fun Modifier.shadow(
    style: ShadowStyle
): Modifier = shadow(
    elevation = style.elevation,
    shape = style.shape,
    clip = style.clip,
    ambientColor = style.ambientColor,
    spotColor = style.spotColor
)