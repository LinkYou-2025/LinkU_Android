package com.example.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.design.theme.color.Basic
import com.example.design.theme.color.ThemeColorScheme
import com.example.design.theme.font.Paperlogy
import com.example.design.theme.font.ThemeFontScheme
import com.example.design.theme.font.getTypography
import com.example.design.util.LocalFigmaDimens
import com.example.design.util.rememberFigmaDimens

val LocalColorTheme = staticCompositionLocalOf<ThemeColorScheme> { Basic }
val LocalFontTheme = staticCompositionLocalOf<ThemeFontScheme> { Paperlogy }

// 링큐 지정 색상 호출 프로퍼티
val MaterialTheme.linkuColors: ThemeColorScheme
    @Composable get() = LocalColorTheme.current

// 링큐 지정 폰트 호출 프로퍼티
val MaterialTheme.linkuFont: ThemeFontScheme
    @Composable get() = LocalFontTheme.current

@Composable
fun ThemeProvider(
    colorScheme: ThemeColorScheme = Basic,
    fontScheme: ThemeFontScheme = Paperlogy,
    content: @Composable () -> Unit,
) {
    val currentTypography = MaterialTheme.typography

    // scaler 함수 생성
    val figmaScale = rememberFigmaDimens()

    val typography = remember(key1 = fontScheme, key2 = currentTypography) {
        getTypography(
            currentTypography = currentTypography,
            font = fontScheme.font,
        )
    }

    CompositionLocalProvider(
        LocalColorTheme provides colorScheme,
        LocalFontTheme provides fontScheme,
        LocalFigmaDimens provides figmaScale
    ) {
        MaterialTheme(
            typography = typography,
            content = content
        )
    }
}