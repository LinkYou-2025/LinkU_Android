package com.linku.file.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.SharedFolderInfo
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.file.ui.item.items.EmptyFolderItemLayout

private const val INTER_LAYER_PADDING = 18.51
private const val ITEM_RATIO = 10f / 174f
private const val SECTION_TITLE_TOP_PADDING = 21.49
private const val SECTION_TITLE_BOTTOM_PADDING = 1.49

/**
 * 공유폴더 탐색의 그룹 화면을 표시합니다.
 *
 * 내가 소유한 공유폴더의 유무와 관계없이 `나의 폴더` 그룹 카드를 단일 진입점으로
 * 표시합니다. 공유받은 사용자 그룹이 비어 있으면 loading이나 error 대신 사용할 수 없도록,
 * 호출자가 성공한 빈 결과를 전달한 경우에만 이 컴포넌트 내부의 [SharedFolderEmptyState]를
 * 표시합니다.
 *
 * @param receivedGroups 소유자별로 묶인 공유받은 폴더 그룹
 * @param ownedGroupLabel 소유 공유폴더 그룹 카드에 표시할 문구
 * @param receivedSectionTitle 공유받은 그룹 섹션 제목
 * @param receivedCountText 공유받은 그룹 개수를 표시할 문구
 * @param receivedGroupLabel 각 소유자 그룹에 표시할 문구를 만드는 함수
 * @param emptyTitle 공유받은 그룹이 없을 때 표시할 제목
 * @param emptySubtitle 공유받은 그룹이 없을 때 표시할 보조 문구
 * @param onOwnedGroupClick 소유 공유폴더 목록 진입 요청
 * @param onReceivedGroupClick 선택한 소유자의 공유폴더 목록 진입 요청
 */
@Composable
internal fun SharedUsersGrid(
    receivedGroups: List<SharedFolderInfo>,
    ownedGroupLabel: String,
    receivedSectionTitle: String,
    receivedCountText: String,
    receivedGroupLabel: (SharedFolderInfo) -> String,
    emptyTitle: String,
    emptySubtitle: String,
    onOwnedGroupClick: () -> Unit,
    onReceivedGroupClick: (SharedFolderInfo) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        top = 20.dp,
        start = 20.dp,
        end = 20.dp,
        bottom = 60.dp,
    ),
) {
    val colors = MaterialTheme.linkuColors
    val layoutDirection = LocalLayoutDirection.current

    BoxWithConstraints(modifier = modifier) {
        val horizontalPadding =
            contentPadding.calculateStartPadding(layoutDirection) +
                contentPadding.calculateEndPadding(layoutDirection)
        val availableWidth = maxWidth - horizontalPadding
        val horizontalSpacing = availableWidth * ITEM_RATIO

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(INTER_LAYER_PADDING.dp),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        ) {
            item(key = "owned_shared_folders") {
                EmptyFolderItemLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable(onClick = onOwnedGroupClick),
                    folderName = ownedGroupLabel,
                )
            }

            item(
                key = "received_shared_folders_header",
                span = { GridItemSpan(maxLineSpan) },
            ) {
                Row(
                    modifier = Modifier.padding(
                        top = SECTION_TITLE_TOP_PADDING.dp,
                        bottom = SECTION_TITLE_BOTTOM_PADDING.dp,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = receivedSectionTitle,
                        fontSize = 20.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = receivedCountText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.gray[600],
                    )
                }
            }

            if (receivedGroups.isEmpty()) {
                item(
                    key = "received_shared_folders_empty",
                    span = { GridItemSpan(maxLineSpan) },
                ) {
                    SharedFolderEmptyState(
                        title = emptyTitle,
                        subtitle = emptySubtitle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                    )
                }
            } else {
                items(
                    items = receivedGroups,
                    key = { group -> group.userId },
                ) { group ->
                    EmptyFolderItemLayout(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable {
                                onReceivedGroupClick(group)
                            },
                        folderName = receivedGroupLabel(group),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SharedUsersGridPreview() {
    LinkuPreview {
        SharedUsersGrid(
            receivedGroups = listOf(
                SharedFolderInfo(1L, "User 1", emptyList()),
                SharedFolderInfo(2L, "User 2", emptyList()),
            ),
            ownedGroupLabel = "나의 폴더",
            receivedSectionTitle = "공유받은 폴더",
            receivedCountText = "2",
            receivedGroupLabel = { group -> "${group.nickname}의 폴더" },
            emptyTitle = "아직 공유받은 폴더가 없어요!",
            emptySubtitle = "공유 링크로 폴더에 참여해보세요",
            onOwnedGroupClick = {},
            onReceivedGroupClick = {},
        )
    }
}
