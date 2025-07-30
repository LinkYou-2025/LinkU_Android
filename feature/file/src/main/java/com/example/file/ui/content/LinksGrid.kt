package com.example.file.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.example.file.ui.item.LinkItemLayout

@Composable
fun LinksGrid(
    linkList: List<String>
){
    VerticalGrid(
        modifier = Modifier
            .fillMaxWidth(),
        columns = SimpleGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.51.dp),
    ) {

        // items 람다 안에 folder를 넘겨줘야 FolderItemLayout에서 사용할 수 있어!
        linkList.forEach { link ->
            LinkItemLayout(linkList)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LinksGridTest(){
    LinksGrid(
        listOf("나의 폴더", "공유받은 폴더")
    )
}