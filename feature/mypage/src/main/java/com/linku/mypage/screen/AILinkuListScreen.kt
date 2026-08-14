package com.linku.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.linku.core.error.AppError
import com.linku.core.model.AiArticleLink
import com.linku.core.model.CategoryType
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.design.util.ReportScaffoldBackground
import com.linku.mypage.AILinkuListViewModel
import com.linku.mypage.R
import com.linku.mypage.component.AILinkuItem

/**
 * AI 요약 링크 화면의 ViewModel과 Compose Paging 수집을 연결합니다.
 *
 * @param navController 마이페이지 내부 뒤로가기를 처리할 내비게이션 컨트롤러
 * @param onNavigateToLinkDetail 선택한 사용자 저장 링크 ID를 앱 루트 상세 화면으로 전달하는 콜백
 * @param viewModel 카테고리 선택과 Paging Flow를 관리하는 ViewModel
 */
@Composable
fun AILinkuListRoute(
    navController: NavController,
    onNavigateToLinkDetail: (userLinkuId: Long) -> Unit,
    viewModel: AILinkuListViewModel = hiltViewModel(),
) {
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val links = viewModel.aiArticleLinks.collectAsLazyPagingItems()

    AILinkuListScreen(
        selectedCategory = selectedCategory,
        links = links,
        onBack = { navController.popBackStack() },
        onSelectAll = viewModel::selectAll,
        onSelectCategory = viewModel::selectCategory,
        onLinkClick = onNavigateToLinkDetail,
    )
}

/**
 * 카테고리 필터와 AI 요약 링크 Paging 상태를 표시합니다.
 *
 * @param selectedCategory 현재 선택한 카테고리이며 `null`이면 전체
 * @param links 현재 필터에서 수집한 Paging 항목
 * @param onBack 뒤로가기 요청
 * @param onSelectAll 전체 필터 선택 요청
 * @param onSelectCategory 카테고리 선택 요청
 * @param onLinkClick 선택한 사용자 저장 링크 ID를 상세 화면 이동 콜백에 전달
 */
@Composable
fun AILinkuListScreen(
    selectedCategory: CategoryType?,
    links: LazyPagingItems<AiArticleLink>,
    onBack: () -> Unit,
    onSelectAll: () -> Unit,
    onSelectCategory: (CategoryType) -> Unit,
    onLinkClick: (userLinkuId: Long) -> Unit,
) {
    var isCategoryMenuExpanded by rememberSaveable { mutableStateOf(false) }

    AILinkuListFrame(
        selectedCategory = selectedCategory,
        isCategoryMenuExpanded = isCategoryMenuExpanded,
        onBack = onBack,
        onSelectAll = onSelectAll,
        onToggleCategoryMenu = {
            isCategoryMenuExpanded = !isCategoryMenuExpanded
        },
        onDismissCategoryMenu = {
            isCategoryMenuExpanded = false
        },
        onSelectCategory = { category ->
            onSelectCategory(category)
            isCategoryMenuExpanded = false
        },
    ) {
        AILinkuPagingContent(
            links = links,
            onLinkClick = onLinkClick,
        )
    }
}

/**
 * 상단 바와 필터를 고정하고 목록 상태 콘텐츠만 교체하는 화면 프레임입니다.
 */
@Composable
private fun AILinkuListFrame(
    selectedCategory: CategoryType?,
    isCategoryMenuExpanded: Boolean,
    onBack: () -> Unit,
    onSelectAll: () -> Unit,
    onToggleCategoryMenu: () -> Unit,
    onDismissCategoryMenu: () -> Unit,
    onSelectCategory: (CategoryType) -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = MaterialTheme.linkuColors

    // 시스템 내비게이션 바 뒤로 비치는 상위 Scaffold 배경색을 이 화면의 실제 배경(gray[100])과
    // 맞춰서, 다른 화면(기본 흰색)과 전환될 때 배경색이 안 맞아 보이는 것을 막음.
    ReportScaffoldBackground(colors.gray[100])

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.gray[100])
            .padding(horizontal = 20.dp),
    ) {
        AILinkuTopBar(onBack = onBack)

        Spacer(modifier = Modifier.height(32.49.dp))

        AILinkuCategoryFilters(
            selectedCategory = selectedCategory,
            isCategoryMenuExpanded = isCategoryMenuExpanded,
            onSelectAll = onSelectAll,
            onToggleCategoryMenu = onToggleCategoryMenu,
            onDismissCategoryMenu = onDismissCategoryMenu,
            onSelectCategory = onSelectCategory,
        )

        Spacer(modifier = Modifier.height(15.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            content = content,
        )
    }
}

