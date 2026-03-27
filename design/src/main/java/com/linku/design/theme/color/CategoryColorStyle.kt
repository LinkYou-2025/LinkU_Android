package com.linku.design.theme.color

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.linku.design.theme.LocalColorTheme

data class CategoryColorStyle(
    val color1: Color,
    val color2: Color,
    val color3: Color,
    val color4: Color,
) {

    // verticalGradient: color1 ~ color2로 만드는 함수
    fun verticalGradient(): Brush = Brush.verticalGradient(
        colorStops = arrayOf(
            0.2f to color1.copy(alpha = 0.8f),
            1.0f to color2.copy(alpha = 0.7f)
        )
    )

    // horizontalGradient: color1 ~ color2로 만드는 함수
    fun horizontalGradient(): Brush = Brush.horizontalGradient(
        colorStops = arrayOf(
            0.2f to color1.copy(alpha = 0.8f),
            1.0f to color2.copy(alpha = 0.7f)
        )
    )

    companion object {
        val DEFAULT: CategoryColorStyle
        @Composable
        get() = CategoryColorStyle(
            color1 = LocalColorTheme.current.white,
            color2 = LocalColorTheme.current.gray[200],
            color3 = LocalColorTheme.current.gray[300],
            color4 = LocalColorTheme.current.gray[500]
        )

        val categoryStyleList: List<CategoryColorStyle> = listOf(
            CategoryColorStyle(
                color1 = Color(0xFFFFEEEE),
                color2 = Color(0xFFFFC2C2),
                color3 = Color(0xFFFFA0A0),
                color4 = Color(0xFFFF5353),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFFFEBE1),
                color2 = Color(0xFFFFD3C1),
                color3 = Color(0xFFFFA783),
                color4 = Color(0xFFFF6A2B),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFFFEFDD),
                color2 = Color(0xFFFFD4A3),
                color3 = Color(0xFFFFB867),
                color4 = Color(0xFFFF9C2B),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFFFFAEB),
                color2 = Color(0xFFFFEEBE),
                color3 = Color(0xFFFFE291),
                color4 = Color(0xFFFFCE45),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFEEFFE0),
                color2 = Color(0xFFDAFDBD),
                color3 = Color(0xFFB8F785),
                color4 = Color(0xFF77E61D),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFDAF7EB),
                color2 = Color(0xFFB8F0D9),
                color3 = Color(0xFF6BDFAE),
                color4 = Color(0xFF00C774),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFC7E8CD),
                color2 = Color(0xFF9ECCA6),
                color3 = Color(0xFF70AA79),
                color4 = Color(0xFF4B9857),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFDDFFFB),
                color2 = Color(0xFFB9F7F0),
                color3 = Color(0xFF98EDE2),
                color4 = Color(0xFF36D1BE),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFECF8FF),
                color2 = Color(0xFFD0EEFF),
                color3 = Color(0xFF97D8FF),
                color4 = Color(0xFF34BBFF),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFE6ECFF),
                color2 = Color(0xFFC2D2FF),
                color3 = Color(0xFF82A3FF),
                color4 = Color(0xFF4C7AF8),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFEDE4FF),
                color2 = Color(0xFFC5A6FF),
                color3 = Color(0xFFA778FF),
                color4 = Color(0xFF813CFF),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFF4E6FF),
                color2 = Color(0xFFE7C6FF),
                color3 = Color(0xFFD8A3FF),
                color4 = Color(0xFFBA5AFF),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFFFEBFB),
                color2 = Color(0xFFFFC7F5),
                color3 = Color(0xFFFF9FED),
                color4 = Color(0xFFFF52DF),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFFFE7F2),
                color2 = Color(0xFFFFC5E0),
                color3 = Color(0xFFFFA2CC),
                color4 = Color(0xFFFF459C),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFF4E6DB),
                color2 = Color(0xFFE0B795),
                color3 = Color(0xFFBE9A7B),
                color4 = Color(0xFF906744),
            ),
            CategoryColorStyle(
                color1 = Color(0xFFD4D4D4),
                color2 = Color(0xFFAFAFAF),
                color3 = Color(0xFF747474),
                color4 = Color(0xFF000000),
            )
        )

