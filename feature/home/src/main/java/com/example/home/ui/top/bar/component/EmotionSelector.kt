package com.example.home.ui.top.bar.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.home.R

@Composable
fun EmotionSelector(
    selectedEmotionId: Long?,
    onEmotionChange: (Long?) -> Unit
) {
    // 컬러 아이콘
    val colorIcons = listOf(
        R.drawable.ic_joy,
        R.drawable.ic_calm,
        R.drawable.ic_excite,
        R.drawable.ic_sad,
        R.drawable.ic_irritation,
        R.drawable.ic_anger
    )

    // 비활성화 아이콘
    val grayIcons = listOf(
        R.drawable.ic_joy_gray,
        R.drawable.ic_calm_gray,
        R.drawable.ic_excite_gray,
        R.drawable.ic_sad_gray,
        R.drawable.ic_irritation_gray,
        R.drawable.ic_anger_gray
    )

    val emotionIds = listOf(1L, 2L, 3L, 4L, 5L, 6L)
    val hasSelection = selectedEmotionId != null

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        colorIcons.forEachIndexed { idx, _ ->
            val id = emotionIds[idx]
            val isSelected = selectedEmotionId == id
            val isDimmed = hasSelection && !isSelected


            val iconResId = if (isDimmed) grayIcons[idx] else colorIcons[idx]

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .then(
                        if (isSelected) {
                            Modifier.background(brush = LocalColorTheme.current.backgroundmaincolor, shape = RoundedCornerShape(18.dp))
                        } else {
                            Modifier.background(color = LocalColorTheme.current.gray[100], shape = RoundedCornerShape(18.dp))
                        }
                    )
                    .then(
                        if (isSelected) Modifier.border(
                            width = 1.dp,
                            brush = Basic.maincolor,
                            shape = RoundedCornerShape(18.dp)
                        ) else Modifier
                    )
                    .padding(8.dp)
                    .clickable {
                        onEmotionChange(if (isSelected) null else id)
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
                    // 흑백 리소스를 쓰면 alpha는 취향(필요없으면 제거해도 됨)
                    // .alpha(if (isDimmed) 0.9f else 1f)
                )
            }

            Spacer(modifier = Modifier.width(7.dp))
        }
    }
}

@Preview
@Composable
fun PreviewEmotionSelector() {
    var selected by remember { mutableStateOf<Long?>(1L) }

    EmotionSelector(
        selectedEmotionId = selected,
        onEmotionChange = { selected = it }
    )
}