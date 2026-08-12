package com.linku.file.ui.bottom.sheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

/**
 * 현재 카테고리에 새 내 폴더를 생성하는 바텀시트 진입점입니다.
 *
 * 폴더 생성 작업 자체는 [onTextDeliver]에 위임하고, 표시 상태와 dismiss는
 * [FolderStateViewModel]의 새 폴더 상태에 연결합니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewMyFolderBottomSheet(
    onTextDeliver: (String) -> Unit,
    folderStateViewModel: FolderStateViewModel
){
    TextFieldFileBottomSheet(
        title = "새로운 폴더를 추가하시겠습니까?",
        body = "폴더명을 입력해주세요!",
        placeholderText = "폴더명은 최대 10자입니다.",
        visible = folderStateViewModel.newFolderBottomSheetVisible,
        onTextDeliver = onTextDeliver,
        onDismiss = { folderStateViewModel.updateNewFolderBottomSheetVisible(false) }
    )
}
