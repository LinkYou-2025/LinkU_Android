package com.linku.file.ui.bottom.sheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.linku.file.FileViewModel
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopFolderEditBottomSheet(
    folderStateViewModel: FolderStateViewModel,
    fileViewModel: FileViewModel
){
    TextFieldFileBottomSheet(
        title = "해당 카테고리를 수정하시겠습니까?",
        body = "새 카테고리명을 입력하고 대표 색상을 지정해주세요!",
        placeholderText = "카테고리명은 현재 변경 불가능합니다.",
        isEditable = true,
        visible = folderStateViewModel.topFolderEditBottomSheetVisible,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        onColorIdDeliver = { colorId ->
            fileViewModel.updateCategoryColor(
                categoryName = folderStateViewModel.readyToUpdateTopFolder!!.folderName,
                colorId = (colorId + 1).toLong(),
                colorStyle = CategoryColorStyle.categoryStyleList[colorId]
            )
        },
        onDismiss = {
            folderStateViewModel.updateTopFolderEditBottomSheetVisible(false)
        }
    )
}