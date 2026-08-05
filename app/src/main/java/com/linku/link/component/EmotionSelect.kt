package com.linku.link.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.EmotionType
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.Basic
import com.linku.design.theme.linkuColors
import com.linku.link.util.imgRes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmotionSelect(
    selectedEmotionId: Long?,
    onEmotionSelect: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val emotions = EmotionType.entries

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 13.dp, start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        emotions.forEach { emotion ->
            val emotionId = emotion.value
            val selected = selectedEmotionId == emotionId

            EmotionBadgeImage(
                emotion = emotion,
                selected = selectedEmotionId == emotionId,
                onClick = {
                    onEmotionSelect(
                        if (selected) null else emotionId
                    )
                }
            )
        }
    }
}

@Composable
private fun EmotionBadgeImage(
    emotion: EmotionType,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.linkuColors

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = if (selected) {
                    colors.selectedChipGradient
                } else {
                    SolidColor(colors.white)
                },
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                brush = if (selected) Basic.maincolor else SolidColor(colors.gray[200]),
                shape = RoundedCornerShape(20.dp)
            )

            .noRippleClickable { onClick() }
            .padding(horizontal = 15.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = emotion.imgRes,
            contentDescription = emotion.tagName,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = emotion.tagName,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = colors.black,
                fontFamily = LocalFontTheme.current.font
            )
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewEmotionSelect() {
    ThemeProvider {
        EmotionSelect(
            selectedEmotionId = 1L,
            onEmotionSelect = { }
        )
    }
}