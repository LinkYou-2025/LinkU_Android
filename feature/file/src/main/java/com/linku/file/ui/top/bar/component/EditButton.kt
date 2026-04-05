// 수정 버튼

package com.linku.file.ui.top.bar.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.linkuColors
import com.linku.file.viewmodel.edit.state.EditStateViewModel
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

@Composable
fun EditButton(
    editStateViewModel: EditStateViewModel,
    folderViewModel: FolderStateViewModel
) {
    val colors = MaterialTheme.linkuColors

    // 수정 버튼
    Text(
        modifier = Modifier
            .noRippleClickable {
                if (folderViewModel.isEditable){
                    editStateViewModel.updateEditMode(!editStateViewModel.isEditMode)
                }
            },

        // 텍스트 내용 ("수정")
        text = if(editStateViewModel.isEditMode) "완료" else "수정",

        // 텍스트 크기 (15sp)
        fontSize = 15.sp,

        // 한 줄 높이 (22sp)
        lineHeight = 22.sp,

        // 폰트 굵기 (보통)
        fontWeight = FontWeight.Normal,

        // 글자색 (White)
        color = colors.white,

        // 텍스트 정렬 방식 (오른쪽 정렬)
        textAlign = TextAlign.Right,
    )
}

@Preview(showBackground = true)
@Composable
fun EditButtonTest() {
    EditButton(viewModel(), viewModel())
}