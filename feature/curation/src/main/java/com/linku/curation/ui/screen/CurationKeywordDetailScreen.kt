package com.linku.curation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.curation.ui.chip.CurationKeywordCloud
import com.linku.curation.ui.header.CurationTopHeader
import com.linku.curation.ui.util.CurationCircleProgressBar
import com.linku.curation.ui.util.CurationGradientCircleBackground
import com.linku.curation.viewModel.CurationKeywordViewModel
import com.linku.curation.viewModel.sideeffect.CurationKeyWordSideEffect
import com.linku.design.component.BottomGradientButton
import com.linku.design.component.TimedCustomToastMessage
import com.linku.design.theme.LinkuPreview
import com.linku.design.util.scaler

// TODO: 백엔드 topTags 응답으로 교체 (랭킹 순서 그대로, 최대 9개) -> ui 확인용 임.
private val sampleKeywords = listOf(
    "퇴근", "영어공부", "오픽", "휴가지", "오픽",
    "엑셀꿀팁", "경제", "점심메뉴", "이직",
)

/**
 * 큐레이션 2번 카드 상세(#43-1) 화면. "이번 달 관심 키워드" 워드클라우드를 보여준다.
 *
 * @param nickname 상단 문구에 쓰일 사용자 닉네임
 * @param month 키워드 조회에 사용할 월 (`yyyy-MM` 형식). ViewModel이 SavedStateHandle로 수신
 * @param onBack 백버튼 클릭 콜백
 * @param onGoHome "링크 저장하러 가기" 버튼 클릭 시 호출. 홈 탭으로 이동
 * @param onKeywordClick 키워드 칩 클릭 시 호출. 클릭된 키워드("#" 없이) 전달
 */
@Composable
internal fun CurationKeywordDetailScreen(
    nickname: String,
    month: String,
    viewModel: CurationKeywordViewModel,
    onBack: () -> Unit = {},
    onGoHome: () -> Unit = {},
    onKeywordClick: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var toastMessage by remember { mutableStateOf("") }
    var isToastVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is CurationKeyWordSideEffect.ShowToast -> {
                    toastMessage = effect.message
                    isToastVisible = true
                }
                is CurationKeyWordSideEffect.NavigateToCurationKeywordLinks ->
                    onKeywordClick(effect.keyword)
            }
        }
    }

    Box {
        CurationKeywordDetailContent(
            nickname = state.nickname.ifBlank { nickname },
            jobName = state.jobName,
            keywords = state.keywords.map { it.name },
            isLoading = state.isLoading,
            isError = state.isError,
            onBack = onBack,
            onGoHome = onGoHome,
            onKeywordClick = onKeywordClick,
        )

        TimedCustomToastMessage(
            visible = isToastVisible,
            toastMessage = toastMessage,
            onDismiss = { isToastVisible = false },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 92.scaler)
        )
    }
}

@Composable
private fun CurationKeywordDetailContent(
    nickname: String,
    jobName: String,
    keywords: List<String>,
    isLoading: Boolean = false,
    isError: Boolean = false,
    onBack: () -> Unit = {},
    onGoHome: () -> Unit = {},
    onKeywordClick: (String) -> Unit = {},
) {
    val isEmpty = keywords.isEmpty()

    BackHandler { onBack() }

    CurationGradientCircleBackground(modifier = Modifier.fillMaxSize()) {
        // 에러나면 배경만 띄우고 토스트 출력 - 요구사항
        if (isError) return@CurationGradientCircleBackground

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CurationCircleProgressBar()
            }
            return@CurationGradientCircleBackground
        }

        if (isEmpty) {
            CurationTopHeader(
                onBackClick = onBack,
                contentTopOffset = 406.scaler,
                title = "이번달 관심 키워드를\n도출하지 못했어요",
                description = "더 많은 링크를 저장해주세요!",
                titleDescriptionGap = 12.scaler,
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                CurationTopHeader(
                    onBackClick = onBack,
                    contentTopOffset = 92.scaler,
                    title = "${nickname}님과 같은 ${jobName}들은\n이번 달, 이런 키워드를 많이 봤어요",
                    description = "나와 비슷한 사람들의 관심 키워드",
                    titleDescriptionGap = 12.scaler,
                )

                CurationKeywordCloud(
                    modifier = Modifier.weight(1f),
                    keywords = keywords,
                    onKeywordClick = { _, keyword -> onKeywordClick(keyword) },
                )
            }
        }

        // 하단 영역 (EmailVerificationScreen과 동일한 방식으로 하단 고정)
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BottomGradientButton(
                text = "링크 저장하러 가기",
                enabled = true,
                onClick = onGoHome,
            )
        }
    }
}

@Preview(name = "#43-1 로딩 중", showBackground = true)
@Composable
private fun CurationKeywordDetailScreenLoadingPreview() {
    LinkuPreview {
        CurationKeywordDetailContent(
            nickname = "세나",
            jobName = "대학생",
            keywords = emptyList(),
            isLoading = true,
        )
    }
}

@Preview(name = "큐레이션 2번 카드 상세 (#43-1)", showBackground = true)
@Composable
private fun CurationKeywordDetailScreenPreview() {
    LinkuPreview {
        CurationKeywordDetailContent(
            nickname = "세나",
            jobName = "대학생",
            keywords = sampleKeywords,
        )
    }
}

@Preview(name = "#43-1 예외_키워드 없는 경우", showBackground = true)
@Composable
private fun CurationKeywordDetailScreenEmptyPreview() {
    LinkuPreview {
        CurationKeywordDetailContent(
            nickname = "세나",
            jobName = "대학생",
            keywords = emptyList(),
        )
    }
}