/** 마이페이지 AI 요약 링크 화면의 상단 탐색 영역입니다. */
@Composable
private fun AILinkuTopBar(onBack: () -> Unit) {
    val colors = MaterialTheme.linkuColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 58.dp)
            .height(24.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_back),
            contentDescription = stringResource(R.string.ai_linku_back_content_description),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(11.dp)
                .noRippleClickable(onClick = onBack),
        )

        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_sparkle),
                contentDescription = null,
                modifier = Modifier.size(width = 16.42.dp, height = 17.51.dp),
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(R.string.ai_linku_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = colors.black,
            )
        }
    }
}

/** 전체 필터와 16개 카테고리 드롭다운을 왼쪽부터 8dp 간격으로 배치합니다. */
@Composable
private fun AILinkuCategoryFilters(
    selectedCategory: CategoryType?,
    isCategoryMenuExpanded: Boolean,
    onSelectAll: () -> Unit,
    onToggleCategoryMenu: () -> Unit,
    onDismissCategoryMenu: () -> Unit,
    onSelectCategory: (CategoryType) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.Top,
    ) {
        AILinkuFilterChip(
            text = stringResource(R.string.ai_linku_filter_all),
            selected = selectedCategory == null,
            onClick = onSelectAll,
        )

        AILinkuCategoryDropdownChip(
            text = selectedCategory?.tagName
                ?: stringResource(R.string.ai_linku_filter_category),
            expanded = isCategoryMenuExpanded,
            onClick = onToggleCategoryMenu,
            onDismiss = onDismissCategoryMenu,
            onCategorySelected = onSelectCategory,
        )
    }
}

/** 선택 여부에 따라 전경과 배경색을 바꾸는 필터 칩입니다. */
@Composable
private fun AILinkuFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors

    Box(
        modifier = Modifier
            .background(
                color = if (selected) colors.black else colors.white,
                shape = RoundedCornerShape(10.dp),
            )
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) colors.white else colors.gray[800],
            maxLines = 1,
        )
    }
}

/**
 * 카테고리 칩과 180dp × 228dp 크기의 스크롤 가능한 메뉴를 표시합니다.
 *
 * 메뉴는 칩 시작점에 정렬하고 칩 아래로 11dp 이동합니다.
 * 메뉴의 상하좌우 18dp 여백은 고정하고, 여백 안쪽의 카테고리 목록만 스크롤합니다.
 * 그림자는 디자인 명세에 따라 X 0dp, Y 3dp, blur 15dp, spread 0dp로 표시합니다.
 */
