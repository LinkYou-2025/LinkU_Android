package com.linku.link.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.JobType
import com.linku.core.model.Situation
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SituationSelect(
    jobType: JobType,
    selectedSituationId: Long?,
    onSituationClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 13.dp, start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        jobType.situations.forEach { situation ->
            val situationId = situation.id.value
            val selected = selectedSituationId == situationId

            SituationChip(
                situation = situation,
                selected = selected,
                onClick = {
                    onSituationClick(situationId)
                }
            )
        }
    }
}

@Composable
private fun SituationChip(
    situation: Situation,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.linkuColors

    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    brush = if (selected) {
                        colors.maincolor
                    } else {
                        SolidColor(colors.black)
                    },
                )
            ) {
                append(situation.tagName)
            }
        },
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        modifier = Modifier
            .background(
                brush = if (selected) {
                    colors.selectedChipGradient
                } else {
                    SolidColor(colors.white)
                },
                shape = RoundedCornerShape(20.dp)
            )
            .then(
                if (selected) {
                    Modifier.border(
                        width = 1.dp,
                        brush = colors.maincolor,
                        shape = RoundedCornerShape(20.dp)
                    )
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = colors.gray[200],
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            )
            .noRippleClickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}

@Preview(showBackground = false)
@Composable
fun PreviewSituationSelect() {
    ThemeProvider {
        SituationSelect(
            jobType = JobType.OFFICE_WORKER,
            selectedSituationId = 18L,
            onSituationClick = { }
        )
    }
}
