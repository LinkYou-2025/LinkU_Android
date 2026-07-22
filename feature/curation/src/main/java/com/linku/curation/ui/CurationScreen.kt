package com.linku.curation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.linku.curation.ui.calendar.CalendarBox
import com.linku.curation.ui.header.CurationHeader
import com.linku.curation.ui.main_card.CurationMainCardPager
import com.linku.curation.ui.util.CurationBackground
import com.linku.curation.viewModel.CurationViewModel
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.top.bar.TopBar
import com.linku.design.util.scaler

// 큐레이션 메인 카드 개수 (pm이 나중에 3개 이상으로 확장될 수도 있다고 함)
private const val CURATION_CARD_COUNT = 3

// 1번 <-> 3번 카드가 서로 이어지는 양방향(무한) 스와이프를 흉내내기 위한 가상 페이지 수.
// 실제 카드는 CURATION_CARD_COUNT개뿐이라 page % CURATION_CARD_COUNT로 실제 인덱스를 구한다.
private const val PAGER_VIRTUAL_PAGE_COUNT = Int.MAX_VALUE

@Composable
internal fun CurationScreen(
    nickname: String,
    viewModel: CurationViewModel = hiltViewModel(), //TODO princehw가 구현해줄거임!
    onMonthlyDetailClick: () -> Unit = {}, // 1번 카드 -> CurationMonthlyDetailScreen
    onKeywordDetailClick: () -> Unit = {}, // 2번 카드 -> CurationKeywordDetailScreen
    onRemindClick: () -> Unit = {}, // 3번 카드 -> CurationRemindScreen
    onMonthlyCurationClick: () -> Unit = {},
) {
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
                pagerState = pagerState,
                onCardClick = { index, _ ->
                    when (index) {
                        0 -> onMonthlyDetailClick()
                        1 -> onKeywordDetailClick()
                        2 -> onRemindClick()
                    }
                },
                onMonthlyCurationClick = onMonthlyCurationClick
            )
        }
    }
}

@Composable
private fun CurationScreenContent(
    modifier: Modifier = Modifier,
    nickname: String,
    pagerState: PagerState,
    onCardClick: (index: Int, imageUrl: String?) -> Unit,
    onMonthlyCurationClick: () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize()) {
        CurationHeader(nickname = nickname)

        Spacer(modifier = Modifier.height(26.scaler))

        CurationMainCardPager(
            imageUrls = List(CURATION_CARD_COUNT) { "" },
            pagerState = pagerState,
            onCardClick = onCardClick
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
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCurationScreen() {
    LinkuPreview {
        CurationScreen(nickname = "세나")
    }
}
