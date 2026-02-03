package com.example.home.ui.alarm.top.bar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.home.ui.alarm.top.bar.component.AlarmTopHeader

@Composable
fun AlarmTopBar() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlarmTopHeader()
    }
}

@Preview
@Composable
fun PreviewAlarmTopBar() {
    AlarmTopBar()
}