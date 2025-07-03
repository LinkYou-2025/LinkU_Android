package com.example.design.theme.font

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.design.R

data object Paperlogy: ThemeFontScheme(
    font = FontFamily(
        Font(R.font.paperlogy_bold, FontWeight.Bold),
        Font(R.font.paperlogy_medium, FontWeight.Medium),
        Font(R.font.paperlogy_regular, FontWeight.Normal),
        Font(R.font.paperlogy_light, FontWeight.Light),
    )
)