package com.example.design.theme.color

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

sealed class ThemeColorScheme(
//    val isDark: Boolean,

    val maincolor: Brush,
    val blue: ColorMap = ColorMap(
        50 to Color(0xFFE5EDFF),
        100 to Color(0xFF95B6FF),
        200 to Color(0xFF2C6FFF),
        300 to Color(0xFF1451D5),
    ),
    val purple: ColorMap = ColorMap(
        50 to Color(0xFFFBEFFF),
        100 to Color(0xFFE5ACF4),
        200 to Color(0xFFCB59EB),
        300 to Color(0xFF9A3AB5),
    ),
    val gray: ColorMap = ColorMap(
        100 to Color(0xFFF5F6F9),
        200 to Color(0xFFE9EAEE),
        300 to Color(0xFFD7D9DF),
        400 to Color(0xFFB7B9BF),
        500 to Color(0xFFA1A3A9),
        600 to Color(0xFF87898F),
        700 to Color(0xFF5F6167),
        800 to Color(0xFF43454B),
    ),

    val black: Color = Color(0xFF000208),
    val white: Color = Color(0xFFFFFFFF),
    val positive: Color = Color(0xFF35DF79),
    val negative: Color = Color(0xFFFF5E5E),
)