@Composable
private fun AILinkuCategoryDropdownChip(
    text: String,
    expanded: Boolean,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    onCategorySelected: (CategoryType) -> Unit,
) {
    val colors = MaterialTheme.linkuColors
    val menuScrollState = rememberScrollState()
    val density = LocalDensity.current
    val menuShape = RoundedCornerShape(18.dp)
    val horizontalShadowPadding = 15.dp
    val topShadowPadding = 12.dp
    val bottomShadowPadding = 18.dp
    val popupPositionProvider = remember(density) {
        AILinkuDropdownPositionProvider(
            contentOffset = DpOffset(x = 0.dp, y = 11.dp),
            density = density,
            horizontalShadowPadding = horizontalShadowPadding,
            topShadowPadding = topShadowPadding,
            bottomShadowPadding = bottomShadowPadding,
        )
    }

    Box(
        modifier = Modifier.wrapContentWidth(Alignment.Start),
        contentAlignment = Alignment.TopStart,
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth(Alignment.Start)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.white)
                .border(
                    width = 1.dp,
                    color = colors.gray[200],
                    shape = RoundedCornerShape(10.dp),
                )
                .noRippleClickable(onClick = onClick)
                .padding(horizontal = 15.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colors.gray[800],
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 140.dp),
            )

            Spacer(modifier = Modifier.width(10.dp))

            Image(
                painter = painterResource(R.drawable.ic_arrow_down_gray500),
                contentDescription = stringResource(
                    R.string.ai_linku_category_menu_content_description,
                ),
                modifier = Modifier.height(12.dp),
            )
        }

        if (expanded) {
            Popup(
                popupPositionProvider = popupPositionProvider,
                onDismissRequest = onDismiss,
                properties = PopupProperties(focusable = true),
            ) {
                // 그림자 여백을 Popup 레이아웃에 포함해 메뉴의 바깥쪽 blur가 잘리지 않도록 합니다.
                Box(
                    modifier = Modifier.padding(
                        start = horizontalShadowPadding,
                        top = topShadowPadding,
                        end = horizontalShadowPadding,
                        bottom = bottomShadowPadding,
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .width(180.dp)
                            .height(228.dp)
                            .dropShadow(
                                shape = menuShape,
                                shadow = Shadow(
                                    radius = 15.dp,
                                    spread = 0.dp,
                                    color = colors.shadowColor.copy(alpha = 0.3f),
                                    offset = DpOffset(x = 0.dp, y = 3.dp),
                                ),
                             )
                             .clip(menuShape)
                             .background(colors.white)
                            .padding(18.dp)
                            .verticalScroll(menuScrollState),
                    ) {
                        CategoryType.entries.forEach { category ->
                            AILinkuCategoryMenuItem(
                                category = category,
                                onClick = { onCategorySelected(category) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 그림자 여백을 제외한 실제 메뉴를 카테고리 칩 시작점과 칩 하단에 맞춰 배치합니다.
 *
 * 화면 아래 공간이 부족하면 메뉴를 칩 위로 배치하고, 어느 방향에도 충분한 공간이 없으면
 * Popup 전체가 화면 안에 남도록 위치를 제한합니다.
 */
private class AILinkuDropdownPositionProvider(
    private val contentOffset: DpOffset,
    private val density: Density,
    private val horizontalShadowPadding: Dp,
    private val topShadowPadding: Dp,
    private val bottomShadowPadding: Dp,
) : PopupPositionProvider {

    /** 앵커와 Popup 크기를 기준으로 그림자 여백까지 포함한 최종 좌표를 계산합니다. */
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val xOffset = with(density) { contentOffset.x.roundToPx() }
        val yOffset = with(density) { contentOffset.y.roundToPx() }
        val horizontalShadowPaddingPx = with(density) {
            horizontalShadowPadding.roundToPx()
        }
        val topShadowPaddingPx = with(density) { topShadowPadding.roundToPx() }
        val bottomShadowPaddingPx = with(density) { bottomShadowPadding.roundToPx() }
        val menuWidth = popupContentSize.width - horizontalShadowPaddingPx * 2
        val menuHeight = popupContentSize.height - topShadowPaddingPx - bottomShadowPaddingPx

        val menuLeft = when (layoutDirection) {
            LayoutDirection.Ltr -> anchorBounds.left + xOffset
            LayoutDirection.Rtl -> anchorBounds.right - menuWidth - xOffset
        }
        val maxPopupX = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
        val popupX = (menuLeft - horizontalShadowPaddingPx).coerceIn(0, maxPopupX)

        val belowPopupY = anchorBounds.bottom + yOffset - topShadowPaddingPx
        val abovePopupY = anchorBounds.top - yOffset - menuHeight - topShadowPaddingPx
        val maxPopupY = (windowSize.height - popupContentSize.height).coerceAtLeast(0)
        val popupY = when {
            belowPopupY + popupContentSize.height <= windowSize.height -> belowPopupY
            abovePopupY >= 0 -> abovePopupY
            else -> belowPopupY.coerceIn(0, maxPopupY)
        }

        return IntOffset(x = popupX, y = popupY)
    }
}

/** 카테고리별 디자인 색상과 이름을 표시하는 드롭다운 항목입니다. */
@Composable
private fun AILinkuCategoryMenuItem(
    category: CategoryType,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors
    val categoryColor = CategoryColorStyle.categoryStyleList
        .getOrNull(category.ordinal)
        ?.color4
        ?: colors.gray[300]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(categoryColor),
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = category.tagName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = colors.gray[800],
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

/** refresh 및 append 상태를 구분하고 링크 클릭을 목록 콘텐츠에 전달합니다. */
@Composable
private fun BoxScope.AILinkuPagingContent(
    links: LazyPagingItems<AiArticleLink>,
    onLinkClick: (userLinkuId: Long) -> Unit,
) {
    val refreshState = links.loadState.refresh

    when {
        refreshState is LoadState.Loading -> {
            AILinkuLoadingContent(modifier = Modifier.align(Alignment.Center))
        }

        refreshState is LoadState.Error && links.itemCount == 0 -> {
            val fallbackMessage = stringResource(R.string.ai_linku_load_error)
            val message = (refreshState.error as? AppError)
                ?.displayMessage
                ?.takeIf { it.isNotBlank() }
                ?: fallbackMessage

            AILinkuErrorContent(
                message = message,
                onRetry = links::retry,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        refreshState is LoadState.NotLoading && links.itemCount == 0 -> {
            AILinkuEmptyContent(modifier = Modifier.align(Alignment.Center))
        }

        else -> {
            AILinkuPagingList(
                links = links,
                onLinkClick = onLinkClick,
            )
        }
    }
}

/** 로드된 링크와 다음 페이지 상태를 표시하고 카드 클릭 시 링크 ID를 전달합니다. */
@Composable
private fun AILinkuPagingList(
    links: LazyPagingItems<AiArticleLink>,
    onLinkClick: (userLinkuId: Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(
            count = links.itemCount,
            key = { index -> aiLinkuItemKey(index, links.peek(index)?.userLinkuId) },
        ) { index ->
            links[index]?.let { link ->
                AILinkuItem(
                    link = link,
                    onClick = onLinkClick,
                )
            }
        }

        when (val appendState = links.loadState.append) {
            is LoadState.Loading -> {
                item(key = "ai_linku_append_loading") {
                    AILinkuAppendLoadingFooter()
                }
            }

            is LoadState.Error -> {
                item(key = "ai_linku_append_error") {
                    AILinkuPagingErrorFooter(
                        message = stringResource(R.string.ai_linku_append_error),
                        onRetry = links::retry,
                    )
                }
            }

            is LoadState.NotLoading -> {
                val refreshState = links.loadState.refresh
                if (refreshState is LoadState.Error) {
                    item(key = "ai_linku_refresh_error") {
                        AILinkuPagingErrorFooter(
                            message = stringResource(R.string.ai_linku_load_error),
                            onRetry = links::retry,
                        )
                    }
                }
            }
        }
    }
}

/** ID가 누락된 과도기 응답도 Paging 슬롯별로 충돌하지 않도록 안정적인 키를 만듭니다. */
internal fun aiLinkuItemKey(index: Int, userLinkuId: Long?): String =
    userLinkuId
        ?.takeIf { it > 0L }
        ?.let { "ai-linku-user-$it" }
        ?: "ai-linku-index-$index"

/** 최초 페이지를 불러오는 동안 표시하는 상태입니다. */
@Composable
private fun AILinkuLoadingContent(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = colors.accentColor)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.ai_linku_loading),
            fontSize = 14.sp,
            color = colors.gray[600],
        )
    }
}

/** AI 요약 링크가 하나도 없을 때 기존 빈 상태 디자인을 표시합니다. */
@Composable
private fun AILinkuEmptyContent(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.img_empty_ailinku),
            contentDescription = null,
            modifier = Modifier.width(85.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.ai_linku_empty_title),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = colors.gray[800],
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = stringResource(R.string.ai_linku_empty_description),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = colors.gray[600],
            textAlign = TextAlign.Center,
        )
    }
}

/** 최초 페이지 조회 실패 메시지와 재시도 동작을 표시합니다. */
@Composable
private fun AILinkuErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = colors.gray[700],
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        AILinkuRetryButton(onRetry = onRetry)
    }
}

