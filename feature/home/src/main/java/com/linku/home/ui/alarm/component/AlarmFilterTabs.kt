package com.linku.home.ui.alarm.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.alarm.AlarmType
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme

//enum class AlarmFilterTab(val label: String) {
//    ALL("전체"),
//    LINK("링크"),
//    FOLDER("폴더"),
//    CURATION("큐레이션"),
//    NOTICE("공지")
//}

@Composable
fun AlarmFilterTabs(
    selected: AlarmType,
    onSelectedChange: (AlarmType) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = AlarmType.entries

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(tabs) { tab ->
            AlarmFilterChip(
                text = tab.displayName,
                selected = (tab == selected),
                onClick = { onSelectedChange(tab) }
            )
        }
    }
}

@Composable
private fun AlarmFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) LocalColorTheme.current.gray[800] else LocalColorTheme.current.white)
            .then(if (!selected) Modifier.border(1.dp, LocalColorTheme.current.gray[200], RoundedCornerShape(10.dp)) else Modifier)
            .clickable { onClick() }
            .padding(horizontal = 15.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) LocalColorTheme.current.white else LocalColorTheme.current.gray[800],
            fontSize = 14.sp,
            fontFamily = LocalFontTheme.current.font,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Preview
@Composable
fun PreviewAlarmFilterTabs() {
    var selected by remember { mutableStateOf(AlarmType.ALL) }

    AlarmFilterTabs(
        selected = selected,
        onSelectedChange = { selected = it }
    )
}