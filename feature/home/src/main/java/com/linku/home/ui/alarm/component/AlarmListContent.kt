package com.linku.home.ui.alarm.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.design.theme.LinkuPreview
import kotlinx.coroutines.flow.flowOf

/**
 * 알람 페이징 상태에 따라 적절한 콘텐츠 컴포저블로 분기합니다.
 *
 * - [LoadState.Loading]: [AlarmLoadingContent] 표시
 * - [LoadState.Error]: [AlarmErrorContent] 표시
 * - 데이터 없음: [AlarmNothingTab] 표시
 * - 데이터 있음: [AlarmSuccessContent] 표시
 *
 * @param alarms 페이징된 알람 데이터
 * @param listState [LazyColumn]의 스크롤 상태
 */
@Composable
fun AlarmListContent(
    alarms: LazyPagingItems<AlarmSummary>,
    listState: LazyListState
) {
    when {
        // 로딩 처리
        alarms.loadState.refresh is LoadState.Loading -> AlarmLoadingContent()

        // 에러 처리
        alarms.loadState.refresh is LoadState.Error -> AlarmErrorContent(
            alarms,
            alarms.loadState.refresh as LoadState.Error
        )

        // 데이터가 없을 때
        alarms.itemCount == 0 -> {
            Spacer(modifier = Modifier.height(12.dp))
            AlarmNothingTab(isVisible = true)
        }

        //데이터가 있을 때
        else -> AlarmSuccessContent(alarms, listState)
    }
}