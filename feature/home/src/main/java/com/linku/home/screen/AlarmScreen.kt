package com.linku.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.LocalColorTheme
import com.linku.home.ui.alarm.component.AlarmItem
import com.linku.home.ui.alarm.component.AlarmTopBar
import com.linku.home.ui.alarm.component.AlarmFilterTabs
import com.linku.home.ui.alarm.component.AlarmSettingTab
import com.linku.home.viewmodel.AlarmViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun AlarmScreen(
    onBack: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AlarmViewModel = hiltViewModel()
) {
    val alarmState by viewModel.alarmState.collectAsStateWithLifecycle()

    //탭 선택 상태
    var selectedTab by rememberSaveable { mutableStateOf(AlarmType.ALL) }

    // 탭별 목록의 스크롤 상태를 저장하는 Map
    val listStates = remember { AlarmType.entries.associateWith { LazyListState() } }

    // 화면에 표시될 페이징될 알람들 데이터
    val alarms = remember(selectedTab) {
        viewModel.getAlarms(selectedTab)
    }.collectAsLazyPagingItems()

    AlarmScreenContent(
        isAlarmAllowed = alarmState,
        selectedTab = selectedTab,
        onSelectedChange = { selectedTab = it },
        alarms = alarms,
        listState = listStates.getValue(selectedTab),
        onBack = onBack,
        onNavigateToMyPage = onNavigateToMyPage,
        onNavigateToHome = onNavigateToHome
    )
}

@Composable
private fun AlarmScreenContent(
    isAlarmAllowed: Boolean = true,
    selectedTab: AlarmType,
    onSelectedChange: (AlarmType) -> Unit,
    alarms: LazyPagingItems<AlarmSummary>,
    listState: LazyListState,
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

        AlarmFilterTabs(
            selected = selectedTab,
            onSelectedChange = onSelectedChange
        )

        Spacer(modifier = Modifier.height(15.dp))

        AlarmSettingTab(
            isVisible = !isAlarmAllowed,
            onClick = onNavigateToMyPage
        )

        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 12.dp),
        ) {
            if (isAlarmAllowed) {
                items(alarms.itemCount) { index ->
                    val item = alarms[index]
                    if (item != null) {
                        AlarmItem(alarm = item)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AlarmScreenPreview() {
    val fakeAlarms = listOf(
        AlarmSummary(
            id = 0,
            alarmType = AlarmType.LINK,
            whenSubmitted = "0분 전",
            message = "'요즘 대학생들이 진짜 쓰는 앱 TOP 10' 링크에 대한 AI 요약이 완료되었어요.",
            isRead = false
        ),
        AlarmSummary(
            id = 1,
            alarmType = AlarmType.CURATION,
            whenSubmitted = "1분 전",
            message = "1월 세나님을 위한 링큐레이션이 도착했어요!",
            isRead = true
        ),
        AlarmSummary(
            id = 2,
            alarmType = AlarmType.FOLDER,
            whenSubmitted = "2분 전",
            message = "더미 알림 2",
            isRead = false
        ),
        AlarmSummary(
            id = 3,
            alarmType = AlarmType.LINK,
            whenSubmitted = "3분 전",
            message = "'요즘 대학생들이 진짜 쓰는 앱 TOP 10' 링크에 대한 AI 요약이 완료되었어요.",
            isRead = true
        ),
        AlarmSummary(
            id = 4,
            alarmType = AlarmType.NOTICE,
            whenSubmitted = "4분 전",
            message = "더미 알림 4",
            isRead = false
        ),
    )

    val alarms = flowOf(PagingData.from(fakeAlarms)).collectAsLazyPagingItems()

//    LinkuPreview {
//        AlarmScreenContent(
//            selectedTab = AlarmType.ALL,
//            onSelectedChange = {},
//            alarms = alarms,
//            onBack = {},
//            onNavigateToMyPage = {},
//            onNavigateToHome = {}
//        )
//    }
}
