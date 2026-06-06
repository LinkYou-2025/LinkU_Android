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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.paging.compose.itemKey
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.LocalColorTheme
import androidx.paging.LoadState
import com.linku.home.ui.alarm.component.AlarmItem
import com.linku.home.ui.alarm.component.AlarmNothingTab
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
    val pushAlarmEnabled by viewModel.pushAlarmEnabled.collectAsStateWithLifecycle()

    //탭 선택 상태
    var selectedTab by rememberSaveable { mutableStateOf(AlarmType.ALL) }

    // 탭별 목록의 스크롤 상태를 저장하는 Map
    val listStates = remember { AlarmType.entries.associateWith { LazyListState() } }

    // 화면에 표시될 페이징될 알람들 데이터
    val alarms = viewModel.getAlarms(selectedTab)
        .collectAsLazyPagingItems()

    AlarmScreenContent(
        isAlarmAllowed = pushAlarmEnabled,
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

//        val isEmpty = isAlarmAllowed
//                && alarms.loadState.refresh is LoadState.NotLoading
//                && alarms.itemCount == 0
//        AlarmNothingTab(
//            isVisible = isEmpty,
//        )

        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 12.dp),
        ) {
            if (isAlarmAllowed) {
                items(
                    count = alarms.itemCount,
                    key = alarms.itemKey { it.id }
                ) { index ->
                    alarms[index]?.let { alarm ->
                        AlarmItem(alarm = alarm)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlarmScreenContentPreview() {
    val sampleAlarms = listOf(
        AlarmSummary(
            id = 1,
            alarmType = AlarmType.CURATION,
            whenSubmitted = "10분 전",
            message = "1월 세나님을 위한 링큐레이션이 도착했어요!",
            targetId = 1L,
            isRead = false
        ),
        AlarmSummary(
            id = 2,
            alarmType = AlarmType.NOTICE,
            whenSubmitted = "1시간 전",
            message = "새로운 서비스 공지사항입니다.",
            targetId = 2L,
            isRead = true
        )
    )
    val alarms = flowOf(PagingData.from(sampleAlarms)).collectAsLazyPagingItems()

    LinkuPreview {
        AlarmScreenContent(
            isAlarmAllowed = true,
            selectedTab = AlarmType.ALL,
            onSelectedChange = {},
            alarms = alarms,
            listState = rememberLazyListState(),
            onBack = {},
            onNavigateToMyPage = {},
            onNavigateToHome = {}
        )
    }
}


