package com.linku.home.ui.home.bar.component

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.EmotionType
import com.linku.core.model.Situation
import com.linku.core.model.SituationOptions
import com.linku.design.BrushText
import com.linku.design.theme.color.Basic
import com.linku.design.theme.linkuColors
import com.linku.design.theme.linkuFont
import com.linku.home.util.imgRes

@Composable
fun SelectedSummaryRow(
    selectedEmotionId: Long?,
    selectedTaskId: Long?,
    situations: List<Situation>,
) {
    val colors = MaterialTheme.linkuColors
    val fonts = MaterialTheme.linkuFont

    val selectedEmotion = requireNotNull(
        EmotionType.fromValue(selectedEmotionId)
    ) {
        "Invalid selectedEmotionId: $selectedEmotionId"
    }

    val selectedSituation = requireNotNull(
        situations.firstOrNull { it.id.value == selectedTaskId }
    ) {
        "Invalid selectedTaskId: $selectedTaskId (not found in situations)"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    brush = colors.backgroundmaincolor,
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 1.dp,
                    brush = colors.maincolor,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = selectedEmotion.imgRes,
                contentDescription = null,
                modifier = Modifier.size(22.86.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(
                    brush = colors.backgroundmaincolor,
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 1.dp,
                    brush = colors.maincolor,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 15.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BrushText(
                text = selectedSituation.tagName,
                brush = Basic.maincolor,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = fonts.font
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSelectedSummaryRow() {
    val sampleSituations = SituationOptions.situationsFor(jobId = 2L)
    val selectedSituationId = sampleSituations.first().id.value

    SelectedSummaryRow(
        selectedEmotionId = EmotionType.JOY.id.value,
        selectedTaskId = selectedSituationId,
        situations = sampleSituations
    )
}