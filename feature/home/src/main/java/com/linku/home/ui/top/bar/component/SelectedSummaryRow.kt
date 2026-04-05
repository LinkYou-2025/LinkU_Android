package com.linku.home.ui.top.bar.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.BrushText
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.color.Basic
import com.linku.home.R
import com.linku.home.screen.Situation

@Composable
fun SelectedSummaryRow(
    selectedEmotionId: Long?,
    selectedTaskId: Long?,
    situations: List<Situation>,
) {
    val colorIcons = listOf(
        R.drawable.ic_joy,
        R.drawable.ic_calm,
        R.drawable.ic_excite,
        R.drawable.ic_sad,
        R.drawable.ic_irritation,
        R.drawable.ic_anger
    )
    val emotionIds = listOf(1L, 2L, 3L, 4L, 5L, 6L)

    val selectedEmotionIcon = run {
        val idx = emotionIds.indexOf(selectedEmotionId)
        require(idx >= 0) { "Invalid selectedEmotionId: $selectedEmotionId" }
        colorIcons[idx]
    }

    val selectedTaskLabel = requireNotNull(
        situations.firstOrNull { it.id == selectedTaskId }?.name
    ) { "Invalid selectedTaskId: $selectedTaskId (not found in situations)" }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))

        // 감정 아이콘
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(brush = LocalColorTheme.current.backgroundmaincolor, shape = RoundedCornerShape(18.dp))
                .border(
                    width = 1.dp,
                    brush = Basic.maincolor,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = selectedEmotionIcon),
                contentDescription = null,
                modifier = Modifier.size(22.86.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // 상황 텍스트
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(
                    brush = LocalColorTheme.current.backgroundmaincolor,
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Basic.maincolor,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 15.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BrushText(
                text = selectedTaskLabel,
                brush = Basic.maincolor,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = LocalFontTheme.current.font
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSelectedSummaryRow() {
    val sampleSituations = listOf(
        Situation(10L, "영어 공부 중")
    )

    SelectedSummaryRow(
        selectedEmotionId = 1L,
        selectedTaskId = 10L,
        situations = sampleSituations
    )
}