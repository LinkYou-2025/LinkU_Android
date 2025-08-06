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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.file.ui.content.categories
import com.example.file.viewmodel.folder.state.FolderStateViewModel
import com.example.file.ui.theme.CategoryColorStyle
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray800
import com.example.file.ui.theme.White

@Composable
fun BottomFolderListMenu(
    folderStateViewModel: FolderStateViewModel,
    items: List<String> = categories,
    isLinks: Boolean = false,
    onChangeFolder: () -> Unit
){
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
        for((i, category) in items.withIndex()){
            DropdownMenuItem(
                leadingIcon = if (isLinks) null else {
                    @Composable {
                        Box(
                            modifier = Modifier
                                .size(25.dp)
                                .clip(CircleShape)
                                .background(color = CategoryColorStyle.categoryStyleList[i].color4)
                        )
                    }
                },
                text = {
                    Text(
                        text = category,
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
                    onChangeFolder()
                }
            )
        }
    }
}

@Preview()
@Composable
fun BottomFolderListMenuTest(){
    val folderStateViewModel: FolderStateViewModel = viewModel()
    BottomFolderListMenu(
        folderStateViewModel = folderStateViewModel,
//        items = listOf("나의 폴더", "공유받은 폴더"),
        isLinks = true,
        onChangeFolder = {}
    )
}