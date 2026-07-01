package com.linku.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.EmotionType
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.home.util.imgRes

@Composable
fun LinkDetailEmotionDropdown(
    emotions: List<EmotionType>,
    selectedEmotion: String,
    onEmotionClick: (EmotionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.linkuColors
    
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(colors.white)
            .padding(top = 14.dp, start = 16.dp, end = 56.dp, bottom = 14.dp)
            .heightIn(max = 264.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        emotions.forEach { emotion ->
            val isSelected = emotion.tagName == selectedEmotion

            Row(
                modifier = Modifier
                    .noRippleClickable {
                        onEmotionClick(emotion)
                    }
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(emotion.imgRes),
                    contentDescription = null,
                    modifier = Modifier.size(29.dp)
                )

                Text(
                    text = emotion.tagName,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) {
                        FontWeight.Medium
                    } else {
                        FontWeight.Normal
                    },
                    color = if (isSelected) {
                        colors.blue[200]
                    } else {
                        colors.gray[800]
                    }
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewLinkDetailEmotionDropdown() {
    ThemeProvider {
        LinkDetailEmotionDropdown(
            emotions = EmotionType.entries.toList(),
            selectedEmotion = "평온",
            onEmotionClick = { }
        )
    }
}