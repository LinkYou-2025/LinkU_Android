package com.example.file.ui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.file.ui.item.LinkItemLayout
import com.example.file.viewmodel.folder.state.FolderStateViewModel
import com.example.file.ui.theme.Black
import com.example.file.ui.theme.DefaultFont

@Composable
fun LinksGrid(
    folderStateViewModel: FolderStateViewModel,
    linkList: List<String>
){
    VerticalGrid(
        modifier = Modifier
            .fillMaxWidth(),
        columns = SimpleGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalArrangement = Arrangement.spacedBy(18.51.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.TopStart
        ) {
            Box(
                modifier = Modifier
                    .noRippleClickable {
                        folderStateViewModel.updateLinkCategorizationBottomSheetVisible(true)
                    },
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier.alpha(1f),
                ) {
                    LinkItemLayout(
                        painter = null,
                        tags = listOf("태그1", "태그2"),
                    )
                }

                Image(
                    modifier = Modifier.padding(top = 103.dp),
                    painter = painterResource(R.drawable.add_folder_icon),
                    contentDescription = null
                )

                Text(
                    modifier = Modifier.padding(top = 147.dp),
                    text = "링크 추가하기",
                    fontSize = 15.sp,
                    fontFamily = DefaultFont,
                    fontWeight = FontWeight(500),
                    color = Black,
                    textAlign = TextAlign.Center,
                )
            }
        }


        // items 람다 안에 folder를 넘겨줘야 FolderItemLayout에서 사용할 수 있어!
        for((i, link) in linkList.withIndex()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = if(i%2==1) Alignment.TopStart else Alignment.TopEnd
            ) {
                LinkItemLayout(
                    title = link
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LinksGridTest(){
    LinksGrid(
        viewModel(),
        listOf("나의 폴더", "공유받은 폴더")
    )
}