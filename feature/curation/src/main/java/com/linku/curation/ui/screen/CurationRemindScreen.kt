package com.linku.curation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.linku.core.model.CategoryType
import com.linku.core.model.EmotionType
import com.linku.core.model.LinkSimpleInfo
import com.linku.curation.CurationViewModel
import com.linku.curation.ui.header.CurationTopHeader
import com.linku.curation.ui.util.CurationGradientCircleBackground
import com.linku.design.component.LinkCardItem
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

@Composable
fun CurationRemindScreen(
    onBack: () -> Unit,
    viewModel: CurationViewModel = hiltViewModel(),
) {
    BackHandler { onBack() }

    val remindLinkItems = viewModel.remindLinks.collectAsLazyPagingItems()

    CurationRemindScreenContent(
        remindLinkItems = remindLinkItems,
        onBack = onBack,
    )
}

/**
 * "n년 n월"을 오늘로부터 정확히 한 달 전 기준으로 반환. API 없이 고정 문구라 로컬에서 계산.
 */
private fun remindMonthText(): String {
    val lastMonth = LocalDate.now().minusMonths(1)
    return "${lastMonth.year}년 ${lastMonth.monthValue}월"
}

/**
 * 링크가 0개일 때 노출되는 헤더. Paging 상태와 무관하게 별도 컴포저블로 분리해서,
 * 프리뷰에서 [collectAsLazyPagingItems]를 거치지 않고 이 상태를 바로 확인할 수 있게 함.
 */
@Composable
private fun CurationRemindEmptyHeader(onBack: () -> Unit) {
    CurationTopHeader(
        onBackClick = onBack,
        contentTopOffset = 406.scaler,
        title = "지난 달 저장한 링크를\n잘 보고 있어요",
        description = "저장해둔 링크들을 꾸준히 소비하고 있네요!",
        titleDescriptionGap = 12.scaler,
    )
}

@Composable
private fun CurationRemindScreenContent(
    remindLinkItems: LazyPagingItems<LinkSimpleInfo>,
    onBack: () -> Unit,
) {
    val isEmpty = remindLinkItems.loadState.refresh is LoadState.NotLoading &&
            remindLinkItems.itemCount == 0

    CurationGradientCircleBackground {
        Column(modifier = Modifier.fillMaxWidth()) {

            if (isEmpty) {
                CurationRemindEmptyHeader(onBack = onBack)
            } else {
                CurationTopHeader(
                    onBackClick = onBack,
                    contentTopOffset = 105.scaler,
                    title = "${remindMonthText()},\n저장만 하고 열어보지 않은 링크예요",
                    // TODO: itemCount는 지금까지 페이징으로 불러온 개수라 스크롤할수록 값이 커짐.
                    // API 연동 후엔 서버가 내려주는 "전체 저장 링크 수" 필드로 교체해야 함(페이징 상태와 무관한 고정값).
                    description = "총 ${remindLinkItems.itemCount}개의 링크가 쌓여있네요!",
                    titleDescriptionGap = 12.scaler,
                )

                // 헤더와 리스트 사이 44 간격은 스크롤되지 않는 고정 여백이라 LazyColumn 밖에 둠
                Spacer(modifier = Modifier.height(44.scaler))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = 20.scaler,
                        end = 20.scaler,
                        bottom = 20.scaler,
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.scaler),
                ) {
                    items(
                        count = remindLinkItems.itemCount,
                        key = remindLinkItems.itemKey { it.linkuId }
                    ) { index ->
                        remindLinkItems[index]?.let { link ->
                            LinkCardItem(
                                hasAiSummary = link.aiArticleExists,
                                linkTitle = link.title,
                                tags = listOfNotNull(
                                    link.categoryType?.tagName,
                                    link.emotionType?.tagName
                                ),
                                domainName = link.domain,
                                isExternalLink = false, // 보통 정해진 도메인일거라 false로 기본값을 둠
                                linkImageUrl = link.linkuImageUrl ?: "",
                                domainImageUrl = link.domainImageUrl ?: "",
                                onDeleteClick = { /* TODO: 삭제 API 연동 전까지는 no-op */ }
                            )
                        }
                    }

                    if (remindLinkItems.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.scaler),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.linkuColors.accentColor)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "큐레이션 리마인드 (#44-1)", showBackground = true)
@Composable
private fun CurationRemindScreenPreview() {
    val sampleLinks = List(6) { index ->
        LinkSimpleInfo(
            linkuId = index.toLong(),
            categoryId = CategoryType.PRODUCTIVITY_TOOL.id,
            memo = null,
            emotionId = EmotionType.CALM.value,
            title = "오픽 AL 따는 꿀팁 얻고 보러오세요",
            domain = "BLOG",
            domainImageUrl = null,
            linkuImageUrl = null,
            aiArticleExists = index % 2 == 0,
        )
    }
    val remindLinkItems = flowOf(PagingData.from(sampleLinks)).collectAsLazyPagingItems()

    LinkuPreview {
        CurationRemindScreenContent(
            remindLinkItems = remindLinkItems,
            onBack = {},
        )
    }
}

/**
 * 링크가 0개일 때 [CurationTopHeader]가 contentTopOffset = 406.scaler 위치까지
 * 내려간 채로 단독 노출되는지 확인하기 위한 프리뷰.
 *
 * [collectAsLazyPagingItems]는 LaunchedEffect로 비동기 수집되는데, 정적 프리뷰는
 * 첫 프레임만 캡처해서 그 수집이 끝나기 전(로딩 중) 상태로 고정돼버림 -> isEmpty가 항상
 * false로 잡혀서 빈 상태 분기를 볼 수 없었음. 그래서 Paging을 아예 거치지 않고
 * [CurationRemindEmptyHeader]를 직접 그려서 확인함.
 */
@Preview(name = "큐레이션 리마인드 - 빈 상태 (#44-1)", showBackground = true)
@Composable
private fun CurationRemindScreenEmptyPreview() {
    LinkuPreview {
        CurationGradientCircleBackground {
            Column(modifier = Modifier.fillMaxWidth()) {
                CurationRemindEmptyHeader(onBack = {})
            }
        }
    }
}
