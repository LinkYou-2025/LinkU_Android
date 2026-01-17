package com.example.login.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.font.Paperlogy
import com.example.design.util.rememberFigmaDimens


//젠더, 직업 선택 버튼
@Composable
fun OptionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 54.dp
) {

    val colorTheme = LocalColorTheme.current
    val (w, h) = rememberFigmaDimens() // 반응형 유틸 가져오기
    val paperlogyFamily = Paperlogy.font
    val shape = RoundedCornerShape(18.dp)

    val activeBorderGradient = listOf(
        Color(0xFF2C6FFF),
        Color(0xFFC800FF)
    )

    val selectedBackground = listOf(
        Color(0xFF2C6FFF).copy(alpha = 0.18f),
        Color(0xFFC800FF).copy(alpha = 0.16f)
    )

    Box(
        modifier = modifier
            .then(
                if (selected) {
                    Modifier
                        .fillMaxWidth()
                        .height(h(54f))
                } else {
                    Modifier
                        .width(w(372f))
                        .height(h(54f))
                }
            )
            .clip(shape)
            .then(
                if (selected) {
                    Modifier.background(
                        brush = Brush.horizontalGradient(selectedBackground),
                        shape = shape
                    )
                } else {
                    Modifier.background(
                        color = colorTheme.white,
                        shape = shape
                    )
                }
            )
            .border(
                width = 1.dp,
                brush = if (selected)
                    Brush.horizontalGradient(activeBorderGradient)
                else
                    Brush.linearGradient(
                        listOf(colorTheme.gray[400]!!, colorTheme.gray[400]!!)
                    ),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = w(22f)),
        contentAlignment = Alignment.CenterStart
    ){
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                fontSize = 15.sp,
                lineHeight = 22.sp, // 요구사항 반영
                fontFamily = paperlogyFamily,
                fontWeight = FontWeight.Normal,
                color = if (selected) colorTheme.black else colorTheme.gray[500]!!
            )

            // 선택된 경우만 체크 표시
            if (selected) {
                CheckIndicator(
                    checked = selected,
                    modifier = Modifier.size(w(20f))
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    name = "OptionButton - 비활성"
)
@Composable
private fun OptionButtonPreview_Unselected() {
    val colorTheme = LocalColorTheme.current
    val (w, h) = rememberFigmaDimens()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorTheme.gray[100]!!)
            .padding(w(16f)) // 프리뷰 패딩 반응형 적용
    ) {
        OptionButton(
            text = "남성",
            selected = false,
            onClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    name = "OptionButton - 활성"
)
@Composable
private fun OptionButtonPreview_Selected() {
    val colorTheme = LocalColorTheme.current
    val (w, h) = rememberFigmaDimens()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorTheme.gray[100]!!)
            .padding(w(16f)) // 프리뷰 패딩 반응형 적용
    ) {
        OptionButton(
            text = "여성",
            selected = true,
            onClick = {}
        )
    }
}

