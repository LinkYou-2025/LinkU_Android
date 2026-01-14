package com.example.home.ui.top.bar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.BrushText
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.LocalFontTheme
import com.example.design.theme.color.Basic
import com.example.home.screen.Situation

@Composable
fun TaskSelector(
    selectedTask: Long?,
    onTaskChange: (Long?) -> Unit,
    situations: List<Situation>,
) {
    val firstRow = remember(situations) { situations.take(4) }
    val secondRow = remember(situations) { situations.drop(4) }

    Column {
        // 첫 줄
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            firstRow.forEach { s ->
                TaskChip(
                    text = s.name,
                    selected = selectedTask == s.id,
                    onClick = { onTaskChange(if (selectedTask == s.id) null else s.id) }
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 둘째 줄
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            secondRow.forEach { s ->
                TaskChip(
                    text = s.name,
                    selected = selectedTask == s.id,
                    onClick = { onTaskChange(if (selectedTask == s.id) null else s.id) }
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}

@Composable
private fun TaskChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    BoxChip(
        selected = selected,
        onClick = onClick
    ) {
        if (selected) {
            BrushText(
                text = text,
                brush = Basic.maincolor,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = LocalFontTheme.current.font
                )
            )
        } else {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.gray[800],
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
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (selected) {
                    Modifier.background(brush = LocalColorTheme.current.backgroundmaincolor, shape = RoundedCornerShape(10.dp))
                } else {
                    Modifier.background(color = LocalColorTheme.current.gray[100], shape = RoundedCornerShape(10.dp))
                }
            )
            .then(
                if (selected) Modifier.border(
                    width = 1.dp,
                    brush = Basic.maincolor,
                    shape = RoundedCornerShape(10.dp)
                ) else Modifier
            )
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTaskSelector() {
    var selected by remember { mutableStateOf<Long?>(null) }

    val sample = listOf(
        Situation(9, "과제 중"),
        Situation(10, "통학 중"),
        Situation(11, "쇼핑 중"),
        Situation(12, "알바 중"),
        Situation(13, "트렌드 확인"),
        Situation(14, "데이트 중"),
        Situation(15, "휴식 중"),
        Situation(16, "자기 전"),
    )

    TaskSelector(
        selectedTask = selected,
        onTaskChange = { selected = it },
        situations = sample
    )
}
