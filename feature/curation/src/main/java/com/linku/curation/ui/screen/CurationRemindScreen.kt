package com.linku.curation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
private fun CurationRemindScreenContent(
    remindLinkItems: LazyPagingItems<LinkSimpleInfo>,
    onBack: () -> Unit,
) {
    val isEmpty = remindLinkItems.loadState.refresh is LoadState.NotLoading &&
            remindLinkItems.itemCount == 0

    CurationGradientCircleBackground {
        Column(modifier = Modifier.fillMaxWidth()) {

            // 반복이 많기는 한데, 가독성을 위해서...
            if (isEmpty) {
                CurationTopHeader(
                    onBackClick = onBack,
                    contentTopOffset = 406.scaler,
                    title = "지난 달 저장한 링크를\n잘 보고 있어요",
                    description = "저장해둔 링크들을 꾸준히 소비하고 있네요!",
                    titleDescriptionGap = 12.scaler,
                )
            } else {
                CurationTopHeader(
                    onBackClick = onBack,
                    contentTopOffset = 105.scaler,
                    title = "${remindMonthText()},\n저장만 하고 열어보지 않은 링크예요",
                    description = "총 ${remindLinkItems.itemCount}개의 링크가 쌓여있네요!",
                    titleDescriptionGap = 12.scaler,
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        top = 44.scaler,
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

@Preview(name = "큐레이션 리마인드 - 빈 상태 (#44-1)", showBackground = true)
@Composable
private fun CurationRemindScreenEmptyPreview() {
    val remindLinkItems = flowOf(PagingData.empty<LinkSimpleInfo>()).collectAsLazyPagingItems()

    LinkuPreview {
        CurationRemindScreenContent(
            remindLinkItems = remindLinkItems,
            onBack = {},
        )
    }
}
