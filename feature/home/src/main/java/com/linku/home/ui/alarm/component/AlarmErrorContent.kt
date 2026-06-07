package com.linku.home.ui.alarm.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.linku.core.error.AppError
import com.linku.core.error.NetworkError
import com.linku.core.model.alarm.AlarmSummary
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.LocalColorTheme

/**
 * 알람 페이징 새로고침 에러를 처리하는 컴포저블입니다.
 *
 * [alarmPagingItems]의 refresh 상태에서 [AppError]를 추출하여
 * 에러 메시지와 재시도 버튼을 표시합니다.
 *
 * @param alarmPagingItems 에러가 발생한 페이징 데이터
 *
 * 일단 임시구현해두었습니다 ~~
 */
@Composable
fun AlarmErrorContent(
    alarmPagingItems: LazyPagingItems<AlarmSummary>,
    errorState: LoadState.Error
) {
    val message = (errorState.error as AppError).displayMessage

    AlarmErrorContent(
        message = message,
        onRetry = { alarmPagingItems.retry() }
    )
}

@Composable
private fun AlarmErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                color = LocalColorTheme.current.gray[600]
            )
            TextButton(onClick = onRetry) { Text("다시 시도") }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun AlarmErrorContentPreview() {
    LinkuPreview {
        AlarmErrorContent(
            message = NetworkError.NoConnection().displayMessage,
            onRetry = {}
        )
    }
}
