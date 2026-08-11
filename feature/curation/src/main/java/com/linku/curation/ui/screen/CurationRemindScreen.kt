package com.linku.curation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.core.model.curation.UnreadLink
import com.linku.curation.ui.effect.skeleton.CurationTopHeaderSkeleton
import com.linku.curation.ui.effect.skeleton.LinkCardItemSkeleton
import com.linku.curation.ui.header.CurationTopHeader
import com.linku.curation.ui.util.CurationErrorLayout
import com.linku.curation.ui.util.CurationGradientCircleBackground
import com.linku.curation.viewModel.CurationRemindViewModel
import com.linku.curation.viewModel.intent.CurationRemindIntent
import com.linku.curation.viewModel.sideeffect.CurationRemindSideEffect
import com.linku.design.component.LinkCardItem
import com.linku.design.component.TimedCustomToastMessage
import com.linku.design.theme.LinkuPreview
import com.linku.design.util.scaler
import java.time.LocalDate

@Composable
fun CurationRemindScreen(
    onBack: () -> Unit,
    onNavigateToLinkDetail: (Long) -> Unit = {},
    viewModel: CurationRemindViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var toastMessage by remember { mutableStateOf("") }
    var isToastVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is CurationRemindSideEffect.ShowToast -> {
                    toastMessage = effect.message
                    isToastVisible = true
                }
                is CurationRemindSideEffect.NavigateToLinkDetail ->
                    onNavigateToLinkDetail(effect.linkId)
            }
        }
    }

    BackHandler { onBack() }

    Box {
        CurationRemindScreenContent(
            links = state.links,
            isLoading = state.isLoading,
            isError = state.isError,
            onBack = onBack,
            onLinkClick = { link ->
                val linkId = link.userLinkuId ?: return@CurationRemindScreenContent
                viewModel.handleIntent(CurationRemindIntent.ClickLink(linkId))
            },
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

/**
 * "n년 n월"을 오늘로부터 정확히 한 달 전 기준으로 반환. API 없이 고정 문구라 로컬에서 계산.
 */
private fun remindMonthText(): String {
    val lastMonth = LocalDate.now().minusMonths(1)
    return "${lastMonth.year}년 ${lastMonth.monthValue}월"
}

/**
 * 링크가 0개일 때 노출되는 헤더. 리스트 상태와 무관하게 별도 컴포저블로 분리해서,
 * 프리뷰에서 이 상태를 바로 확인할 수 있게 함.
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
    links: List<UnreadLink>,
    isLoading: Boolean = false,
    isError: Boolean = false,
    onBack: () -> Unit,
    onLinkClick: (UnreadLink) -> Unit = {},
) {
    val remindMonth = remember { remindMonthText() }
    val isEmpty = links.isEmpty()

    CurationGradientCircleBackground {

        // 에러나면 배경만 띄우고 토스트 출력 - 피그마 요구사항
        if (isError) return@CurationGradientCircleBackground

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
                CurationRemindEmptyHeader(onBack = onBack)
            } else {
                val listState = rememberLazyListState()
                // 스크롤을 시작하면 헤더를 위로 붙여서(105 -> 59) 리스트가 더 많이 보이게 함
                val isScrolled by remember {
                    derivedStateOf {
                        listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
                    }
                }
                val headerContentTopOffset by animateDpAsState(
                    targetValue = if (isScrolled) 59.scaler else 105.scaler,
                    label = "curationRemindHeaderOffset"
                )

                CurationTopHeader(
                    onBackClick = onBack,
                    contentTopOffset = headerContentTopOffset,
                    title = "$remindMonth,\n저장만 하고 열어보지 않은 링크예요",
                    // TODO: API 연동 후엔 서버가 내려주는 "전체 저장 링크 수" 필드로 교체해야 함
                    description = "총 ${links.size}개의 링크가 쌓여있네요!",
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
                    items(
                        count = links.size,
                        key = { index -> links[index].userLinkuId ?: index.toLong() }
                    ) { index ->
                        val link = links[index]
                        LinkCardItem(
                            hasAiSummary = false,
                            linkTitle = link.title,
                            tags = link.categories.take(2),
                            domainName = link.domain,
                            isExternalLink = false,
                            linkImageUrl = link.linkuImageUrl ?: "",
                            domainImageUrl = link.domainImageUrl,
                            onCardClick = { onLinkClick(link) },
                            onDeleteClick = { /* TODO: 삭제 API 연동 전까지는 no-op */ }
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "큐레이션 리마인드 - 로딩 중 (#44-1)", showBackground = true)
@Composable
private fun CurationRemindScreenLoadingPreview() {
    LinkuPreview {
        CurationRemindScreenContent(
            links = emptyList(),
            isLoading = true,
            onBack = {},
        )
    }
}

@Preview(name = "큐레이션 리마인드 (#44-1)", showBackground = true)
@Composable
private fun CurationRemindScreenPreview() {
    val sampleLinks = List(6) { index ->
        UnreadLink(
            userLinkuId = index.toLong(),
            title = "오픽 AL 따는 꿀팁 얻고 보러오세요",
            domain = "BLOG",
            categories = listOf("생산성·툴", "평온"),
            domainImageUrl = "",
            linkuImageUrl = null,
        )
    }

    LinkuPreview {
        CurationRemindScreenContent(
            links = sampleLinks,
            onBack = {},
        )
    }
}

/**
 * 링크가 0개일 때 [CurationTopHeader]가 contentTopOffset = 406.scaler 위치까지
 * 내려간 채로 단독 노출되는지 확인하기 위한 프리뷰.
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
