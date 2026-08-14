// 폴더 목록을 보이는 탭의 레이아웃

package com.linku.file.ui.top.bar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.file.FileViewModel
import com.linku.file.R
import com.linku.file.viewmodel.folder.state.FolderState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import kotlin.collections.get

@Composable
fun MyListLayout(
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel
) {
    val colors = MaterialTheme.linkuColors

    val topFolderText = folderStateViewModel.selectedTopFolder?.folderName.orEmpty()
    val bottomFolderText = folderStateViewModel.selectedBottomFolder?.folderName.orEmpty()

    val colorStyle =
        fileViewModel.categoryColorMap.collectAsState().value[folderStateViewModel.selectedTopFolder?.folderName]
            ?: CategoryColorStyle.DEFAULT

    // 레이아웃의 배경틀
    Box(
        modifier = Modifier
            .height(35.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.white)
            .background(brush = colorStyle.horizontalGradient())
            // 가로 111.28947dp, 세로 35dp로 전체 크기 지정
            .padding(horizontal = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        // 오른쪽 화살표의 폭과 앞 간격을 먼저 예약해 긴 폴더명이 화살표를 밀어내지 못하게 합니다.
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(end = MyListArrowWidth + MyListArrowSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = topFolderText,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(500),
                color = colorStyle.color4,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (folderStateViewModel.currentFolderState == FolderState.LINKS) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = colorStyle.color3,
                    modifier = Modifier
                        .padding(horizontal = MyListPathSpacing)
                        .size(width = 7.dp, height = 13.dp),
                )

                Text(
                    text = bottomFolderText,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(500),
                    color = colorStyle.color4,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Icon(
            tint = colorStyle.color3,
            painter = painterResource(id = R.drawable.check_img),
            contentDescription = "아래 화살표",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(width = MyListArrowWidth, height = 7.dp),
        )
    }
}

private val MyListArrowWidth = 13.dp
private val MyListArrowSpacing = 10.dp
private val MyListPathSpacing = 4.dp

@Preview(showBackground = true)
@Composable
private fun MyListLayoutTest() {
    val folderStateViewModel: FolderStateViewModel = viewModel()
    // folderStateViewModel.updateSelectedBottomFolder("단어장")
    MyListLayout(
        viewModel(),
        folderStateViewModel = folderStateViewModel
    )
}
