package com.example.file.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.example.file.FileViewModel
import com.example.file.modifier.noRippleClickable
import com.example.file.ui.item.TopFolderItemLayout
import com.example.file.viewmodel.edit.state.EditStateViewModel
import com.example.file.viewmodel.folder.state.FolderState
import com.example.file.viewmodel.folder.state.FolderStateViewModel
import com.example.file.ui.theme.CategoryColorStyle

@Composable
fun TopFolderGrid(
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    editStateViewModel: EditStateViewModel,
){
    val folderList = fileViewModel.parentFolders.collectAsState().value
    VerticalGrid(
        modifier = Modifier
            .fillMaxWidth(),
        columns = SimpleGridCells.Fixed(2),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(18.51.dp),
    ) {
        for ((i, folder) in folderList.withIndex()) {
            val categoryColorStyle = fileViewModel.categoryColorMap.collectAsState().value[folder.folderName]

            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                contentAlignment = if(i%2==0) Alignment.TopStart else Alignment.TopEnd
            ){
                TopFolderItemLayout(
                    categoryColorStyle = categoryColorStyle?:CategoryColorStyle.categoryStyleList[0],
                    categoryName = folder.folderName,
                    isBookmarked = folder.isBookmarked,
                    editStateViewModel = editStateViewModel
                ){
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
fun TopFolderGridTest(){
    TopFolderGrid(
        fileViewModel = hiltViewModel(),
        folderStateViewModel = viewModel(),
        editStateViewModel = viewModel(),
    )
}