package com.linku.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linku.design.theme.LocalColorTheme
import com.linku.home.ui.alarm.top.bar.AlarmTopBar
import com.linku.home.ui.alarm.top.bar.component.AlarmFilterTab
import com.linku.home.ui.alarm.top.bar.component.AlarmFilterTabs
import com.linku.home.ui.alarm.top.bar.component.AlarmSettingTab

@Composable
fun AlarmScreen(
    onBack: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColorTheme.current.gray[100])
            .padding(start = 20.dp, end = 20.dp, top = 56.dp, bottom = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlarmTopBar(
            onBack = onBack,
            onHomeClick = onNavigateToHome
        )

        Spacer(modifier = Modifier.height(30.dp))

        var selectedTab by remember { mutableStateOf(AlarmFilterTab.ALL) }

        AlarmFilterTabs(
            selected = selectedTab,
            onSelectedChange = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(15.dp))

        AlarmSettingTab(
            onClick = onNavigateToMyPage
        )
    }
}

@Preview
@Composable
fun PreviewAlarmScreen() {
    AlarmScreen(
        onBack = {},
        onNavigateToMyPage = {},
        onNavigateToHome = {}
    )
}