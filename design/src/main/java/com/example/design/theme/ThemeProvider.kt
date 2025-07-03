package com.example.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.example.design.theme.color.Basic
import com.example.design.theme.color.ThemeColorScheme
import com.example.design.theme.font.Paperlogy
import com.example.design.theme.font.ThemeFontScheme
import com.example.design.theme.font.getTypography

val LocalColorTheme = compositionLocalOf<ThemeColorScheme> { Basic }
val LocalFontTheme = compositionLocalOf<ThemeFontScheme> { Paperlogy }

@Composable
fun ThemeProvider(
    colorScheme: ThemeColorScheme = Basic,
    fontScheme: ThemeFontScheme = Paperlogy,
    content: @Composable () -> Unit,
) {
    val currentTypography = MaterialTheme.typography

    CompositionLocalProvider(
        LocalColorTheme provides colorScheme,
        LocalFontTheme provides fontScheme,
    ) {
        MaterialTheme(
            typography = remember(key1 = fontScheme, key2 = currentTypography) {
                getTypography(
                    currentTypography = currentTypography,
                    font = fontScheme.font,
                )
            },
        ) {
            content.invoke()
        }
    }
}