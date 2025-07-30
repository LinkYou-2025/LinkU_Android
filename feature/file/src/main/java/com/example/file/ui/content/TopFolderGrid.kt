package com.example.file.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.example.file.ui.item.TopFolderItemLayout
import com.example.file.ui.theme.CategoryColorStyle

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
){
    VerticalGrid(
        modifier = Modifier
            .fillMaxWidth(),
        columns = SimpleGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.51.dp),
    ) {
        for (i in 0..15) {
            TopFolderItemLayout(
                categoryColorStyle = CategoryColorStyle.categoryStyleList[i],
                categoryName = categories[i],
                isBookmarked = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopFolderGridTest(){
    TopFolderGrid(
    )
}