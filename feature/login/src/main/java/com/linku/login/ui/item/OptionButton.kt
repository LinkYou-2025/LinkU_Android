package com.linku.login.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler


//젠더, 직업 선택 버튼
@Composable
internal fun OptionButton(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {

    val colorTheme = MaterialTheme.linkuColors
    val shape = RoundedCornerShape(18.dp)

    // 배경용 연한 그라데이션
    val selectedBackground = listOf(
        colorTheme.blue[200].copy(alpha = 0.18f),
        colorTheme.purple[200].copy(alpha = 0.16f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
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
                else SolidColor(colorTheme.gray[400]),
                shape = shape
            )
            .clickable(onClick = onClick)
            // 높이를 고정하지 않고 텍스트 위아래 패딩으로 보장 (글씨 크기 반응형, LoginTextField와 동일 스펙)
            .padding(
                start = (22.scaler),
                end = (22.scaler),
                top = (21.scaler),
                bottom = (21.scaler)
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                lineHeight = 20.sp, // 요구사항 반영
                fontWeight = FontWeight.Normal,
                color = if (selected) colorTheme.black else colorTheme.gray[500]
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
                .background(MaterialTheme.linkuColors.gray[100])
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
                .background(MaterialTheme.linkuColors.gray[100])
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

