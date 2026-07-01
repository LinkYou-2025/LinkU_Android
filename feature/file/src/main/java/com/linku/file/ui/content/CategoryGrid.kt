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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.file.FileViewModel
import com.linku.file.ui.item.CategoryItemLayout
import com.linku.file.viewmodel.edit.state.EditStateViewModel
import com.linku.file.viewmodel.folder.state.FolderState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

private const val INTER_LAYER_PADDING = 18.51
private const val ITEM_RATIO = 10f / 174f

@Composable
internal fun CategoryGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 60.dp),
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    editStateViewModel: EditStateViewModel
){
    val categoryColorMap by fileViewModel.categoryColorMap.collectAsStateWithLifecycle()
    val categories by fileViewModel.parentFolders.collectAsStateWithLifecycle()

    BoxWithConstraints(
        modifier = modifier
    ) {
        val horizontalPadding = with(LocalLayoutDirection) {
            contentPadding.calculateStartPadding(current) +
                    contentPadding.calculateEndPadding(current)
        }

        val availableWidth = maxWidth - horizontalPadding

        val horizontalSpacing = availableWidth * ITEM_RATIO

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(INTER_LAYER_PADDING.dp),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
        ) {
            items(categories) { folder ->
                CategoryItemLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable {
                            if (editStateViewModel.isEditMode) {
                                folderStateViewModel.updateReadyToUpdateTopFolder(folder)
                                folderStateViewModel.updateTopFolderEditBottomSheetVisible(true)
                            } else {
                                fileViewModel.getFoldersAndNotCategorizationLinks(folder.folderId)
                                folderStateViewModel.updateSelectedTopFolder(folder)
                                folderStateViewModel.updateFolderState(FolderState.BOTTOM)
                            }
                        },
                    colorStyle = categoryColorMap[folder.folderName] ?: CategoryColorStyle.DEFAULT,
                    folder = folder,
                    isEditMode = editStateViewModel.isEditMode,
                ) {
                    fileViewModel.updateBookmark(
                        folderId = folder.folderId,
                        updateBookmarked = !folder.isBookmarked
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    heightDp = 2000
)
@Composable
private fun CategoryGridTest(){
    CategoryGrid(
        fileViewModel = viewModel(),
        folderStateViewModel = viewModel(),
        editStateViewModel = viewModel(),
    )
}