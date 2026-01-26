package com.example.design.theme.font

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.design.R

data object Taebaek : ThemeFontScheme(
    font = FontFamily(
        Font(R.font.taebaek_font, FontWeight.Normal, FontStyle.Normal)
    )
)