/** 다음 페이지를 불러오는 동안 목록 하단에 표시하는 로딩 상태입니다. */
@Composable
private fun AILinkuAppendLoadingFooter() {
    val colors = MaterialTheme.linkuColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            color = colors.accentColor,
            strokeWidth = 2.dp,
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.ai_linku_append_loading),
            fontSize = 13.sp,
            color = colors.gray[600],
        )
    }
}

/** 기존 목록을 유지할 수 있는 Paging 실패 시 하단 메시지와 재시도를 제공합니다. */
@Composable
private fun AILinkuPagingErrorFooter(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            fontSize = 13.sp,
            color = MaterialTheme.linkuColors.gray[600],
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        AILinkuRetryButton(onRetry = onRetry)
    }
}

/** Paging 실패 상태에서 공통으로 사용하는 재시도 버튼입니다. */
@Composable
private fun AILinkuRetryButton(onRetry: () -> Unit) {
    val colors = MaterialTheme.linkuColors

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.black)
            .noRippleClickable(onClick = onRetry)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.ai_linku_retry),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = colors.white,
        )
    }
}

/** 전체 필터의 빈 상태 레이아웃을 확인하는 프리뷰입니다. */
@Preview(showBackground = true)
@Composable
private fun PreviewAILinkuEmptyScreen() {
    ThemeProvider {
        AILinkuListFrame(
            selectedCategory = null,
            isCategoryMenuExpanded = false,
            onBack = {},
            onSelectAll = {},
            onToggleCategoryMenu = {},
            onDismissCategoryMenu = {},
            onSelectCategory = {},
        ) {
            AILinkuEmptyContent(modifier = Modifier.align(Alignment.Center))
        }
    }
}

/** 특정 카테고리의 최초 로딩 상태를 확인하는 프리뷰입니다. */
@Preview(showBackground = true)
@Composable
private fun PreviewAILinkuLoadingScreen() {
    ThemeProvider {
        AILinkuListFrame(
            selectedCategory = CategoryType.IT_DEV,
            isCategoryMenuExpanded = false,
            onBack = {},
            onSelectAll = {},
            onToggleCategoryMenu = {},
            onDismissCategoryMenu = {},
            onSelectCategory = {},
        ) {
            AILinkuLoadingContent(modifier = Modifier.align(Alignment.Center))
        }
    }
}

/** 다음 페이지 오류와 재시도 UI를 확인하는 프리뷰입니다. */
@Preview(showBackground = true)
@Composable
private fun PreviewAILinkuAppendError() {
    ThemeProvider {
        AILinkuPagingErrorFooter(
            message = stringResource(R.string.ai_linku_append_error),
            onRetry = {},
        )
    }
}
