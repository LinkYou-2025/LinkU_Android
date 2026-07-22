package com.linku.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.linku.design.theme.color.Basic
import com.linku.design.theme.color.ThemeColorScheme
import com.linku.design.theme.font.Paperlogy
import com.linku.design.theme.font.ThemeFontScheme
import com.linku.design.theme.font.getTypography
import com.linku.design.util.LocalFigmaDimens
import com.linku.design.util.rememberFigmaDimens

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

    // 기기의 시스템 글자 크기 설정(접근성 폰트 확대 등)을 무시하고, 앱이 지정한 sp 값 그대로 렌더링되도록 고정
    val currentDensity = LocalDensity.current
    val fixedFontScaleDensity = remember(currentDensity) {
        Density(density = currentDensity.density, fontScale = 1f)
    }

    CompositionLocalProvider(
        LocalColorTheme provides colorScheme,
        LocalFontTheme provides fontScheme,
        LocalFigmaDimens provides figmaScale,
        LocalDensity provides fixedFontScaleDensity
    ) {
        MaterialTheme(
            typography = typography,
            content = content
        )
    }
}