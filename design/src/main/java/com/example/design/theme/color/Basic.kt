package com.example.design.theme.color

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data object Basic: ThemeColorScheme(
//    isDark = false,
    maincolor= Brush.horizontalGradient(
        listOf(
            Color(0xFF2C6FFF),
            Color(0xFFC800FF) //수정함.
        )
    ),
    backgroundmaincolor = Brush.horizontalGradient(
        listOf(
            Color(0xFF2C6FFF).copy(0.1f),
            Color(0xFFCB59EB).copy(0.1f)
        )
    ),
    blue = ColorMap(
        50 to Color(0xFFE5EDFF),
        100 to Color(0xFF95B6FF),
        200 to Color(0xFF2C6FFF),
        300 to Color(0xFF1451D5),
    ),
    purple = ColorMap(
        50 to Color(0xFFFBEFFF),
        100 to Color(0xFFE5ACF4),
        200 to Color(0xFFCB59EB),
        300 to Color(0xFF9A3AB5),
    ),
)