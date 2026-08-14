package com.linku.file.ui.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linku.core.model.FolderSimpleInfo
import com.linku.design.theme.LinkuPreview
import com.linku.file.ui.item.items.EmptyFolderItemLayout

private const val INTER_LAYER_PADDING = 18.51
private const val ITEM_RATIO = 10f / 174f

/**
 * 현재 공유 범위에 속한 폴더 목록을 표시하는 상태 없는 그리드입니다.
 *
 * 일반 상태에서는 tap으로 상세 이동을 요청하고 long press는 제공하지 않습니다. 나가기 모드에서는
 * tap을 무시하며 long press만 안정적인 폴더 객체와 함께 상위로 전달합니다. 확인 Dialog와 API
 * 요청은 이 컴포넌트가 소유하지 않습니다.
 *
 * @param folderList 표시할 공유폴더 목록
 * @param isLeaveMode 폴더 나가기 대상을 선택하는 모드인지 여부
 * @param onFolderClick 일반 모드에서 선택한 폴더의 상세 이동 요청
 * @param onFolderLongClick 나가기 모드에서 길게 누른 폴더의 확인 요청
 * @param onLongClickLabel 폴더별 접근성 long-click 동작 레이블
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun SharedFoldersGrid(
    folderList: List<FolderSimpleInfo>,
    isLeaveMode: Boolean,
    onFolderClick: (FolderSimpleInfo) -> Unit,
    onFolderLongClick: (FolderSimpleInfo) -> Unit,
    onLongClickLabel: (FolderSimpleInfo) -> String,
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
        val horizontalSpacing = availableWidth * ITEM_RATIO

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(INTER_LAYER_PADDING.dp),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        ) {
            items(
                items = folderList,
                key = { folder -> folder.folderId },
            ) { folder ->
                val interactionSource = remember(folder.folderId) {
                    MutableInteractionSource()
                }

                EmptyFolderItemLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .combinedClickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {
                                if (!isLeaveMode) {
                                    onFolderClick(folder)
                                }
                            },
                            onLongClickLabel = if (isLeaveMode) {
                                onLongClickLabel(folder)
                            } else {
                                null
                            },
                            onLongClick = if (isLeaveMode) {
                                { onFolderLongClick(folder) }
                            } else {
                                null
                            },
                        ),
                    folderName = folder.folderName,
                )
            }
        }
    }
}

private val sampleSharedFolders = listOf(
    FolderSimpleInfo(1, "Shared Folder 1", 0, false),
    FolderSimpleInfo(2, "Shared Folder 2", 0, true),
)

@Preview(showBackground = true)
@Composable
private fun SharedFoldersGridPreview() {
    LinkuPreview {
        SharedFoldersGrid(
            folderList = sampleSharedFolders,
            isLeaveMode = true,
            onFolderClick = {},
            onFolderLongClick = {},
            onLongClickLabel = { folder -> "${folder.folderName} 폴더 나가기" },
        )
    }
}