//        val categoryStyleMap: Map<String, CategoryColorStyle> = mapOf(
//            "#FF5353" to CategoryColorStyle(
//                color1 = Color(0xFFFFEEEE),
//                color2 = Color(0xFFFFC2C2),
//                color3 = Color(0xFFFFA0A0),
//                color4 = Color(0xFFFF5353),
//            ),
//            "#FF6A2B" to CategoryColorStyle(
//                color1 = Color(0xFFFFEBE1),
//                color2 = Color(0xFFFFD3C1),
//                color3 = Color(0xFFFFA783),
//                color4 = Color(0xFFFF6A2B),
//            ),
//            "#FF9C2B" to CategoryColorStyle(
//                color1 = Color(0xFFFFEFDD),
//                color2 = Color(0xFFFFD4A3),
//                color3 = Color(0xFFFFB867),
//                color4 = Color(0xFFFF9C2B),
//            ),
//            "#FFCE45" to CategoryColorStyle(
//                color1 = Color(0xFFFFFAEB),
//                color2 = Color(0xFFFFEEBE),
//                color3 = Color(0xFFFFE291),
//                color4 = Color(0xFFFFCE45),
//            ),
//            "#77E61D" to CategoryColorStyle(
//                color1 = Color(0xFFEEFFE0),
//                color2 = Color(0xFFDAFDBD),
//                color3 = Color(0xFFB8F785),
//                color4 = Color(0xFF77E61D),
//            ),
//            "#00C774" to CategoryColorStyle(
//                color1 = Color(0xFFDAF7EB),
//                color2 = Color(0xFFB8F0D9),
//                color3 = Color(0xFF6BDFAE),
//                color4 = Color(0xFF00C774),
//            ),
//            "#4B9857" to CategoryColorStyle(
//                color1 = Color(0xFFC7E8CD),
//                color2 = Color(0xFF9ECCA6),
//                color3 = Color(0xFF70AA79),
//                color4 = Color(0xFF4B9857),
//            ),
//            "#36D1BE" to CategoryColorStyle(
//                color1 = Color(0xFFDDFFFB),
//                color2 = Color(0xFFB9F7F0),
//                color3 = Color(0xFF98EDE2),
//                color4 = Color(0xFF36D1BE),
//            ),
//            "#34BBFF" to CategoryColorStyle(
//                color1 = Color(0xFFECF8FF),
//                color2 = Color(0xFFD0EEFF),
//                color3 = Color(0xFF97D8FF),
//                color4 = Color(0xFF34BBFF),
//            ),
//            "#4C7AF8" to CategoryColorStyle(
//                color1 = Color(0xFFE6ECFF),
//                color2 = Color(0xFFC2D2FF),
//                color3 = Color(0xFF82A3FF),
//                color4 = Color(0xFF4C7AF8),
//            ),
//            "#813CFF" to CategoryColorStyle(
//                color1 = Color(0xFFEDE4FF),
//                color2 = Color(0xFFC5A6FF),
//                color3 = Color(0xFFA778FF),
//                color4 = Color(0xFF813CFF),
//            ),
//            "#BA5AFF" to CategoryColorStyle(
//                color1 = Color(0xFFF4E6FF),
//                color2 = Color(0xFFE7C6FF),
//                color3 = Color(0xFFD8A3FF),
//                color4 = Color(0xFFBA5AFF),
//            ),
//            "#FF52DF" to CategoryColorStyle(
//                color1 = Color(0xFFFFEBFB),
//                color2 = Color(0xFFFFC7F5),
//                color3 = Color(0xFFFF9FED),
//                color4 = Color(0xFFFF52DF),
//            ),
//            "#FF459C" to CategoryColorStyle(
//                color1 = Color(0xFFFFE7F2),
//                color2 = Color(0xFFFFC5E0),
//                color3 = Color(0xFFFFA2CC),
//                color4 = Color(0xFFFF459C),
//            ),
//            "#906744" to CategoryColorStyle(
//                color1 = Color(0xFFF4E6DB),
//                color2 = Color(0xFFE0B795),
//                color3 = Color(0xFFBE9A7B),
//                color4 = Color(0xFF906744),
//            ),
//            "#000000" to CategoryColorStyle(
//                color1 = Color(0xFFD4D4D4),
//                color2 = Color(0xFFAFAFAF),
//                color3 = Color(0xFF747474),
//                color4 = Color(0xFF000000),
//            )
//        )

    }
}