package com.linku.home.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.Basic
import com.linku.home.R

private data class EmotionUi(
    val id: Long,
    val label: String,
    @param:DrawableRes val iconRes: Int
)

private val EMOTIONS = listOf(
    EmotionUi(1L, "즐거움", R.drawable.ic_joy),
    EmotionUi(2L, "평온",   R.drawable.ic_calm),
    EmotionUi(3L, "설렘",   R.drawable.ic_excite),
    EmotionUi(4L, "우울",   R.drawable.ic_sad),
    EmotionUi(5L, "짜증",   R.drawable.ic_irritation),
    EmotionUi(6L, "분노",   R.drawable.ic_anger),
)

@Composable
fun EmotionSelect(
    selectedEmotionId: Long?,
    onEmotionSelect: (Long?) -> Unit
) {
    val firstRow = EMOTIONS.take(3)
    val secondRow = EMOTIONS.drop(3)

    Column(
        modifier = Modifier.padding(top = 13.dp, start = 20.dp)
    ) {
        Row {
            firstRow.forEach { e ->
                EmotionBadgeImage(
                    iconRes = e.iconRes,
                    label = e.label,
                    selected = selectedEmotionId == e.id,
                    onToggle = { onEmotionSelect(if (selectedEmotionId == e.id) null else e.id) }
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row {
            secondRow.forEach { e ->
                EmotionBadgeImage(
                    iconRes = e.iconRes,
                    label = e.label,
                    selected = selectedEmotionId == e.id,
                    onToggle = { onEmotionSelect(if (selectedEmotionId == e.id) null else e.id) }
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}

@Composable
private fun EmotionBadgeImage(
    @DrawableRes iconRes: Int,
    label: String,
    selected: Boolean,
    onToggle: () -> Unit
) {
    val boxBackground = Brush.horizontalGradient(
        listOf(Color(0x1A2C6FFF), Color(0x1AC800FF))
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = if (selected) boxBackground else SolidColor(LocalColorTheme.current.white)
            )
            .then(
                if (selected) {
                    Modifier.border(1.dp, brush = Basic.maincolor, shape = RoundedCornerShape(20.dp))
                } else {
                    Modifier.border(1.dp, color = LocalColorTheme.current.gray[200], shape = RoundedCornerShape(20.dp))
                }
            )
            .noRippleClickable { onToggle() }
            .padding(horizontal = 15.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(5.dp))

        // 라벨
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = if (selected) LocalColorTheme.current.black else LocalColorTheme.current.gray[800],
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
            selectedEmotionId = 1,
            onEmotionSelect = { }
        )
    }
}