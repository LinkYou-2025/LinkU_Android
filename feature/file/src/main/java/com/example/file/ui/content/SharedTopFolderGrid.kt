package com.example.file.ui.content

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.example.file.FileViewModel
import com.example.design.modifier.noRippleClickable
import com.example.file.ui.item.EmptyFolderItemLayout
import com.example.file.viewmodel.edit.state.EditStateViewModel
import com.example.file.viewmodel.folder.state.FolderState
import com.example.file.viewmodel.folder.state.FolderStateViewModel

@Composable
fun SharedTopFolderGrid(
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    editStateViewModel: EditStateViewModel
) {
    val folderList = fileViewModel.sharedTopFolders.collectAsState().value
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
                    categoryName = "${folder.nickname}의 폴더",
                )
            }
        }
    }
}