package com.linku.file.ui.content

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.linku.design.modifier.noRippleClickable
import com.linku.file.FileViewModel
import com.linku.file.ui.item.items.EmptyFolderItemLayout
import com.linku.file.viewmodel.edit.state.EditStateViewModel
import com.linku.file.viewmodel.folder.state.FolderState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

@Composable
fun SharedTopFolderGrid(
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    editStateViewModel: EditStateViewModel
) {
    val folderList = fileViewModel.sharedTopFolders.collectAsState().value

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
            Log.d("SharedTopFolderGrid", "folder: $folder")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable {
                        folderStateViewModel.updateSelectedSharedFolder(folder)
                        fileViewModel.getSharedBottomFolders(folder)
                        folderStateViewModel.updateFolderState(FolderState.BOTTOM)
                    },
                contentAlignment = if(i%2==0) Alignment.TopStart else Alignment.TopEnd
            ){
                EmptyFolderItemLayout(
                    modifier = Modifier.fillMaxSize(164f/174f),
                    folderName = "${folder.nickname}의 폴더",
                )
            }
        }
    }
}
