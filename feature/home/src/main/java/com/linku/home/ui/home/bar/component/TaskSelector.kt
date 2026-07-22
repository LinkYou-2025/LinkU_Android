package com.linku.home.ui.home.bar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.Situation
import com.linku.core.model.SituationOptions
import com.linku.design.BrushText
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.color.Basic
import com.linku.design.theme.linkuColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskSelector(
    selectedTask: Long?,
    onTaskChange: (Long?) -> Unit,
    situations: List<Situation>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = 10.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 4
    ) {
        situations.forEach { situation ->
            val situationId = situation.id.value
            val isSelected = selectedTask == situationId

            TaskChip(
                text = situation.tagName,
                selected = isSelected,
                onClick = {
                    onTaskChange(
                        if (isSelected) null else situationId
                    )
                }
            )
        }
    }
}

@Composable
private fun TaskChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors

    BoxChip(
        selected = selected,
        onClick = onClick
    ) {
        if (selected) {
            BrushText(
                text = text,
                brush = colors.maincolor,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = LocalFontTheme.current.font
                ),
            )
        } else {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.gray[800],
                    fontFamily = LocalFontTheme.current.font
                )
            )
        }
    }
}

@Composable
private fun BoxChip(
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.linkuColors

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (selected) {
                    Modifier.background(brush = colors.backgroundmaincolor, shape = RoundedCornerShape(10.dp))
                } else {
                    Modifier.background(color = colors.gray[100], shape = RoundedCornerShape(10.dp))
                }
            )
            .then(
                if (selected) Modifier.border(
                    width = 1.dp,
                    brush = Basic.maincolor,
                    shape = RoundedCornerShape(10.dp)
                ) else Modifier
            )
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 8.dp)
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTaskSelector() {
    var selected by remember { mutableStateOf<Long?>(null) }

    TaskSelector(
        selectedTask = selected,
        onTaskChange = { selected = it },
        situations = SituationOptions.situationsFor(jobId = 2L)
    )
}
