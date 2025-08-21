package com.example.file.ui.top.bar.component

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.file.FileViewModel
import com.example.file.ui.theme.Black
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.MainColor
import com.example.file.ui.theme.White
import com.example.file.viewmodel.folder.state.FolderState
import com.example.file.viewmodel.folder.state.FolderStateViewModel

@Composable
fun TopFolderListMenu(
    folderStateViewModel: FolderStateViewModel,
    fileViewModel: FileViewModel
){
    val items = listOf("나의 폴더", "공유받은 폴더")
    var selectedText = if (folderStateViewModel.isSharedFolders) "공유받은 폴더" else "나의 폴더"

    DropdownMenu(
        modifier = Modifier
            .width(150.dp),
        shape = RoundedCornerShape(18.dp),
        offset = DpOffset(0.dp, 10.dp),
        expanded = folderStateViewModel.topMenuExpanded,
        onDismissRequest = { folderStateViewModel.updateTopMenuExpanded(false) },
        containerColor = White
    ) {
        for ((i, selectedOption) in items.withIndex()){
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .graphicsLayer(alpha = 0.99f) // 강제 레이어
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent() // 기본 아이콘 먼저 그림
                                    drawRect(
                                        brush = if (selectedOption == selectedText) MainColor
                                        else Brush.horizontalGradient(listOf(White, White)),
                                        blendMode = BlendMode.SrcAtop // 아이콘 영역만 그라데이션 입힘!
                                    )
                                }
                            }
                    )
                },
                text = {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    // 폰트 크기 (15sp)
                                    fontSize = 15.sp,

                                    // 사용할 폰트 (paperlogy 폰트)
                                    fontFamily = DefaultFont,

                                    // 폰트 굵기
                                    fontWeight = FontWeight(if (selectedOption == selectedText) 500 else 400),

                                    // 텍스트 그라데이션 색상(링큐 메인 색상)
                                    brush = if (selectedOption == selectedText) MainColor
                                    else Brush.horizontalGradient(listOf(Black, Black))
                                )
                            ) {
                                // 실제 표시할 텍스트
                                append(selectedOption)
                            }
                        },
                    )
                },
                onClick = {
                    if (selectedOption != selectedText){
                        if (i == 0) {
                            // 나의 폴더 클릭 시
                            folderStateViewModel.updateIsSharedFolders(false)
                        } else {
                            // 공유 받은 폴더 클릭 시
                            fileViewModel.getSharedFolders()
                            folderStateViewModel.updateIsSharedFolders(true)
                        }
                        folderStateViewModel.updateTopMenuExpanded(false)
                        folderStateViewModel.updateFolderState(FolderState.TOP)
                    }
                }
            )
        }
    }
}

@Preview()
@Composable
private fun FolderListMenuTest(){
    val folderStateViewModel: FolderStateViewModel = viewModel()
    TopFolderListMenu(
        folderStateViewModel = folderStateViewModel,
        fileViewModel = viewModel()
    )
}