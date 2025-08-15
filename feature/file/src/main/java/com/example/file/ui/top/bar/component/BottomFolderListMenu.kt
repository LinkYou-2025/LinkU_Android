package com.example.file.ui.top.bar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.file.FileViewModel
import com.example.file.viewmodel.folder.state.FolderStateViewModel
import com.example.file.ui.theme.CategoryColorStyle
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray800
import com.example.file.ui.theme.White
import com.example.file.viewmodel.folder.state.FolderState

@Composable
fun BottomFolderListMenu(
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    onChangeFolder: () -> Unit
){
    val isLinks = folderStateViewModel.currentFolderState == FolderState.LINKS
    val parentFolders = fileViewModel.parentFolders.collectAsStateWithLifecycle().value
    val subFolders = fileViewModel.subFolders.collectAsStateWithLifecycle().value

    val colorStyles = fileViewModel.categoryColorMap.collectAsStateWithLifecycle().value

    DropdownMenu(
        modifier = Modifier
            .heightIn(max = 264.dp)
            .width(205.dp),
        shape = RoundedCornerShape(18.dp),
        offset = DpOffset(0.dp, 10.dp),
        expanded = folderStateViewModel.bottomMenuExpanded,
        onDismissRequest = { folderStateViewModel.updateBottomMenuExpanded(false) },
        containerColor = White
    ) {
        if(!isLinks){
            for ((i, folder) in parentFolders.withIndex()) {
                val colorStyle = colorStyles[folder.folderName]?: CategoryColorStyle.DEFAULT

                DropdownMenuItem(
                    leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(CircleShape)
                                    .background(color = colorStyle.color4)
                            )
                        },
                    text = {
                        Text(
                            text = folder.folderName,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            fontFamily = DefaultFont,
                            fontWeight = FontWeight(400),
                            color = Gray800,
                            maxLines = 1,  // 한 줄만 보여주고
                            overflow = TextOverflow.Ellipsis  // 넘치면 ...으로 대체
                        )
                    },
                    onClick = {
                        if(folder.folderId!=folderStateViewModel.selectedTopFolder?.folderId){
                            fileViewModel.getLinksFolders(folder.folderId)
                            folderStateViewModel.updateSelectedTopFolder(folder)
                            folderStateViewModel.updateFolderState(FolderState.BOTTOM)
                            folderStateViewModel.updateBottomMenuExpanded(false)
                        }
                    }
                )
            }
        }else{
            for ((i, folder) in subFolders.withIndex()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = folder.folderName,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            fontFamily = DefaultFont,
                            fontWeight = FontWeight(400),
                            color = Gray800,
                            maxLines = 1,  // 한 줄만 보여주고
                            overflow = TextOverflow.Ellipsis  // 넘치면 ...으로 대체
                        )
                    },
                    onClick = {
                        if(folder.folderId!=folderStateViewModel.selectedBottomFolder?.folderId){
                            fileViewModel.getLinks(folder.folderId)
                            folderStateViewModel.updateSelectedBottomFolder(folder)
                            folderStateViewModel.updateFolderState(FolderState.LINKS)
                            folderStateViewModel.updateBottomMenuExpanded(false)
                        }
                    }
                )
            }

        }
    }
}

@Preview()
@Composable
fun BottomFolderListMenuTest(){
    val folderStateViewModel: FolderStateViewModel = viewModel()
    BottomFolderListMenu(
        fileViewModel = hiltViewModel(),
        folderStateViewModel = folderStateViewModel,
        onChangeFolder = {}
    )
}