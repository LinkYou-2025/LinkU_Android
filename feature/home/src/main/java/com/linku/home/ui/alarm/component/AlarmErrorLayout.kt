package com.linku.home.ui.alarm.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.linku.core.error.AppError
import com.linku.core.error.NetworkError
import com.linku.core.model.alarm.AlarmSummary
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.home.R
import kotlinx.coroutines.flow.flowOf

/**
 * 알람 페이징 새로고침 에러를 처리하는 컴포저블입니다.
 *
 * [alarmPagingItems]의 refresh 상태에서 [AppError]를 추출하여
 * 에러 메시지와 재시도 버튼을 표시합니다.
 *
 * @param alarmPagingItems 에러가 발생한 페이징 데이터
 * @param errorState 알람 목록을 새로고침하는 과정에서 발생한 에러 상태
 *
 */
@Composable
fun AlarmErrorLayout(
    alarmPagingItems: LazyPagingItems<AlarmSummary>,
    errorState: LoadState.Error,
    modifier: Modifier = Modifier
) {
    // 에러 유형에 따라 출력 메세지 분기처리.
    val (title, subtitle) = when (val error = errorState.error as AppError) {
        is NetworkError -> "네트워크 연결이 불안정해요." to "인터넷 연결 상태를 확인한 후 다시 시도해주세요."
        else -> error.displayMessage to null
    }

    AlarmErrorLayoutContent(
        title = title,
        subtitle = subtitle,
        onRetry = { alarmPagingItems.retry() },
        modifier = modifier
    )
}

@Composable
private fun AlarmErrorLayoutContent(
    title: String,
    subtitle: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorTheme = MaterialTheme.linkuColors

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // 와이파이 끊김 아이콘
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = colorTheme.backgroundGradient6,
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_not_wifi),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }

        Spacer(Modifier.size(14.dp))

        // 출력 메세지
        Text(
            text = title,
            style = LocalTextStyle.current.copy(
                fontWeight = FontWeight(600),
                fontSize = 18.sp,
                lineHeight = 27.sp,
                letterSpacing = -(0.839).sp,
                color = colorTheme.black,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(4.5.dp))

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = LocalTextStyle.current.copy(
                    fontWeight = FontWeight(500),
                    fontSize = 14.sp,
                    color = colorTheme.gray[500],
                    textAlign = TextAlign.Center,
                    lineHeight = 22.4.sp,
                    letterSpacing = -(0.35).sp
                )
            )
        }

        Spacer(Modifier.size(26.5.dp))

        // 다시 시도 버튼
        TextButton(
            onClick = onRetry,
            modifier = Modifier
                .size(112.dp, 44.dp)
                .background(
                    brush = colorTheme.maincolor,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(
                text = "다시 시도",
                style = LocalTextStyle.current.copy(
                    color = colorTheme.white,
                    fontWeight = FontWeight(500),
                    fontSize = 16.sp,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlarmErrorLayoutPreview() {
    val alarmPagingItems = flowOf(PagingData.from(emptyList<AlarmSummary>())).collectAsLazyPagingItems()
    LinkuPreview {
        AlarmErrorLayout(
            alarmPagingItems = alarmPagingItems,
            errorState = LoadState.Error(NetworkError.NoConnection())
        )
    }
}
