package com.linku.design.theme.font

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.linku.design.R

// 큐레이션 화면 달력 아이콘 폰트체 추가했습니다.

data object Laundrygothic : ThemeFontScheme(
    font = FontFamily(
        Font(R.font.laundrygothic_bold, FontWeight.Bold, FontStyle.Normal),
        Font(R.font.laundrygothic_regular, FontWeight.Normal, FontStyle.Normal)
    )
)
