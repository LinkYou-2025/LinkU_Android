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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.FolderSimpleInfo
import com.linku.design.modal.ModalWindow
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.file.ui.item.items.EmptyFolderItemLayout

private const val INTER_LAYER_PADDING = 18.51
private const val ITEM_RATIO = 10f / 174f

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SharedFoldersGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 60.dp),
    folderList: List<FolderSimpleInfo>,
    onFolderClick: (FolderSimpleInfo) -> Unit,
    onDeleteFolder: (FolderSimpleInfo) -> Unit
) {
    val colors = MaterialTheme.linkuColors
    val layoutDirection = LocalLayoutDirection.current

    BoxWithConstraints(
        modifier = modifier
    ) {
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
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
        ) {
            items(folderList, key = { it.folderId }) { folder ->
                var deleteModalVisible by remember { mutableStateOf(false) }
                val interactionSource = remember { MutableInteractionSource() }

                EmptyFolderItemLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .combinedClickable(
                            indication = null,
                            interactionSource = interactionSource,
                            onClick = {
                                onFolderClick(folder)
                            },
                            onLongClick = {
                                deleteModalVisible = true
                            }
                        ),
                    folderName = folder.folderName
                )

                ModalWindow(
                    visible = deleteModalVisible,
                    onOkay = {
                        onDeleteFolder(folder)
                        deleteModalVisible = false
                    },
                    onDismiss = { deleteModalVisible = false },
                    positiveText = "삭제하기",
                    negativeText = "취소하기",
                    title = "해당 폴더를 삭제하시겠습니까?"
                ) {
                    Text(
                        text = "삭제 시 폴더 내 모든 링크가 영구적으로\n" +
                                "제거되며 복구가 불가능합니다.",
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight(400),
                        color = colors.gray[600],
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private val sampleSharedFolders = listOf(
    FolderSimpleInfo(1, "Shared Folder 1", 0, false),
    FolderSimpleInfo(2, "Shared Folder 2", 0, true),
    FolderSimpleInfo(3, "Shared Folder 3", 0, false),
    FolderSimpleInfo(4, "Shared Folder 4", 0, false)
)

@Preview(showBackground = true)
@Composable
private fun SharedFoldersGridPreview() {
    LinkuPreview {
        SharedFoldersGrid(
            folderList = sampleSharedFolders,
            onFolderClick = {},
            onDeleteFolder = {}
        )
    }
}
