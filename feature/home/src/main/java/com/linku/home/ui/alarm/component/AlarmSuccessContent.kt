package com.linku.home.ui.alarm.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.design.theme.LinkuPreview
import kotlinx.coroutines.flow.flowOf

/**
 * 페이징된 알람 목록을 [LazyColumn]으로 렌더링하는 컴포저블입니다.
 *
 * 리스트 하단에 [AlarmAppendStateFooter]를 붙여 추가 로딩/에러 상태를 처리합니다.
 *
 * @param alarms 페이징된 알람 데이터
 * @param listState [LazyColumn]의 스크롤 상태
 */
@Composable
fun AlarmSuccessContent(
    alarms: LazyPagingItems<AlarmSummary>,
    listState: LazyListState
) {
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp)
    ) {
        items(
            count = alarms.itemCount,
            key = alarms.itemKey { it.id }
        ) { index ->
            alarms[index]?.let { AlarmItem(alarm = it) }
        }
        item { AlarmAppendStateFooter(alarms) }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlarmSuccessContentPreview() {
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
        AlarmSuccessContent(
            alarms = alarms,
            listState = rememberLazyListState()
        )
    }
}
