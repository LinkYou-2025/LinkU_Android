package com.linku.mypage.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.linku.core.model.AiArticleLink
import com.linku.design.component.LinkCardItem
import com.linku.design.theme.ThemeProvider

/**
 * AI 요약이 생성된 저장 링크를 공용 링크 카드 디자인으로 표시합니다.
 *
 * AI 요약 목록의 특성에 맞춰 AI 북마크를 항상 표시하고 외부 링크 아이콘은 숨깁니다.
 * [AiArticleLink.userLinkuId]가 양수인 항목만 상세 이동과 더보기 및 삭제 동작을 제공합니다.
 *
 * @param link 표시할 AI 요약 링크
 * @param onClick 카드 본문을 눌렀을 때 유효한 사용자 저장 링크 ID를 전달하는 콜백
 * @param isDeleteMenuVisible 이 항목의 삭제 메뉴를 현재 표시할지 여부
 * @param onMoreClick 더보기 버튼을 눌렀을 때 유효한 사용자 저장 링크 ID를 전달하는 콜백
 * @param onDeleteClick 삭제 메뉴를 눌렀을 때 유효한 사용자 저장 링크 ID를 전달하는 콜백
 * @param modifier 카드 외부 레이아웃에 적용할 modifier
 * @param isInteractionEnabled 카드 상세·더보기·삭제 상호작용을 허용할지 여부
 */
@Composable
fun AILinkuItem(
    link: AiArticleLink,
    onClick: (Long) -> Unit,
    isDeleteMenuVisible: Boolean,
    onMoreClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    isInteractionEnabled: Boolean = true,
) {
    val onCardClickAction = if (isInteractionEnabled) {
        aiLinkuClickAction(link.userLinkuId, onClick)
    } else {
        null
    }
    val onMoreClickAction = if (isInteractionEnabled) {
        aiLinkuClickAction(link.userLinkuId, onMoreClick)
    } else {
        null
    }
    val onDeleteClickAction = if (isInteractionEnabled) {
        aiLinkuClickAction(link.userLinkuId, onDeleteClick)
    } else {
        null
    }
    val hasEnabledUserLinkuId = onCardClickAction != null

    LinkCardItem(
        hasAiSummary = true,
        linkTitle = link.title,
        modifier = modifier,
        tags = listOfNotNull(
            link.displayCategoryName.takeIf { categoryName -> categoryName.isNotBlank() },
            link.emotionType?.tagName?.takeIf { emotionName -> emotionName.isNotBlank() },
        ),
        domainName = link.domain,
        isExternalLink = false,
        linkImageUrl = link.linkuImageUrl.orEmpty(),
        domainImageUrl = link.domainImageUrl.orEmpty(),
        isMoreVisible = hasEnabledUserLinkuId,
        isDeleteMenuVisible = hasEnabledUserLinkuId && isDeleteMenuVisible,
        onMoreClick = onMoreClickAction ?: {},
        onCardClick = onCardClickAction,
        onDeleteClick = onDeleteClickAction ?: {},
    )
}

/**
 * 사용자 저장 링크 ID가 유효할 때만 해당 ID를 전달하는 클릭 동작을 만듭니다.
 *
 * @param userLinkuId 서버가 반환한 사용자 저장 링크 ID
 * @param onClick 유효한 ID를 전달받을 원본 콜백
 * @return 양수 ID를 전달하는 클릭 동작. ID가 0 이하이면 `null`
 */
internal fun aiLinkuClickAction(
    userLinkuId: Long,
    onClick: (Long) -> Unit,
): (() -> Unit)? = userLinkuId
    .takeIf { id -> id > 0L }
    ?.let { validUserLinkuId ->
        { onClick(validUserLinkuId) }
    }

/** 공용 링크 카드와 동일한 AI 요약 링크 카드 및 삭제 메뉴를 확인합니다. */
@Preview(showBackground = false)
@Composable
private fun PreviewAILinkuItem() {
    ThemeProvider {
        AILinkuItem(
            link = AiArticleLink(
                userLinkuId = 1L,
                linku = "https://example.com/article",
                emotionId = 2L,
                domain = "BLOG",
                domainImageUrl = null,
                title = "요즘 대학생들이 진짜 쓰는 앱 TOP10",
                linkuImageUrl = null,
                categoryId = 8L,
                categoryName = "생산성·툴",
            ),
            onClick = {},
            isDeleteMenuVisible = true,
            onMoreClick = {},
            onDeleteClick = {},
        )
    }
}
