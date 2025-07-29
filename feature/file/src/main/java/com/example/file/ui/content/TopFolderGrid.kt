package com.example.file.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid

@Composable
fun TopFolderGrid(
    folderList: List<String>
){
    VerticalGrid(
        modifier = Modifier
            .fillMaxWidth(),
        columns = SimpleGridCells.Fixed(2),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(18.51.dp),
    ) {
        folderList.forEach { folder ->
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopFolderGridTest(){
    TopFolderGrid(
        listOf("나의 폴더", "공유받은 폴더")
    )
}