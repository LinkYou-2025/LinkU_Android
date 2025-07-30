package com.example.file.ui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.example.file.R
import com.example.file.modifier.noRippleClickable
import com.example.file.ui.item.BottomFolderItemLayout
import com.example.file.ui.item.EmptyFolderItemLayout
import com.example.file.ui.item.LinkItemLayout
import com.example.file.ui.state.EditState
import com.example.file.ui.theme.Black
import com.example.file.ui.theme.CategoryColorStyle
import com.example.file.ui.theme.DefaultFont

@Composable
fun BottomFolderGrid(
    folderList: List<String>,
    linkList: List<String>,
    editState: EditState,
    onFolderClick: (String) -> Unit
){
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
                    contentAlignment = Alignment.Center
                ) {
                    EmptyFolderItemLayout()

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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .noRippleClickable{ onFolderClick(folder)},
                    contentAlignment = if(i%2==1) Alignment.TopStart else Alignment.TopEnd
                ) {
                    BottomFolderItemLayout(
                        categoryColorStyle = CategoryColorStyle.categoryStyleList[0],
                        categoryName = folder,
                        editState = editState
                    )
                }
            }
        }


        // "분류되지 않은 링크" 텍스트
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
            for((i, link) in linkList.withIndex()){
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = if(i%2==0) Alignment.TopStart else Alignment.TopEnd
                ) {
                    LinkItemLayout(
                        title = link
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomFolderGridTest(){
    BottomFolderGrid(
        listOf("카테고리 1","카테고리 2","카테고리 3","카테고리 4","카테고리 5"),
        listOf("태그1", "태그2"),
        editState = viewModel()
    ){}
}