package com.example.file.ui.bottom.sheet

import androidx.compose.runtime.Composable
import com.example.file.viewmodel.folder.state.FolderStateViewModel

@Composable
fun TopFolderEditBottomSheet(
    folderStateViewModel: FolderStateViewModel
){
    TextFieldFileBottomSheet(
        title = "해당 카테고리를 수정하시겠습니까?",
        body = "새 카테고리명을 입력하고 대표 색상을 지정해주세요!",
        placeholderText = "저장",
        isEditable = true,
        visible = folderStateViewModel.topFolderEditBottomSheetVisible,
        onDismiss = { folderStateViewModel.upadateTopFolderEditBottomSheetVisible(false) }
    )
}