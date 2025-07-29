package com.example.file.ui.content

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.file.ui.item.LinkItemLayout
import com.example.file.ui.theme.Black
import com.example.file.ui.theme.DefaultFont
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid

@Composable
fun BottomFolderGrid(
    folderList: List<String>,
    linkList: List<String>
){
    // Folder Grid
    VerticalGrid(
        modifier = Modifier
            .fillMaxWidth(),
        columns = SimpleGridCells.Fixed(2),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(18.51.dp),
    ) {
        // items 람다 안에 folder를 넘겨줘야 FolderItemLayout에서 사용할 수 있어!
        folderList.forEach { folder ->
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
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(18.51.dp)
    ) {
        // items 람다 안에 file을 넘겨줘야 LinkItemLayout에서 사용할 수 있어!
        linkList.forEach { link -> // 변수 이름을 link로 변경하는 게 더 명확할 것 같아!
            LinkItemLayout(linkList)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomFolderGridTest(){
    BottomFolderGrid(
        listOf("나의 폴더", "공유받은 폴더"),
        listOf("태그1", "태그2")
    )
}