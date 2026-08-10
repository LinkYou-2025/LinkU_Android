package com.linku.file.ui.bottom.sheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

private const val FOLDER_NAME_MAX_LENGTH = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomFolderEditBottomSheet(
    onTextDeliver: (String) -> Unit,
    folderStateViewModel: FolderStateViewModel
){
    TextFieldFileBottomSheet(
        title = "폴더명을 변경하시겠습니까?",
        body = "변경할 폴더명을 입력해주세요!",
        placeholderText = folderStateViewModel.readyToUpdateBottomFolder?.folderName?:"에러",
        visible = folderStateViewModel.bottomFolderEditBottomSheetVisible,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        maxTextLength = FOLDER_NAME_MAX_LENGTH,
        onTextDeliver = { onTextDeliver(it) },
        onDismiss = { folderStateViewModel.updateBottomFolderEditBottomSheetVisible(false) }
    )
}
