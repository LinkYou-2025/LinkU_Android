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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.LocalColorTheme
import com.linku.home.component.NoticeTitleSection
import com.linku.home.ui.alarm.component.AlarmTopBar
import com.linku.home.viewmodel.NoticeViewModel

@Composable
fun NoticeScreen(
    onBack: () -> Unit,
    //viewModel: NoticeViewModel = hiltViewModel()
) {
    NoticeScreenContent(
        onBack = onBack
    )
}

@Composable
fun NoticeScreenContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.white)
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 56.dp, bottom = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlarmTopBar(
            onBack = onBack,
            topText = "시스템/공지"
        )

        Spacer(Modifier.height(40.dp))

        // TODO: api연동 시 뷰모델에서 상태 호이스팅
        NoticeTitleSection(
            title = "개인정보 이용제공·내역 안내",
            noticeDate = "26.03.31"
        )

        Spacer(Modifier.height(20.dp))

        HorizontalDivider(color = LocalColorTheme.current.gray[300])

        Spacer(Modifier.height(32.dp))

        // TODO: api연동 시 뷰모델에서 상태 호이스팅
        // TODO: 피그마에서 보니 공지 content가 길던데, 스웨거에서 확인해보니 응답값은 짧게 옴. 다인 누나와 백엔드에게 물어보기
        Text(
            text = "새로운 기능이 추가됐어요, 지금 확인해보세요",
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
    ThemeProvider {
        NoticeScreen(onBack = {})
    }
}
