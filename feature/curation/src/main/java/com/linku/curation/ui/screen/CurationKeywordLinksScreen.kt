package com.linku.curation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.linku.core.model.CategoryType
import com.linku.core.model.EmotionType
import com.linku.core.model.LinkSimpleInfo
import com.linku.curation.ui.effect.skeleton.CurationTopHeaderSkeleton
import com.linku.curation.ui.effect.skeleton.LinkCardItemSkeleton
import com.linku.curation.ui.header.CurationTopHeader
import com.linku.curation.ui.util.CurationGradientCircleBackground
import com.linku.curation.viewModel.CurationViewModel
import com.linku.design.component.LinkCardItem
import com.linku.design.theme.LinkuPreview
import com.linku.design.util.scaler

/**
 * 큐레이션 워드클라우드(#43-1)에서 키워드 하나를 클릭했을 때 보여주는 관련 링크 목록(#43-2) 화면.
 * [CurationRemindScreen]과 UI 구조는 동일하지만, 목록이 페이징 없이 한 번에 내려온다는 점만 다르다.
 *
 * @param keyword 클릭된 키워드("#" 없이)
 * @param nickname 설명 문구에 쓰일 사용자 닉네임
 * @param onBack 백버튼 클릭 콜백
 */
@Composable
internal fun CurationKeywordLinksScreen(
    keyword: String,
    nickname: String,
    viewModel: CurationViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {
    BackHandler { onBack() }

    // TODO: viewModel에서 keyword 기준 링크 목록 API 연동
    val links: List<LinkSimpleInfo> = emptyList()
    val isLoading = false

    CurationKeywordLinksScreenContent(
        keyword = keyword,
        nickname = nickname,
        links = links,
        isLoading = isLoading,
        onBack = onBack,
    )
}

/**
 * 해당 키워드로 저장된 링크가 없을 때 노출되는 헤더.
 */
@Composable
private fun CurationKeywordLinksEmptyHeader(keyword: String, onBack: () -> Unit) {
    CurationTopHeader(
        onBackClick = onBack,
        contentTopOffset = 406.scaler,
        title = "\"$keyword\"\n관련된 링크가 없어요",
        description = "이 키워드로 링크를 더 저장해보세요!",
        titleDescriptionGap = 12.scaler,
    )
}

@Composable
private fun CurationKeywordLinksScreenContent(
    keyword: String,
    nickname: String,
    links: List<LinkSimpleInfo>,
    isLoading: Boolean = false,
    onBack: () -> Unit,
) {
    val isEmpty = links.isEmpty()

    CurationGradientCircleBackground {
        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth()) {
                CurationTopHeaderSkeleton(
                    onBack = onBack,
                    contentTopOffset = 105.scaler,
                )

                Spacer(modifier = Modifier.height(44.scaler))

                Column(
                    modifier = Modifier.padding(horizontal = 20.scaler),
                    verticalArrangement = Arrangement.spacedBy(10.scaler)
                ) {
                    repeat(4) { LinkCardItemSkeleton() }
                }
            }
            return@CurationGradientCircleBackground
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            if (isEmpty) {
                CurationKeywordLinksEmptyHeader(keyword = keyword, onBack = onBack)
            } else {
                val listState = rememberLazyListState()
                // 스크롤을 시작하면 헤더를 위로 붙여서(105 -> 59) 리스트가 더 많이 보이게 함 => pm이 감명 받은 포인트..
                val isScrolled by remember {
                    derivedStateOf {
                        listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
                    }
                }
                val headerContentTopOffset by animateDpAsState(
                    targetValue = if (isScrolled) 59.scaler else 105.scaler,
                    label = "curationKeywordLinksHeaderOffset"
                )

                CurationTopHeader(
                    onBackClick = onBack,
                    contentTopOffset = headerContentTopOffset,
                    title = "\"$keyword\"\n관련된 링크예요",
                    description = "${nickname}님이 저장한 링크를 모아봤어요!",
                    titleDescriptionGap = 12.scaler,
                )

                // 헤더와 리스트 사이 44 간격은 스크롤되지 않는 고정 여백이라 LazyColumn 밖에 둠
                Spacer(modifier = Modifier.height(44.scaler))

                LazyColumn(
                    state = listState,
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
                    items(items = links, key = { it.userLinkuId }) { link ->
                        LinkCardItem(
                            hasAiSummary = link.aiArticleExists,
                            linkTitle = link.title,
                            tags = listOfNotNull(
                                link.categoryType?.tagName,
                                link.emotionType?.tagName
                            ),
                            domainName = link.domain,
                            isExternalLink = false, // 보통 정해진 도메인일거라 false로 기본값을 둠
                            isMoreVisible = false,
                            linkImageUrl = link.linkuImageUrl ?: "",
                            domainImageUrl = link.domainImageUrl ?: "",
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "큐레이션 키워드 관련 링크 - 로딩 중 (#43-2)", showBackground = true)
@Composable
private fun CurationKeywordLinksScreenLoadingPreview() {
    LinkuPreview {
        CurationKeywordLinksScreenContent(
            keyword = "영어공부",
            nickname = "세나",
            links = emptyList(),
            isLoading = true,
            onBack = {},
        )
    }
}

@Preview(name = "큐레이션 키워드 관련 링크 (#43-2)", showBackground = true)
@Composable
private fun CurationKeywordLinksScreenPreview() {
    val sampleLinks = List(6) { index ->
        LinkSimpleInfo(
            userLinkuId = index.toLong() + 1L,
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

    LinkuPreview {
        CurationKeywordLinksScreenContent(
            keyword = "영어공부",
            nickname = "세나",
            links = sampleLinks,
            onBack = {},
        )
    }
}

@Preview(name = "큐레이션 키워드 관련 링크 - 빈 상태 (#43-2)", showBackground = true)
@Composable
private fun CurationKeywordLinksScreenEmptyPreview() {
    LinkuPreview {
        CurationKeywordLinksScreenContent(
            keyword = "영어공부",
            nickname = "세나",
            links = emptyList(),
            onBack = {},
        )
    }
}
