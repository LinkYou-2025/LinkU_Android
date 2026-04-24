package com.linku.login.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.font.Paperlogy
import com.linku.design.theme.linkuColors
import com.linku.design.util.rememberFigmaDimens
import com.linku.design.util.scaler


//젠더, 직업 선택 버튼
@Composable
internal fun OptionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 54.dp
) {

    val colorTheme = MaterialTheme.linkuColors
    val shape = RoundedCornerShape(18.dp)

    // 배경용 연한 그라데이션
    val selectedBackground = listOf(
        colorTheme.blue[200]!!.copy(alpha = 0.18f),
        colorTheme.purple[200]!!.copy(alpha = 0.16f)
    )

    Box(
        modifier = modifier
            .then(
                if (selected) {
                    Modifier
                        .fillMaxWidth()
                        .height((54.scaler))
                } else {
                    Modifier
                        .width((372.scaler))
                        .height((54.scaler))
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
                // ⭐ 여기서 메인 컬러를 바로 주입!
                brush = if (selected) colorTheme.maincolor
                else SolidColor(colorTheme.gray[400]!!),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = (22.scaler)),
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
                fontWeight = FontWeight.Normal,
                color = if (selected) colorTheme.black else colorTheme.gray[500]!!
            )

            // 선택된 경우만 체크 표시
            if (selected) {
                CheckIndicator(
                    checked = selected,
                    modifier = Modifier.size((20.scaler))
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
    LinkuPreview {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.linkuColors.gray[100]!!)
                .padding((16.scaler))
        ) {
            OptionButton(
                text = "남성",
                selected = false,
                onClick = {}
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "OptionButton - 활성"
)
@Composable
private fun OptionButtonPreview_Selected() {

    LinkuPreview {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.linkuColors.gray[100]!!)
                .padding((16.scaler))
        ) {
            OptionButton(
                text = "여성",
                selected = true,
                onClick = {}
            )
        }
    }
}

