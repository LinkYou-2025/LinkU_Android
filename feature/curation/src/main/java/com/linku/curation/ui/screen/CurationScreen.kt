package com.linku.curation.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.core.model.curation.SectionItem
import com.linku.curation.ui.calendar.CalendarBox
import com.linku.curation.ui.header.CurationHeader
import com.linku.curation.ui.main_card.CurationMainCardPager
import com.linku.curation.ui.util.CurationBackground
import com.linku.curation.ui.util.CurationErrorLayout
import com.linku.curation.viewModel.intent.CurationMainIntent
import com.linku.curation.viewModel.sideeffect.CurationMainSideEffect
import com.linku.curation.viewModel.CurationViewModel
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.top.bar.TopBar
import com.linku.design.util.scaler

private const val CURATION_CARD_COUNT = 3
private const val PAGER_VIRTUAL_PAGE_COUNT = Int.MAX_VALUE

// 1번(키워드)/2번(리마인드) 카드는 API가 아직 콘텐츠를 내려주지 않을 때 쓰는 고정 멘트.
// 0번(월간) 카드는 서버 응답(SectionItem)이 항상 있는 걸 기준으로 하므로 별도 고정값을 두지 않는다.
private val DEFAULT_CARD_TITLES = listOf(
    "", // 서버에서 내려줌
    "지난 달의 나를\n가장 잘 보여주는 키워드",
    "잊고 있던 '나중에 보기',\n오늘이 그날이에요!"
)
private val DEFAULT_CARD_DESCRIPTIONS = listOf(
    "이번 달을 위한 링크, 링큐가 준비했어요",
    "많이 본 키워드로 링크들을 모아봤어요",
    "저장만 해두기엔 아까운 링크들이 기다려요"
)

