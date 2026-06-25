package com.linku.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.Situation
import com.linku.core.model.SituationOptions
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.Basic

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SituationSelect(
    jobId: Long,
    selectedSituationId: Long?,
    onSituationSelect: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val situations = SituationOptions.situationsFor(jobId)

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 13.dp, start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        situations.forEach { situation ->
            SituationChip(
                situation = situation,
                selected = selectedSituationId == situation.id,
                onClick = {
                    onSituationSelect(
                        if (selectedSituationId == situation.id) null else situation.id
                    )
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
    Text(
        text = situation.tagName,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = LocalColorTheme.current.black,
        modifier = Modifier
            .background(
                brush = if (selected) {
                    LocalColorTheme.current.inactiveColor
                } else {
                    SolidColor(LocalColorTheme.current.white)
                },
                shape = RoundedCornerShape(20.dp)
            )
            .then(
                if (selected) {
                    Modifier.border(
                        width = 1.dp,
                        brush = Basic.maincolor,
                        shape = RoundedCornerShape(20.dp)
                    )
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = LocalColorTheme.current.gray[200],
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            )
            .noRippleClickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 9.dp)
    )
}

@Preview(showBackground = false)
@Composable
fun PreviewSituationSelect() {
    ThemeProvider {
        SituationSelect(
            jobId = 3L,
            selectedSituationId = 18L,
            onSituationSelect = { }
        )
    }
}