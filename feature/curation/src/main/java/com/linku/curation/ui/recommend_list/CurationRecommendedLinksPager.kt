package com.linku.curation.ui.recommend_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.curation.LinkType
import com.linku.core.model.curation.RecommendLink
import com.linku.design.component.LinkCardItem
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

//인디케이터 도트는 최대 이 개수만큼만 보여줌
private const val MAX_INDICATOR_DOTS = 9
// 기획상으로 9개씩 제공해주는 것으로 알고 있는데 혹시 모를 이슈 대응을 위해 1~9개까지 대응되게 구현했습니다.
//추후 9개 이상도 가능하게 확장성 고려했습니다. 나중에 숫자 변경을 하면 됩니다.
// 현재 스웨거상 4개 내려주는 것을 확인해서 유동적으로 사용할 수 있게 확장해서 짰습니다:)

/**
 * 큐레이션 카드 상세(#42-1)의 "추천 링크" 섹션.
 *
 * 링크를 [linksPerPage]개씩 묶어 [HorizontalPager]의 한 페이지로 보여주고,
 * 스와이프로 다음 묶음으로 넘어갈 수 있다. 페이지가 2개 이상일 때만 하단에 도트 인디케이터를 그린다.
 *
 * @param links 추천 링크 목록
 * @param linksPerPage 한 페이지에 보여줄 링크 개수
 * @param onLinkClick 링크 카드 클릭 시 호출. 클릭된 링크 전달
 * @param onDeleteClick 카드의 "더보기" 메뉴에서 삭제 선택 시 호출. 클릭된 링크 전달
 */
@Composable
fun CurationRecommendedLinksPager(
    modifier: Modifier = Modifier,
    links: List<RecommendLink>,
    linksPerPage: Int = 3,
    onLinkClick: (RecommendLink) -> Unit = {},
    onDeleteClick: (RecommendLink) -> Unit = {},
) {
    if (links.isEmpty()) return

    val colorTheme = MaterialTheme.linkuColors
    val pages = remember(links, linksPerPage) { links.chunked(linksPerPage) }
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "추천 링크",
            style = LocalTextStyle.current.copy(
                brush = colorTheme.emotionTitleGradient,
                fontSize = 18.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight(600),
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(25.scaler))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Column(verticalArrangement = Arrangement.spacedBy(10.scaler)) {
                pages[page].forEach { link ->
                    RecommendedLinkCardItem(
                        link = link,
                        onClick = onLinkClick,
                    )
                }
            }
        }

        if (pages.size > 1) {
            Spacer(modifier = Modifier.height(20.dp))

            CurationLinksPagerIndicator(
                pageCount = pages.size,
                currentPage = pagerState.currentPage
            )
        }
    }
}

/**
 * "추천 링크" 섹션 전용 페이지 인디케이터.
 *
 * 선택된 페이지는 32x6 파란 pill로, 나머지는 6x6 회색 원으로 표시.
 * 페이지가 [MAX_INDICATOR_DOTS]개보다 많아도 도트는 최대 그만큼만 그린다.
 */
@Composable
private fun CurationLinksPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    val colorTheme = MaterialTheme.linkuColors

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount.coerceAtMost(MAX_INDICATOR_DOTS)) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .width(if (isSelected) 32.dp else 6.dp)
                    .height(6.dp)
                    .background(
                        color = if (isSelected) colorTheme.blue[200] else colorTheme.gray[400],
                        shape = RoundedCornerShape(if (isSelected) 3.5.dp else 6.dp)
                    )
            )
        }
    }
}

// 연동해주는 분이 분리 여부를 결정해주세요:)
@Composable
private fun RecommendedLinkCardItem(
    link: RecommendLink,
    onClick: (RecommendLink) -> Unit,
) {
    //LinkCardItem은 지현이가 구현해서 여기서 궁금한거 있으면 그녀에게...
    LinkCardItem(
        hasAiSummary = false,
        linkTitle = link.title,
        tags = link.categories.take(2),
        domainName = link.domain,
        isExternalLink = link.type is LinkType.External,
        isMoreVisible = false,
        linkImageUrl = link.imageUrl,
        domainImageUrl = link.domainImageUrl,
        onCardClick = { onClick(link) },
    )
}

// 프리뷰용 링크 N개 생성 (3개 샘플을 돌려가며 채움 -> 태그 없는 카드도 섞여서 나옴)
private fun demoLinks(count: Int): List<RecommendLink> {
    val samples = listOf(
        RecommendLink(
            userLinkuId = 1L,
            title = "오픽 AL 따는 꿀팁 얼른 보러오세요",
            url = "https://blog.naver.com/example",
            imageUrl = "",
            domain = "blog.naver.com",
            domainImageUrl = "",
            categories = listOf("생산성·툴", "평온"),
            type = LinkType.Internal(linkId = 1L)
        ),
        RecommendLink(
            userLinkuId = 2L,
            title = "오픽 공부할 때 문법도 공부해야할까?",
            url = "https://github.com/example",
            imageUrl = "",
            domain = "github.com",
            domainImageUrl = "",
            categories = listOf("여행", "행복"),
            type = LinkType.Internal(linkId = 2L)
        ),
        RecommendLink(
            userLinkuId = null,
            title = "글램핑 예약, 누구보다 싸게하기",
            url = "https://blog.naver.com/example2",
            imageUrl = "",
            domain = "blog.naver.com",
            domainImageUrl = "",
            categories = emptyList(),
            type = LinkType.External(url = "https://blog.naver.com/example2")
        )
    )

    return List(count) { index ->
        samples[index % samples.size].copy(userLinkuId = index.toLong())
    }
}

@Preview(name = "추천 링크 - 1개(1페이지, 인디케이터 없음)", showBackground = true)
@Composable
private fun CurationRecommendedLinksPagerPreview_1Link() {
    LinkuPreview {
        CurationRecommendedLinksPager(links = demoLinks(1))
    }
}

@Preview(name = "추천 링크 - 4개(2페이지)", showBackground = true)
@Composable
private fun CurationRecommendedLinksPagerPreview_4Links() {
    LinkuPreview {
        CurationRecommendedLinksPager(links = demoLinks(4))
    }
}

@Preview(name = "추천 링크 - 8개(3페이지)", showBackground = true)
@Composable
private fun CurationRecommendedLinksPagerPreview_8Links() {
    LinkuPreview {
        CurationRecommendedLinksPager(links = demoLinks(8))
    }
}

@Preview(name = "추천 링크 - 9개(3페이지, 최대치)", showBackground = true)
@Composable
private fun CurationRecommendedLinksPagerPreview_9Links() {
    LinkuPreview {
        CurationRecommendedLinksPager(links = demoLinks(9))
    }
}
