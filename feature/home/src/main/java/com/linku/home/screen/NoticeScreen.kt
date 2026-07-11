package com.linku.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.core.model.alarm.AlarmDetail
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LinkuPreview
import com.linku.home.component.NoticeTitleSection
import com.linku.home.model.NoticeUiState
import com.linku.home.ui.alarm.component.AlarmTopBar
import com.linku.home.viewmodel.NoticeViewModel

@Composable
fun NoticeScreen(
    onBack: () -> Unit,
    viewModel: NoticeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NoticeScreenContent(
        onBack = onBack,
        uiState = uiState
    )
}

@Composable
fun NoticeScreenContent(
    modifier: Modifier = Modifier,
    uiState: NoticeUiState,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.white)
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlarmTopBar(
            onBack = onBack,
            topText = "시스템/공지"
        )

        Spacer(Modifier.height(40.dp))

        NoticeTitleSection(
            title = uiState.detail.title,
            noticeDate = uiState.detail.noticeDate
        )

        Spacer(Modifier.height(20.dp))

        HorizontalDivider(color = LocalColorTheme.current.gray[300])

        Spacer(Modifier.height(32.dp))

        // TODO: 피그마에서 보니 공지 content가 길던데, 스웨거에서 확인해보니 응답값은 짧게 옴. 다인 누나와 백엔드에게 물어보기
        Text(
            text = uiState.detail.content,
            modifier = Modifier
                .fillMaxSize(),
            textAlign = TextAlign.Start,
            color = LocalColorTheme.current.gray[800],
            fontSize = 15.sp
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun NoticeScreenPreview() {
    LinkuPreview {
        NoticeScreenContent(
            uiState = NoticeUiState(
                detail = AlarmDetail(
                    title = "링큐 서비스 점검 안내",
                    noticeDate = "2024.03.15"
                )
            ),
            onBack = {}
        )
    }
}
