package com.linku.home.ui.alarm.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.linku.core.model.alarm.AlarmSummary
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.linkuColors

/**
 * 알람 목록 하단에 추가 로딩 상태를 표시하는 푸터 컴포저블입니다.
 *
 * [alarmPagingItems]의 append [LoadState]를 감지하여 로딩 중이면 [CircularProgressIndicator],
 * 에러 시 재시도 버튼을 표시합니다.
 *
 * @param alarmPagingItems append 상태를 제공하는 페이징 데이터
 *
 */
@Composable
fun AlarmAppendStateFooter(
    alarmPagingItems: LazyPagingItems<AlarmSummary>
) {
    val append = alarmPagingItems.loadState.append
    val endReached = append.endOfPaginationReached

    if (endReached) return  // 끝까지 도달했으면 푸터 출력 X

    AlarmAppendStateFooterContent(
        appendState = append,
        onRetry = { alarmPagingItems.retry() }
    )
}

/**
 * append [LoadState]에 따라 로딩 인디케이터 또는 재시도 버튼을 렌더링합니다.
 *
 * @param appendState 현재 append 로드 상태
 * @param onRetry 재시도 버튼 클릭 콜백
 */
@Composable
private fun AlarmAppendStateFooterContent(
    appendState: LoadState,
    onRetry: () -> Unit
) {
    val colorTheme = MaterialTheme.linkuColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(235.dp)
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        when (appendState) {
            is LoadState.Loading -> {
                CircularProgressIndicator(
                    color = colorTheme.black
                )
            }

            is LoadState.Error -> {
                AlarmErrorFooter(
                    onClick = onRetry
                )
            }

            else -> Unit
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun AlarmAppendStateFooterLoadingPreview() {
    LinkuPreview {
        AlarmAppendStateFooterContent(
            appendState = LoadState.Loading,
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AlarmAppendStateFooterErrorPreview() {
    LinkuPreview {
        AlarmAppendStateFooterContent(
            appendState = LoadState.Error(Exception("preview error")),
            onRetry = {}
        )
    }
}
