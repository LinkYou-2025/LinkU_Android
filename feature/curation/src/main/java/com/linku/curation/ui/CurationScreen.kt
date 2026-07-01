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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.linku.curation.CurationViewModel
import com.linku.curation.ui.calendar.CalendarBox
import com.linku.curation.ui.header.CurationHeader
import com.linku.curation.ui.main_card.CurationMainCardPager
import com.linku.curation.ui.screen.CurationKeywordDetailScreen
import com.linku.curation.ui.util.CurationBackground
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.top.bar.TopBar
import com.linku.design.util.scaler

@Composable
fun CurationScreen(
    nickname: String,
    viewModel: CurationViewModel = hiltViewModel(), //TODO princehw가 구현해줄거임!
    onCard1Click: () -> Unit = {}, // TODO 다음 PR에서 구현..
    onCard3Click: () -> Unit = {}, // TODO 다음 PR에서 구현..
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    var showKeywordDetail by remember { mutableStateOf(false) }
    val displayNickname = nickname.ifBlank { "세나" }

    Box(modifier = Modifier.fillMaxSize()) {
        CurationBackground(showLogo = true)

        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(showSearchBar = false, backgroundColor = null)

            Spacer(modifier = Modifier.height(28.scaler))

            CurationScreenContent(
                nickname = displayNickname,
                pagerState = pagerState,
                onCardClick = { index, _ ->
                    when (index) {
                        0 -> onCard1Click()
                        1 -> showKeywordDetail = true
                        2 -> onCard3Click()
                    }
                }
            )
        }

        if (showKeywordDetail) {
            CurationKeywordDetailScreen(
                nickname = displayNickname,
                onBack = { showKeywordDetail = false },
                onHome = { showKeywordDetail = false }
            )
        }
    }
}

@Composable
private fun CurationScreenContent(
    nickname: String,
    pagerState: PagerState,
    onCardClick: (index: Int, imageUrl: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        CurationHeader(nickname = nickname)

        Spacer(modifier = Modifier.height(26.scaler))

        CurationMainCardPager(
            imageUrls = List(3) { "" }, //아 pm이 나중에 3개 이상으로 확장될 수도 있대요. 그래서 이렇게 했어요
            pagerState = pagerState,
            onCardClick = onCardClick
        )

        Spacer(modifier = Modifier.height(33.scaler))

        Text(
            text = "지난 큐레이션",
            fontSize = 22.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight(700),
            color = MaterialTheme.linkuColors.black,
            modifier = Modifier.padding(horizontal = 24.scaler)
        )

        Spacer(modifier = Modifier.height(18.scaler))

        CalendarBox(modifier = Modifier.padding(horizontal = 18.scaler))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCurationScreen() {
    LinkuPreview {
        CurationScreen(nickname = "세나")
    }
}
