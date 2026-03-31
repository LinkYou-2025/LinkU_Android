package com.linku.file.ui.content

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.linku.file.FileViewModel
import com.linku.file.ui.item.TopFolderItemLayout
import com.linku.file.viewmodel.edit.state.EditStateViewModel
import com.linku.file.viewmodel.folder.state.FolderState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

@Composable
internal fun TopFolderGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 60.dp),
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    editStateViewModel: EditStateViewModel
){
    FolderGrid(
        modifier = modifier,
        contentPadding = contentPadding,
        folderList = fileViewModel.parentFolders.collectAsState().value,
        categoryColorMap = fileViewModel.categoryColorMap.collectAsState().value,
        onFolderClick = { folder ->
            if (editStateViewModel.isEditMode) {
                folderStateViewModel.updateReadyToUpdateTopFolder(folder)
                folderStateViewModel.updateTopFolderEditBottomSheetVisible(true)
            } else {
                fileViewModel.getFoldersAndNotCategorizationLinks(folder.folderId)
                folderStateViewModel.updateSelectedTopFolder(folder)
                folderStateViewModel.updateFolderState(FolderState.BOTTOM)
            }
        }
    ) { folder, colorStyle ->
        TopFolderItemLayout(
            modifier = Modifier.fillMaxSize(164f/174f),
            colorStyle = colorStyle,
            folder = folder,
            isEditMode = editStateViewModel.isEditMode,
        ){
            fileViewModel.updateBookmark(
                folderId = folder.folderId,
                updateBookmarked = !folder.isBookmarked
            )
        }
    }
}

@Preview(
    showBackground = true,
    heightDp = 2000
)
@Composable
private fun TopFolderGridTest(){
    TopFolderGrid(
        fileViewModel = hiltViewModel(),
        folderStateViewModel = viewModel(),
        editStateViewModel = viewModel(),
    )
}