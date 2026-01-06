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
import com.example.design.R
import com.example.login.Paperlogy


//젠더, 직업 선택 버튼
@Composable
fun OptionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 56.dp
) {
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
                        .height(54.dp)
                } else {
                    Modifier
                        .width(372.dp)
                        .height(54.dp)
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
                        color = Color.White,
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
                        listOf(Color(0xFFB7B9BF), Color(0xFFB7B9BF))
                    ),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp), // ⭐ 핵심
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
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Normal,
                color = if (selected) {
                    Color.Black
                } else {
                    Color(0xFFA1A3A9)
                }
            )

            // 선택된 경우만 체크 표시
            if (selected) {
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp)
                        .background(
                            color = Color(0xFFCB59EB),
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_checkbox_checked),
                        contentDescription = "선택됨",
                        modifier = Modifier
                            .padding(1.5.dp)
                            .width(9.54546.dp)
                            .height(7.27273.dp)
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFF5F6F9,
    name = "OptionButton - 비활성"
)
@Composable
private fun OptionButtonPreview_Unselected() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
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
    backgroundColor = 0xFFF5F6F9,
    name = "OptionButton - 활성"
)
@Composable
private fun OptionButtonPreview_Selected() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OptionButton(
            text = "여성",
            selected = true,
            onClick = {}
        )
    }
}

