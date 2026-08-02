package com.linku.curation.ui.monthly

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.curation.ui.header.CurationTopHeader
import com.linku.curation.viewModel.CurationHistoryViewModel
import com.linku.curation.viewModel.intent.CurationHistoryIntent
import com.linku.curation.viewModel.sideeffect.CurationHistorySideEffect
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

/**
 * 월별 큐레이션 모아보기(#41-1) 화면.
 * ViewModel을 주입받아 [MonthlyCurationScreenContent]에 상태를 전달한다.
 *
 * @param year 상단에 표시할 연도. ex) 2026
 * @param onBackClick 백버튼 클릭 콜백
 * @param onMonthClick 월 카드 클릭 콜백. 클릭된 월(1~12)을 전달함
 */
@Composable
fun MonthlyCurationScreen(
    modifier: Modifier = Modifier,
    viewModel: CurationHistoryViewModel = hiltViewModel(),
    year: Int = 2026,
    onBackClick: () -> Unit = {},
    onMonthClick: (Long) -> Unit = {},
) {
    val context = LocalContext.current
    val state by viewModel.curationHistoryState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is CurationHistorySideEffect.NavigateToCurationDetail -> onMonthClick(effect.curationId)
                is CurationHistorySideEffect.ShowToast ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // "yyyy-MM" 형식의 month에서 월(1~12)을 추출해 키로 사용
    val historyMap = state.curationHistory.associateBy { it.month.takeLast(2).toIntOrNull() ?: 0 }

    MonthlyCurationScreenContent(
        modifier = modifier,
        year = year,
        onBackClick = onBackClick,
        onMonthClick = { month ->
            // 클릭된 월(Long)로 curationId 조회, 없으면 무시
            val curationId = historyMap[month.toInt()]?.curationId ?: return@MonthlyCurationScreenContent
            viewModel.handleIntent(CurationHistoryIntent.ClickCurationHistory(curationId))
        },
        imageUrlOf = { month -> historyMap[month]?.thumbnailUrl }
    )
}

/**
 * 월별 큐레이션 모아보기(#41-1) 실제 UI.
 * [CurationTopHeader](연도 + "월간 큐레이션") 아래 44.49 간격을 두고 [MonthlyCurationGrid]를 보여준다.
 *
 * 값 받으면 헤더 뒤에 배경 Box 하나 추가하면 됨.
 *
 * @param year 상단에 표시할 연도. ex) 2026
 * @param onBackClick 백버튼 클릭 콜백
 * @param onMonthClick 월 카드 클릭 콜백. 클릭된 월(1~12)을 전달함
 * @param imageUrlOf 월(1~12)에 해당하는 카드 배경 이미지 URL을 반환하는 함수. 없으면 null 반환
 */
@Composable
private fun MonthlyCurationScreenContent(
    modifier: Modifier = Modifier,
    year: Int = 2026,
    onBackClick: () -> Unit = {},
    onMonthClick: (Long) -> Unit = {},
    imageUrlOf: (Int) -> String? = { null },
) {
    val colorTheme = MaterialTheme.linkuColors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorTheme.white)
    ) {
        CurationTopHeader(
            onBackClick = onBackClick,
            contentTopOffset = 92.scaler,
            title = "${year}년",
            description = "월간 큐레이션",
            titleDescriptionGap = 8.scaler // 피그마상 12인데 아무리 봐도 12간격으로는... 피그마랑 다른데? 6로 했는데 디자이너와 조정해주세용
        )

        Spacer(modifier = Modifier.height(44.49f.scaler))

        MonthlyCurationGrid(
            onMonthClick = onMonthClick,
            imageUrlOf = imageUrlOf
        )
    }
}

@Preview(name = "월별 큐레이션 모아보기 (#41-1)", showBackground = true)
@Composable
private fun MonthlyCurationScreenContentPreview() {
    LinkuPreview {
        MonthlyCurationScreenContent(
            imageUrlOf = { "https://example.com/preview.jpg" }
        )
    }
}
