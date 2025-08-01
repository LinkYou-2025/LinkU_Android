package com.example.file.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.example.file.FileViewModel
import com.example.file.modifier.noRippleClickable
import com.example.file.ui.item.TopFolderItemLayout
import com.example.file.ui.state.EditStateViewModel
import com.example.file.ui.theme.CategoryColorStyle
import kotlinx.coroutines.flow.withIndex

val categories = listOf(
    "어학",
    "뉴스",
    "공부법",
    "IT·개발",
    "자기계발",
    "취업·이직",
    "비즈니스 인사이트",
    "생산성·툴",
    "라이프스타일",
    "심리·자기이해",
    "에세이·칼럼",
    "트렌드",
    "디자인·예술",
    "영상·뮤직",
    "맛집·여행",
    "기타"
)

@Composable
fun TopFolderGrid(
    fileViewModel: FileViewModel,
    editStateViewModel: EditStateViewModel,
    onFolderClick: (String) -> Unit,
    onFolderEdit: () -> Unit,
){
    val categoryList = fileViewModel.categoryList.collectAsState().value
    VerticalGrid(
        modifier = Modifier
            .fillMaxWidth(),
        columns = SimpleGridCells.Fixed(2),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(18.51.dp),
    ) {
        for ((i, folder) in categoryList.withIndex()) {
            val categoryName = folder.categoryName
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable { if(editStateViewModel.isEditMode){
                        onFolderEdit()
                    }else{
                        onFolderClick(categoryName)
                    } },
                contentAlignment = if(i%2==0) Alignment.TopStart else Alignment.TopEnd
            ){
                TopFolderItemLayout(
                    categoryColorStyle = CategoryColorStyle.categoryStyleList[i],
                    categoryName = categoryName,
                    isBookmarked = false,
                    editStateViewModel = editStateViewModel
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    heightDp = 2000
)
@Composable
fun TopFolderGridTest(){
    TopFolderGrid(
        fileViewModel = hiltViewModel(),
        editStateViewModel = viewModel(),
        {}
    ){}
}