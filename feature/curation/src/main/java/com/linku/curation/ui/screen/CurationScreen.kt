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
import com.linku.curation.viewModel.intent.CurationMainIntent
import com.linku.curation.viewModel.sideeffect.CurationMainSideEffect
import com.linku.curation.viewModel.CurationViewModel
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.top.bar.TopBar
import com.linku.design.util.scaler

private const val CURATION_CARD_COUNT = 3
private const val PAGER_VIRTUAL_PAGE_COUNT = Int.MAX_VALUE

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
    val curationMain by viewModel.curationMainState.collectAsStateWithLifecycle()

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

        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(showSearchBar = false, backgroundColor = null)

            Spacer(modifier = Modifier.height(12.scaler))

            CurationScreenContent(
                nickname = displayNickname,
                sections = curationMain?.sections ?: emptyList(),
                pagerState = pagerState,
                onCardClick = { index ->
                    val month = curationMain?.latestCurationMonth ?: return@CurationScreenContent
                    when (index) {
                        0 -> viewModel.handleIntent(
                            CurationMainIntent.ClickCurationSection(
                                curationId = curationMain?.latestCurationId ?: return@CurationScreenContent,
                                month = month
                            )
                        )
                        1 -> viewModel.handleIntent(CurationMainIntent.ClickLastMonthKeyWord(month))
                        2 -> viewModel.handleIntent(CurationMainIntent.ClickUnreadLink)
                    }
                },
                onMonthlyCurationClick = {
                    val month = curationMain?.latestCurationMonth ?: return@CurationScreenContent
                    viewModel.handleIntent(CurationMainIntent.ClickYearHistory(month))
                }
            )
        }
    }
}

@Composable
private fun CurationScreenContent(
    modifier: Modifier = Modifier,
    nickname: String,
    sections: List<SectionItem>,
    pagerState: PagerState,
    onCardClick: (index: Int) -> Unit,
    onMonthlyCurationClick: () -> Unit = {},
) {
    val imageUrls = List(CURATION_CARD_COUNT) { index ->
        sections.find { it.section == index + 1 }?.imageUrl ?: ""
    }

    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxWidth().verticalScroll(scrollState)) {
        CurationHeader(nickname = nickname)

        Spacer(modifier = Modifier.height(26.scaler))

        CurationMainCardPager(
            imageUrls = imageUrls,
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
            pagerState = pagerState,
            onCardClick = {},
            onMonthlyCurationClick = {}
        )
    }
}

