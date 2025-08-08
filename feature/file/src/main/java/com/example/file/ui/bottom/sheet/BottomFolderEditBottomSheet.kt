package com.example.file.ui.bottom.sheet

import androidx.compose.runtime.Composable
import com.example.file.viewmodel.folder.state.FolderStateViewModel

@Composable
fun BottomFolderEditBottomSheet(
    folderStateViewModel: FolderStateViewModel
){
    TextFieldFileBottomSheet(
        title = "폴더명을 변경하시겠습니까?",
        body = "변경할 폴더명을 입력해주세요!",
        placeholderText = folderStateViewModel.selectedBottomFolder?:"",
        visible = folderStateViewModel.bottomFolderEditBottomSheetVisible,
        onDismiss = { folderStateViewModel.updateBottomFolderEditBottomSheetVisible(false) }
    )
}