package com.example.file.ui.bottom.sheet

import androidx.compose.runtime.Composable
import com.example.file.ui.state.FolderStateViewModel

@Composable
fun NewBottomFolderBottomSheet(
    folderStateViewModel: FolderStateViewModel
){
    TextFieldFileBottomSheet(
        title = "새로운 폴더를 추가하시겠습니까?",
        body = "폴더명을 입력해주세요!",
        placeholderText = "폴더명은 최대 10자입니다.",
        visible = folderStateViewModel.newFolderBottomSheetVisible,
        onDismiss = { folderStateViewModel.updateNewFolderBottomSheetVisible(false) }
    )
}