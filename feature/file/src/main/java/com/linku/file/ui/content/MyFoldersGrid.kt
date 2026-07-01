package com.linku.file.ui.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.linku.core.model.FolderSimpleInfo
import com.linku.design.modal.ModalWindow
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.file.FileViewModel
import com.linku.file.R
import com.linku.file.ui.item.EmptyFolderItemLayout
import com.linku.file.ui.item.LinkItemLayout
import com.linku.file.ui.item.MyFolderItemLayout
import com.linku.file.viewmodel.edit.state.EditStateViewModel
import com.linku.file.viewmodel.folder.state.FolderState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

private const val INTER_LAYER_PADDING = 18.51
private const val SECTION_TITLE_TOP_PADDING = 21.49
private const val SECTION_TITLE_BOTTOM_PADDING = 1.49
private const val ITEM_RATIO = 10f / 174f

@Composable
internal fun MyFoldersGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 60.dp),
    fileViewModel: FileViewModel,
    editStateViewModel: EditStateViewModel,
    folderStateViewModel: FolderStateViewModel
) {
    val colors = MaterialTheme.linkuColors

    val folderList by fileViewModel.subFolders.collectAsStateWithLifecycle()
    val linkList by fileViewModel.notCategorizationLinks.collectAsStateWithLifecycle()
    val categoryColorMap by fileViewModel.categoryColorMap.collectAsStateWithLifecycle()

    val selectedTopFolderColorStyle =
        categoryColorMap[folderStateViewModel.selectedTopFolder?.folderName]
            ?: CategoryColorStyle.categoryStyleList[0]

    var deleteModalWindowVisible by remember { mutableStateOf(false) }
    var selectedLinkId by remember { mutableStateOf<Long?>(null) }

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
            item {
                AddBottomFolderItem(
                    modifier = Modifier
                        .fillMaxSize(164f / 174f)
                        .noRippleClickable {
                            if (!editStateViewModel.isEditMode) {
                                folderStateViewModel.updateNewFolderBottomSheetVisible(true)
                            }
                        }
                )
            }

            items(folderList) { folder ->
                MyFolderItem(
                    folder = folder,
                    colorStyle = selectedTopFolderColorStyle,
                    isEditMode = editStateViewModel.isEditMode,
                    onClick = {
                        fileViewModel.getLinks(folder.folderId)
                        folderStateViewModel.updateSelectedBottomFolder(folder)
                        folderStateViewModel.updateFolderState(FolderState.LINKS)
                    },
                    onEdit = {
                        folderStateViewModel.updateReadyToUpdateBottomFolder(folder)
                        folderStateViewModel.updateBottomFolderEditBottomSheetVisible(true)
                    },
                    onChangeSharing = {
                        fileViewModel.folderToPrivate(folder)
                    },
                    onDelete = {
                        fileViewModel.deleteSubfolder(folder.folderId)
                    }
                )
            }

            if (linkList.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "분류되지 않은 링크",
                        fontSize = 20.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight(700),
                        color = colors.black,
                        modifier = Modifier.padding(
                            top = SECTION_TITLE_TOP_PADDING.dp,
                            bottom = SECTION_TITLE_BOTTOM_PADDING.dp
                        )
                    )
                }

                items(linkList) { link ->
                    LinkItemLayout(
                        modifier = Modifier.fillMaxSize(164f / 174f),
                        link = link,
                        onClick = {
                            fileViewModel.onLinkClick?.invoke(link.linkuId)
                        },
                        onLongClick = {
                            selectedLinkId = link.linkuId
                            deleteModalWindowVisible = true
                        }
                    )
                }
            }
        }
    }

    ModalWindow(
        visible = deleteModalWindowVisible,
        onOkay = {
            selectedLinkId?.let { id ->
                fileViewModel.deleteNotCategorizationLink(id)
            }
            deleteModalWindowVisible = false
            selectedLinkId = null
        },
        onDismiss = { deleteModalWindowVisible = false },
        title = "해당 링크를 삭제하시겠습니까?",
        positiveText = "삭제",
        negativeText = "취소"
    ) {
        Text(
            text = "삭제 시 해당 링크가 영구적으로 제거되며\n복구가 불가능합니다.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight(400),
            color = colors.gray[600],
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AddBottomFolderItem(
    modifier: Modifier
) {
    val colors = MaterialTheme.linkuColors

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            EmptyFolderItemLayout()

            Image(
                modifier = Modifier.padding(top = 71.dp),
                painter = painterResource(R.drawable.add_folder_icon),
                contentDescription = null
            )

            Text(
                text = "폴더 추가하기",
                fontSize = 15.sp,
                fontWeight = FontWeight(500),
                color = colors.black,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MyFolderItem(
    folder: FolderSimpleInfo,
    colorStyle: CategoryColorStyle,
    isEditMode: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onChangeSharing: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = MaterialTheme.linkuColors
    val interactionSource = remember { MutableInteractionSource() }
    var deleteModalWindowVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.combinedClickable(
            indication = null,
            interactionSource = interactionSource,
            onClick = {
                if (!isEditMode) {
                    onClick()
                }
            },
            onLongClick = {
                deleteModalWindowVisible = true
            }
        ),
        contentAlignment = Alignment.Center
    ) {
        MyFolderItemLayout(
            modifier = Modifier.fillMaxSize(164f / 174f),
            colorStyle = colorStyle,
            folder = folder,
            isEditMode = isEditMode,
            onEdit = onEdit,
            onChangeSharing = onChangeSharing
        )
    }

    ModalWindow(
        visible = deleteModalWindowVisible,
        onOkay = {
            onDelete()
            deleteModalWindowVisible = false
        },
        onDismiss = { deleteModalWindowVisible = false },
        positiveText = "삭제하기",
        negativeText = "취소하기",
        title = "해당 폴더를 삭제하시겠습니까?"
    ) {
        Text(
            text = "삭제 시 폴더 내 모든 링크가 영구적으로\n제거되며 복구가 불가능합니다.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight(400),
            color = colors.gray[600],
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyFoldersGridTest() {
    MyFoldersGrid(
        fileViewModel = viewModel(),
        editStateViewModel = viewModel(),
        folderStateViewModel = viewModel(),
    )
}
