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
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.ThemeProvider
import com.linku.home.R

@Composable
fun LinkDetailEmotionDropdown(
    emotions: List<EmotionType>,
    selectedEmotion: String,
    onEmotionClick: (EmotionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(LocalColorTheme.current.white)
            .padding(top = 14.dp, start = 16.dp, end = 56.dp, bottom = 14.dp)
            .heightIn(max = 264.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        emotions.forEach { emotion ->
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
                    painter = painterResource(emotion.iconRes()),
                    contentDescription = null,
                    modifier = Modifier.size(29.dp)
                )

                Text(
                    text = emotion.tagName,
                    fontSize = 15.sp,
                    fontWeight = if (emotion.tagName == selectedEmotion) {
                        FontWeight.Medium
                    } else {
                        FontWeight.Normal
                    },
                    color = if (emotion.tagName == selectedEmotion) {
                        LocalColorTheme.current.blue[200]
                    } else {
                        LocalColorTheme.current.gray[800]
                    }
                )
            }
        }
    }
}

private fun EmotionType.iconRes(): Int {
    return when (this) {
        EmotionType.JOY -> R.drawable.ic_joy
        EmotionType.CALM -> R.drawable.ic_calm
        EmotionType.EXCITE -> R.drawable.ic_excite
        EmotionType.SAD -> R.drawable.ic_sad
        EmotionType.IRRITATION -> R.drawable.ic_irritation
        EmotionType.ANGER -> R.drawable.ic_anger
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