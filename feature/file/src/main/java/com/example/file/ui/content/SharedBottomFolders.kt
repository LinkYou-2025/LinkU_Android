package com.example.file.ui.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.example.design.modal.ModalWindow
import com.example.file.FileViewModel
import com.example.file.ui.item.EmptyFolderItemLayout
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray600
import com.example.file.viewmodel.edit.state.EditStateViewModel
import com.example.file.viewmodel.folder.state.FolderState
import com.example.file.viewmodel.folder.state.FolderStateViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SharedBottomFolderGrid(
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    editStateViewModel: EditStateViewModel
) {
    val folderList by fileViewModel.sharedBottomFolders.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    * 스크롤은 compose 전 자식 요소에게 크기를 물어보고 최종 크기를 결정하는 방식.   *
    * SequentialGrid 기반인 VerticalGrid는 SubcomposeLayout임.                       *
    * 이는 자식에게 묻는 과정에서 compose가 다시 발생하는 문제로 최신부턴 사용 불가. *
    * 그러므로 꼭 LazyVertical 그리드로 변경 예정.                                   *
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    VerticalGrid(
        modifier = Modifier
            .fillMaxWidth(),
        columns = SimpleGridCells.Fixed(2),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(18.51.dp),
    ) {
        for ((i, folder) in folderList.withIndex()) {
            var visible by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = {
                            fileViewModel.getLinks(folder.folderId)
                            folderStateViewModel.updateSelectedBottomSharedFolder(folder)
                            folderStateViewModel.updateFolderState(FolderState.LINKS)
                        },
                        onLongClick = {
                            visible = true
                        }
                    ),
                contentAlignment = if(i%2==0) Alignment.TopStart else Alignment.TopEnd
            ){
                EmptyFolderItemLayout(
                    modifier = Modifier.fillMaxSize(164f/174f),
                    folderName = folder.folderName,
                )
            }

            ModalWindow(
                visible = visible,
                onOkay = {
                    fileViewModel.deleteSharedFolder(folder.folderId)
                },
                onDismiss = {visible = false},
                positiveText = "삭제하기",
                title = "해당 폴더를 삭제하시겠습니까?"
            ) {
                Text(
                    text = "삭제 시 폴더 내 모든 링크가 영구적으로\n제거되며 복구가 불가능합니다.",
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontFamily = DefaultFont,
                    fontWeight = FontWeight(400),
                    color = Gray600,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}