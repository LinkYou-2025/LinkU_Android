package com.linku.curation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.linku.curation.ui.chip.CurationKeywordCloud
import com.linku.curation.ui.header.CurationTopHeader
import com.linku.curation.ui.util.CurationGradientCircleBackground
import com.linku.curation.viewModel.CurationViewModel
import com.linku.design.component.BottomGradientButton
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
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
 * @param onBack 백버튼 클릭 콜백
 * @param onGoHome "링크 저장하러 가기" 버튼 클릭 시 호출. 홈 탭으로 이동
 * @param onKeywordClick 키워드 칩 클릭 시 호출. 클릭된 키워드("#" 없이) 전달
 */
@Composable
internal fun CurationKeywordDetailScreen(
    nickname: String,
    viewModel: CurationViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onGoHome: () -> Unit = {},
    onKeywordClick: (String) -> Unit = {},
) {
    CurationKeywordDetailContent(
        nickname = nickname,
        keywords = sampleKeywords, // TODO: viewModel에서 실제 응답 받은 topTags로 교체
        onBack = onBack,
        onGoHome = onGoHome,
        onKeywordClick = onKeywordClick,
    )
}

@Composable
private fun CurationKeywordDetailContent(
    nickname: String,
    keywords: List<String>,
    onBack: () -> Unit = {},
    onGoHome: () -> Unit = {},
    onKeywordClick: (String) -> Unit = {},
) {
    val colorTheme = MaterialTheme.linkuColors
    val isEmpty = keywords.isEmpty()

    BackHandler { onBack() }

    CurationGradientCircleBackground(modifier = Modifier.fillMaxSize()) {
        if (isEmpty) {
            CurationTopHeader(
                onBackClick = onBack,
                contentTopOffset = 406.scaler,
                title = "이번달 관심 키워드를\n도출하지 못했어요",
                description = "더 많은 링크를 저장해주세요!",
                titleDescriptionGap = 12.scaler,
            )
        } else {
            CurationTopHeader(
                onBackClick = onBack,
                contentTopOffset = 92.scaler,
                title = "${nickname}님과 같은 대학생들은\n이번 달, 이런 키워드를 많이 봤어요",
                description = "나와 비슷한 사람들의 관심 키워드",
                titleDescriptionGap = 12.scaler,
            )

            CurationKeywordCloud(
                keywords = keywords,
                onKeywordClick = { _, keyword -> onKeywordClick(keyword) }
            )
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
                activeGradient = colorTheme.maincolor,
                inactiveGradient = colorTheme.inactiveColor,
                onClick = onGoHome,
            )
        }
    }
}

@Preview(name = "큐레이션 2번 카드 상세 (#43-1)", showBackground = true)
@Composable
private fun CurationKeywordDetailScreenPreview() {
    LinkuPreview {
        CurationKeywordDetailContent(
            nickname = "세나",
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
            keywords = emptyList(),
        )
    }
}
