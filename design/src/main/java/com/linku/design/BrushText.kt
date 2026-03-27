package com.linku.design

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import com.linku.design.theme.LocalFontTheme

@Composable
fun BrushText(
    text: String,
    brush: Brush? = null,
    color: Color = Color.Companion.Unspecified,
    style: TextStyle = TextStyle.Companion.Default,
    modifier: Modifier = Modifier.Companion,
    fontFamily: FontFamily? = null,
) {
    // 기본값: 테마의 폰트 사용 (LocalFontTheme는 당신이 만든 compositionLocal)
    val family = fontFamily ?: LocalFontTheme.current.font

    // 전체 텍스트에 적용될 스타일에 fontFamily 병합
    val styleWithFamily = style.merge(TextStyle(fontFamily = family))

    if (brush != null) {
        BasicText(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        brush = brush,
                        fontSize = style.fontSize,
                        fontWeight = style.fontWeight,
                        fontFamily = family
                    )
                ) {
                    append(text)
                }
            },
            style = styleWithFamily,
            modifier = modifier,
        )
    } else {
        BasicText(
            text = text,
            style = styleWithFamily.copy(color = color),
            modifier = modifier,
        )
    }
}