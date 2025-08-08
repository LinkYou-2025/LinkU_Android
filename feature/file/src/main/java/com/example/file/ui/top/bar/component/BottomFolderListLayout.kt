// 폴더 목록을 보이는 탭의 레이아웃

package com.example.file.ui.top.bar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.file.R
import com.example.file.viewmodel.folder.state.FolderState
import com.example.file.viewmodel.folder.state.FolderStateViewModel
import com.example.file.ui.theme.CategoryColorStyle
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.White

@Composable
fun BottomFolderListLayout(
    colorStyle: CategoryColorStyle,
    folderStateViewModel: FolderStateViewModel
) {
    var text = folderStateViewModel.selectedTopFolder!!.folderName +
            if(folderStateViewModel.currentFolderState==FolderState.LINKS)
                folderStateViewModel.selectedBottomFolder?.let{ " > ${it.folderName}" }
            else ""

    // 레이아웃의 배경틀
    Box(
        modifier = Modifier
            .height(35.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .background(brush = colorStyle.horizontalGradient())
            // 가로 111.28947dp, 세로 35dp로 전체 크기 지정
            .padding(horizontal = 15.dp),
        contentAlignment = Alignment.Center
    ) {

        // 내부 배치 레이아웃
        Row(
            modifier = Modifier
                // 전체 영역을 가득 채우도록
                .wrapContentSize(),

            // 가로 정렬 방법 (요소 간 10dp 간격, 가로 중앙 정렬)
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),

            // 세로 정렬 방법 (세로 중앙 정렬)
            verticalAlignment = Alignment.CenterVertically,
        ) {

            // 현재, 상위 폴더명
            Text(
                text = text,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontFamily = DefaultFont,
                fontWeight = FontWeight(500),
                color = colorStyle.color4,
                textAlign = TextAlign.Center,
            )

            // 아래 화살표 모양 아이콘
            Icon(
                // 아이콘 색상 (Gray500)
                tint = colorStyle.color3,

                // 사용할 아이콘 이미지 리소스 (drawable/check_img.xml)
                painter = painterResource(id = R.drawable.check_img),

                // 이미지 설명 ("아래 화살표")
                contentDescription = "아래 화살표"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomFolderListLayoutTest() {
    val folderStateViewModel: FolderStateViewModel = viewModel()
    // folderStateViewModel.updateSelectedBottomFolder("단어장")
    BottomFolderListLayout(
        colorStyle = CategoryColorStyle.categoryStyleList[0],
        folderStateViewModel = folderStateViewModel
    )
}