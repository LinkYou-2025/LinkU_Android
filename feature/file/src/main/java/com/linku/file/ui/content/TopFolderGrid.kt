package com.linku.file.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.linku.file.FileViewModel
import com.linku.design.modifier.noRippleClickable
import com.linku.file.ui.item.TopFolderItemLayout
import com.linku.file.viewmodel.edit.state.EditStateViewModel
import com.linku.file.viewmodel.folder.state.FolderState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import com.linku.design.theme.color.CategoryColorStyle

@Composable
fun TopFolderGrid(
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    editStateViewModel: EditStateViewModel,
){
    val folderList = fileViewModel.parentFolders.collectAsState().value

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    * 스크롤은 compose 전 자식 요소에게 크기를 물어보고 최종 크기를 결정하는 방식.   *
    * SequentialGrid 기반인 VerticalGrid는 SubcomposeLayout임.                       *
    * 이는 자식에게 묻는 과정에서 compose가 다시 발생하는 문제로 최신부턴 사용 불가. *
    * 그러므로 꼭 LazyVertical 그리드로 변경 예정.                                   *
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    VerticalGrid(
        modifier = Modifier
            .fillMaxWidth(),
        columns = SimpleGridCells.Fixed(2),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(18.51.dp),
    ) {
        for ((i, folder) in folderList.withIndex()) {
            val categoryColorStyle = fileViewModel.categoryColorMap.collectAsState().value[folder.folderName]

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // 임시 추가
                    .height(153.52942.dp)
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
                horizontalArrangement = if(i%2==0) Arrangement.Start else Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){
                TopFolderItemLayout(
                    //modifier = Modifier.fillMaxSize(164f/174f),   // 임시 제거
                    modifier = Modifier.fillMaxHeight(164f/174f),   // 임시 추가
                    colorStyle = categoryColorStyle?:CategoryColorStyle.categoryStyleList[0],
                    folderName = folder.folderName,
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