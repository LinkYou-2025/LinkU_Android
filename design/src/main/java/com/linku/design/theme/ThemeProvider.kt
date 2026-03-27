package com.linku.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.linku.design.theme.color.Basic
import com.linku.design.theme.color.ThemeColorScheme
import com.linku.design.theme.font.Paperlogy
import com.linku.design.theme.font.ThemeFontScheme
import com.linku.design.theme.font.getTypography
import com.linku.design.util.LocalFigmaDimens
import com.linku.design.util.rememberFigmaDimens

val LocalColorTheme = compositionLocalOf<ThemeColorScheme> { Basic }
val LocalFontTheme = compositionLocalOf<ThemeFontScheme> { Paperlogy }

@Composable
fun ThemeProvider(
    colorScheme: ThemeColorScheme = Basic,
    fontScheme: ThemeFontScheme = Paperlogy,
    content: @Composable () -> Unit,
) {
    val currentTypography = MaterialTheme.typography

    // scaler 함수 생성
    val figmaScale = rememberFigmaDimens()

    CompositionLocalProvider(
        LocalColorTheme provides colorScheme,
        LocalFontTheme provides fontScheme,
        LocalFigmaDimens provides figmaScale
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