package com.example.design.theme.font

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily

fun getTypography(currentTypography: Typography, font: FontFamily): Typography {
    return Typography(
        displayLarge = currentTypography.displayLarge.copy(fontFamily = font),
        displayMedium = currentTypography.displayMedium.copy(fontFamily = font),
        displaySmall = currentTypography.displaySmall.copy(fontFamily = font),
        headlineLarge = currentTypography.headlineLarge.copy(fontFamily = font),
        headlineMedium = currentTypography.headlineMedium.copy(fontFamily = font),
        headlineSmall = currentTypography.headlineSmall.copy(fontFamily = font),
        titleLarge = currentTypography.titleLarge.copy(fontFamily = font),
        titleMedium = currentTypography.titleMedium.copy(fontFamily = font),
        titleSmall = currentTypography.titleSmall.copy(fontFamily = font),
        bodyLarge = currentTypography.bodyLarge.copy(fontFamily = font),
        bodyMedium = currentTypography.bodyMedium.copy(fontFamily = font),
        bodySmall = currentTypography.bodySmall.copy(fontFamily = font),
    )
}