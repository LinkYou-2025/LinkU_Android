package com.linku.design.theme.color

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
        20 to Color(0xFFE5EDFF), // 큐레이션 감정 프로그래스바에 사용
        30 to Color(0xFFC7D9FF), // 큐레이션 감정 프로그래스바에 사용
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