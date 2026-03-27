package com.example.file.ui.bottom.sheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.example.file.viewmodel.folder.state.FolderStateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBottomFolderBottomSheet(
    onTextDeliver: (String) -> Unit,
    folderStateViewModel: FolderStateViewModel
){
    TextFieldFileBottomSheet(
        title = "새로운 폴더를 추가하시겠습니까?",
        body = "폴더명을 입력해주세요!",
        placeholderText = "폴더명은 최대 10자입니다.",
        visible = folderStateViewModel.newFolderBottomSheetVisible,
        onTextDeliver = { onTextDeliver(it) },
        onDismiss = { folderStateViewModel.updateNewFolderBottomSheetVisible(false) }
    )
}