@Composable
fun CurationScreen(
    nickname: String,
    viewModel: CurationViewModel,
    onMonthlyDetailClick: (month: String, curationId: Long) -> Unit = { _, _ -> },
    onKeywordDetailClick: (month: String) -> Unit = {},
    onRemindClick: () -> Unit = {},
    onCurationHistoryClick: (year: Int) -> Unit = {},
) {
    val context = LocalContext.current
    val state by viewModel.curationMainState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is CurationMainSideEffect.NavigateToCurationSection -> onMonthlyDetailClick(effect.month, effect.curationId)
                is CurationMainSideEffect.NavigateToLastMonthKeyWord -> onKeywordDetailClick(effect.month)
                is CurationMainSideEffect.NavigateToUnreadLink -> onRemindClick()

                // 최신 큐레이션 조회 api응답 중 year부분만 파싱해서 전달.
                is CurationMainSideEffect.NavigateToYearHistory ->
                    effect.month
                        .substringBefore('-')
                        .toIntOrNull()
                        ?.let(onCurationHistoryClick)

                is CurationMainSideEffect.ShowToast ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val pagerState = rememberPagerState(
        initialPage = PAGER_VIRTUAL_PAGE_COUNT / 2 - (PAGER_VIRTUAL_PAGE_COUNT / 2).mod(
            CURATION_CARD_COUNT
        ),
        pageCount = { PAGER_VIRTUAL_PAGE_COUNT }
    )
    val displayNickname = nickname.ifBlank { "세나" }

    Box(modifier = Modifier.fillMaxSize()) {
        CurationBackground(modifier = Modifier.fillMaxSize(), showLogo = true)

        if (state.errorMessage.isNotEmpty()) {
            CurationErrorLayout(
                errorMessage = state.errorMessage,
                onRetry = { viewModel.handleIntent(CurationMainIntent.Retry) }
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                TopBar(showSearchBar = false, backgroundColor = null)

                Spacer(modifier = Modifier.height(12.scaler))

                CurationScreenContent(
                    nickname = displayNickname,
                    sections = state.curationMain?.sections ?: emptyList(),
                    latestCurationMonth = state.curationMain?.latestCurationMonth,
                    pagerState = pagerState,
                    onCardClick = { index ->
                        val month = state.curationMain?.latestCurationMonth ?: return@CurationScreenContent
                        when (index) {
                            0 -> viewModel.handleIntent(
                                CurationMainIntent.ClickCurationSection(
                                    curationId = state.curationMain?.latestCurationId ?: return@CurationScreenContent,
                                    month = month
                                )
                            )
                            1 -> viewModel.handleIntent(CurationMainIntent.ClickLastMonthKeyWord(month))
                            2 -> viewModel.handleIntent(CurationMainIntent.ClickUnreadLink)
                        }
                    },
                    onMonthlyCurationClick = {
                        state.curationMain?.latestCurationMonth?.let { month ->
                            viewModel.handleIntent(CurationMainIntent.ClickYearHistory(month))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CurationScreenContent(
    modifier: Modifier = Modifier,
    nickname: String,
    sections: List<SectionItem>,
    latestCurationMonth: String?,
    pagerState: PagerState,
    onCardClick: (index: Int) -> Unit,
    onMonthlyCurationClick: () -> Unit = {},
) {
    // 섹션별 이미지 URL을 카드 순서에 맞게 구성
    val imageUrls = List(CURATION_CARD_COUNT) { index ->
        sections.find { it.section == index + 1 }?.imageUrl ?: ""
    }

    // 섹션별 제목을 구성하고, 없으면 기본 제목 사용
    val titles = List(CURATION_CARD_COUNT) { index ->
        sections.find { it.section == index + 1 }?.title?.takeIf { it.isNotBlank() }
            ?: DEFAULT_CARD_TITLES.getOrElse(index) { "" }
    }

    // 섹션별 설명을 구성하고, 없으면 기본 설명 사용
    val descriptions = List(CURATION_CARD_COUNT) { index ->
        sections.find { it.section == index + 1 }?.description?.takeIf { it.isNotBlank() }
            ?: DEFAULT_CARD_DESCRIPTIONS.getOrElse(index) { "" }
    }

    // "yyyy-MM" 형식의 최신 큐레이션 월에서 월(月) 정수만 추출. 0번(월간) 카드의 고정 이미지 선택에 사용.
    val month = latestCurationMonth?.substringAfter('-')?.toIntOrNull() ?: 0

    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxWidth().verticalScroll(scrollState)) {
        CurationHeader(nickname = nickname)

        Spacer(modifier = Modifier.height(26.scaler))

        CurationMainCardPager(
            imageUrls = imageUrls,
            titles = titles,
            descriptions = descriptions,
            month = month,
            pagerState = pagerState,
            onCardClick = { index, _ -> onCardClick(index) }
        )

        Spacer(modifier = Modifier.height(26.scaler))

        Text(
            text = "지난 큐레이션",
            fontSize = 22.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight(700),
            color = MaterialTheme.linkuColors.black,
            modifier = Modifier.padding(horizontal = 24.scaler)
        )

        Spacer(modifier = Modifier.height(18.scaler))

        CalendarBox(
            modifier = Modifier.padding(horizontal = 20.scaler),
            onClick = onMonthlyCurationClick
        )

        Spacer(modifier = Modifier.height(15.scaler))
    }
}

@Preview(showBackground = true)
@Composable
private fun CurationScreenContentPreview() {
    val sections = listOf(
        SectionItem(
            section = 0,
            title = "Section 1",
            description = "Description 1",
            imageUrl = ""
        ),
        SectionItem(
            section = 1,
            title = "Section 2",
            description = "Description 2",
            imageUrl = ""
        ),
        SectionItem(
            section = 2,
            title = "Section 3",
            description = "Description 3",
            imageUrl = ""
        )
    )
    val pagerState = rememberPagerState(
        initialPage = PAGER_VIRTUAL_PAGE_COUNT / 2 - (PAGER_VIRTUAL_PAGE_COUNT / 2).mod(
            CURATION_CARD_COUNT
        ),
        pageCount = { PAGER_VIRTUAL_PAGE_COUNT }
    )
    LinkuPreview {
        CurationScreenContent(
            nickname = "테스트",
            sections = sections,
            latestCurationMonth = "2026-08",
            pagerState = pagerState,
            onCardClick = {},
            onMonthlyCurationClick = {}
        )
    }
}

