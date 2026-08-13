package com.linku.file.ui.bottom.sheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

private const val FOLDER_NAME_MAX_LENGTH = 10

/**
 * 선택한 내 폴더의 이름을 변경하는 바텀시트 진입점입니다.
 *
 * 이름 변경 작업 자체는 [onTextDeliver]에 위임하고, 표시 상태와 dismiss는
 * [FolderStateViewModel]의 내 폴더 수정 상태에 연결합니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyFolderEditBottomSheet(
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
        onTextDeliver = onTextDeliver,
        onDismiss = { folderStateViewModel.updateBottomFolderEditBottomSheetVisible(false) }
    )
}
