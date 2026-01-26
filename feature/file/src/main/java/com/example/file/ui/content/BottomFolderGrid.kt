package com.example.file.ui.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.example.file.FileViewModel
import com.example.file.R
import com.example.design.modifier.noRippleClickable
import com.example.file.ui.modal.FileModalWindow
import com.example.file.ui.item.BottomFolderItemLayout
import com.example.file.ui.item.EmptyFolderItemLayout
import com.example.file.ui.item.LinkItemLayout
import com.example.file.viewmodel.edit.state.EditStateViewModel
import com.example.file.viewmodel.folder.state.FolderStateViewModel
import com.example.file.ui.theme.Black
import com.example.design.theme.color.CategoryColorStyle
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray600
import com.example.file.viewmodel.folder.state.FolderState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomFolderGrid(
    fileViewModel: FileViewModel,
    editStateViewModel: EditStateViewModel,
    folderStateViewModel: FolderStateViewModel,
    onFolderAdd: () -> Unit
){
    val interactionSource = remember { MutableInteractionSource() }

    val folderList by fileViewModel.subFolders.collectAsStateWithLifecycle()
    val linkList by fileViewModel.notCategorizationLinks.collectAsStateWithLifecycle()

    var deleteModalWindowVisible by remember { mutableStateOf(false) }
    var selectedLinkId by remember { mutableStateOf<Long?>(null) }

    Column {
        // Folder Grid
        VerticalGrid(
            modifier = Modifier
                .fillMaxWidth(),
            columns = SimpleGridCells.Fixed(2),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(18.51.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .noRippleClickable{
                            if(!editStateViewModel.isEditMode){ onFolderAdd() }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    EmptyFolderItemLayout(
                        modifier = Modifier.fillMaxSize(164f/174f)
                    )

                    Image(
                        modifier = Modifier.padding(top = 71.dp),
                        painter = painterResource(R.drawable.add_folder_icon),
                        contentDescription = null
                    )

                    Text(
                        text = "폴더 추가하기",
                        fontSize = 15.sp,
                        fontFamily = DefaultFont,
                        fontWeight = FontWeight(500),
                        color = Black,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            // items 람다 안에 folder를 넘겨줘야 FolderItemLayout에서 사용할 수 있어!
            for((i, folder) in folderList.withIndex()) {
                var visible by remember { mutableStateOf(false) }

                val parentModifier = if (!editStateViewModel.isEditMode) {
                    Modifier.combinedClickable(
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = {
                            fileViewModel.getLinks(folder.folderId)
                            folderStateViewModel.updateSelectedBottomFolder(folder)
                            folderStateViewModel.updateFolderState(FolderState.LINKS)
                          },
                        onLongClick = { visible = true }
                    )
                } else {
                    Modifier.combinedClickable(
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = { /* ... */ },
                        onLongClick = { visible = true }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(parentModifier),
                    contentAlignment = if(i%2==1) Alignment.TopStart else Alignment.TopEnd
                ) {
                    val categoryColorStyle = fileViewModel.categoryColorMap.collectAsState().value[folderStateViewModel.selectedTopFolder?.folderName]

                    BottomFolderItemLayout(
                        modifier = Modifier.fillMaxSize(164f/174f),
                        colorStyle = categoryColorStyle?:CategoryColorStyle.categoryStyleList[0],
                        folder = folder,
                        editStateViewModel = editStateViewModel,
                        onEdit = {
                            folderStateViewModel.updateReadyToUpdateBottomFolder(folder)
                            folderStateViewModel.updateBottomFolderEditBottomSheetVisible(true)
                        },
                        onChangeSharing = {
                            fileViewModel.changeSharing(folder)
                        }
                    )
                }

                FileModalWindow(
                    visible = visible,
                    onOkay = {fileViewModel.deleteSubfolder(folder.folderId, i)},
                    onDismiss = {visible = false},
                    positiveText = "삭제하기",
                    negativeText = "취소하기",
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


        if(linkList.isNotEmpty()){// "분류되지 않은 링크" 텍스트
            Text(
                text = "분류되지 않은 링크",
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontFamily = DefaultFont,
                fontWeight = FontWeight(700),
                color = Black,
                modifier = Modifier.padding(top = 40.dp, bottom = 20.dp) // 위아래 간격 추가
            )


            // Link Grid
            VerticalGrid(
                modifier = Modifier
                    .fillMaxWidth(),
                columns = SimpleGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                verticalArrangement = Arrangement.spacedBy(18.51.dp)
            ) {
                // items 람다 안에 file을 넘겨줘야 LinkItemLayout에서 사용할 수 있어!
                for ((i, link) in linkList.withIndex()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = if (i % 2 == 0) Alignment.TopStart else Alignment.TopEnd
                    ) {
                        LinkItemLayout(
                            link = link,
                            onClick = {
                                fileViewModel.onLinkClick?.invoke(link.linkuId)
                            },
                            onLongClick = {
                                selectedLinkId = link.linkuId

                                deleteModalWindowVisible = true
                            }
                        )
                    }
                }
            }
        }
    }

    // 링크 삭제 모달창
    FileModalWindow(
        visible = deleteModalWindowVisible,
        onOkay = {
            // ✅ 확인에서 안전하게 현재 선택된 id로 삭제
            selectedLinkId?.let { id ->
                fileViewModel.deleteNotCategorizationLink(id)
            }
            // 상태 정리
            deleteModalWindowVisible = false
            selectedLinkId = null
        },
        onDismiss = { deleteModalWindowVisible = false },
        title = "해당 링크를 삭제하시겠습니까?",
        positiveText = "삭제",
        negativeText = "취소"
    ) {
        Text(
            text = "삭제 시 해당 링크가 영구적으로 제거되며\n복구가 불가능합니다.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontFamily = DefaultFont,
            fontWeight = FontWeight(400),
            color = Gray600,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomFolderGridTest(){
    BottomFolderGrid(
        fileViewModel = hiltViewModel(),
        editStateViewModel = viewModel(),
        folderStateViewModel = viewModel(),
    ){}
}