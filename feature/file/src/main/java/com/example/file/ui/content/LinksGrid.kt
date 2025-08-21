package com.example.file.ui.content

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.example.file.FileViewModel
import com.example.file.R
import com.example.design.modifier.noRippleClickable
import com.example.file.ui.item.LinkItemLayout
import com.example.file.ui.modal.FileModalWindow
import com.example.file.viewmodel.folder.state.FolderStateViewModel
import com.example.file.ui.theme.Black
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray600

@Composable
fun LinksGrid(
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
){
    val linkList = fileViewModel.links.collectAsStateWithLifecycle().value

    val hasNotCategorizationLinks = fileViewModel.notCategorizationLinks.collectAsStateWithLifecycle().value.isNotEmpty()

    var modalWindowVisible by remember { mutableStateOf(false) }

    VerticalGrid(
        modifier = Modifier
            .fillMaxWidth(),
        columns = SimpleGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalArrangement = Arrangement.spacedBy(18.51.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable {
                    Log.d("LinksGrid", "링크 추가하기 클릭")
                    if(hasNotCategorizationLinks){
                        folderStateViewModel.updateLinkCategorizationBottomSheetVisible(true)
                    } else{
                        modalWindowVisible = true
                    }
                },
            contentAlignment = Alignment.TopStart
        ) {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier.alpha(1f),
                ) {
                    LinkItemLayout(
                        link = null
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
                    link = link,
                    onClick = {
                        fileViewModel.onLinkClick?.invoke(link.linkuId)
                    }
                )
            }
        }
    }

    // 분류되지 않는 링크가 없으면 뜨는 모달창
    FileModalWindow(
        visible = modalWindowVisible,
        onDismiss = { modalWindowVisible = false },
        title = "분류되지 않은 링크가 없습니다.",
        positiveText = "확인"
    ) {
        Text(
            text = "새 링크를 저장한 뒤 분류해보세요!",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontFamily = DefaultFont,
            fontWeight = FontWeight.Normal,
            color = Gray600,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LinksGridTest(){
    LinksGrid(
        hiltViewModel(),
        viewModel(),
    )
}