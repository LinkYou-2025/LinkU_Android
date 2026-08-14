package com.linku.file.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linku.core.model.LinkItemInfo
import com.linku.design.theme.LinkuPreview
import com.linku.file.ui.item.LinkItemLayout

private const val SHARED_LINK_INTER_LAYER_PADDING = 18.51
private const val SHARED_LINK_ITEM_RATIO = 10f / 174f

/**
 * 공유폴더 상세의 링크를 읽기 전용으로 표시합니다.
 *
 * 링크 추가 셀과 편집·삭제 long press는 제공하지 않습니다. 일반 tap만 기존 루트 링크 상세
 * 콜백으로 [LinkItemInfo.userLinkuId]를 전달합니다.
 *
 * @param links 현재 공유폴더에 속한 링크 목록
 * @param onLinkClick 루트 링크 상세 이동을 요청할 사용자 링크 ID 콜백
 */
@Composable
internal fun SharedLinksGrid(
    links: List<LinkItemInfo>,
    onLinkClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        top = 20.dp,
        start = 20.dp,
        end = 20.dp,
        bottom = 60.dp,
    ),
) {
    val layoutDirection = LocalLayoutDirection.current

    BoxWithConstraints(modifier = modifier) {
        val horizontalPadding =
            contentPadding.calculateStartPadding(layoutDirection) +
                contentPadding.calculateEndPadding(layoutDirection)
        val availableWidth = maxWidth - horizontalPadding
        val horizontalSpacing = availableWidth * SHARED_LINK_ITEM_RATIO

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(SHARED_LINK_INTER_LAYER_PADDING.dp),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        ) {
            items(
                items = links,
                key = { link -> link.userLinkuId },
            ) { link ->
                LinkItemLayout(
                    link = link,
                    onClick = { selectedLink ->
                        selectedLink?.let { onLinkClick(it.userLinkuId) }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SharedLinksGridPreview() {
    LinkuPreview {
        SharedLinksGrid(
            links = listOf(
                LinkItemInfo(
                    userLinkuId = 1L,
                    parentFolderId = 10L,
                    title = "읽기 전용 공유 링크",
                    url = "https://example.com",
                    tags = listOf("공유"),
                    linkuImageUrl = null,
                    createdAt = null,
                ),
            ),
            onLinkClick = {},
        )
    }
}
