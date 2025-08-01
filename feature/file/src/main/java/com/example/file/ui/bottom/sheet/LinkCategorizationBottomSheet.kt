package com.example.file.ui.bottom.sheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.file.R
import com.example.file.ui.state.FolderStateViewModel
import com.example.file.ui.theme.Black
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray100
import com.example.file.ui.theme.Gray800
import com.example.file.ui.theme.Purple200

data class Link(
    val title: String,
    val domain: String,
    val icon: Painter?,
    val img: Painter?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkCategorizationBottomSheet(
    folderStateViewModel: FolderStateViewModel,
) {
    val links: List<Link> = listOf(
        Link("title", "domain", painterResource(id = R.drawable.twiter_logo_img), null),
        Link("title", "domain", null, painterResource(id = R.drawable.test_img)),
        Link(
            "title",
            "domain",
            painterResource(id = R.drawable.twiter_logo_img),
            painterResource(id = R.drawable.test_img)
        ),
        Link("title", "domain", null, null),
        Link(
            "title",
            "domain",
            painterResource(id = R.drawable.twiter_logo_img),
            painterResource(id = R.drawable.test_img)
        ),
        Link(
            "title",
            "domain",
            painterResource(id = R.drawable.twiter_logo_img),
            painterResource(id = R.drawable.test_img)
        ),
        Link(
            "title",
            "domain",
            painterResource(id = R.drawable.twiter_logo_img),
            painterResource(id = R.drawable.test_img)
        ),
    )

    FileBottomSheet(
        title = "${folderStateViewModel.selectedTopFolder} 폴더의 미분류 링크 목록",
        body = "하위폴더에 추가하실 링크를 선택해주세요!",
        buttonText = "추가",
        visible = folderStateViewModel.linkCategorizationBottomSheetVisible,
        onDismiss = { folderStateViewModel.updateLinkCategorizationBottomSheetVisible(false) }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 210.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(links) {
                val title = it.title
                val domain = it.domain
                val icon = it.icon
                val img = it.img
                Row(
                    modifier = Modifier
                        .height(60.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    var checked by remember { mutableStateOf(true) }
                    Checkbox(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp)),
                        checked = checked,
                        onCheckedChange = { checked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Purple200,
                        )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(Gray100),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(18.dp)),
                            painter = img
                                ?: painterResource(id = R.drawable.link_categorization_default),
                            contentDescription = null
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 8.dp),
                    ) {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            fontFamily = DefaultFont,
                            fontWeight = FontWeight(500),
                            color = Black,
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Gray100),
                                contentAlignment = Alignment.Center
                            ) {
                                if (icon != null) {
                                    Image(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape),
                                        painter = icon,
                                        contentDescription = null
                                    )
                                }
                            }

                            Text(
                                text = domain,
                                fontSize = 12.sp,
                                lineHeight = 14.sp,
                                fontFamily = DefaultFont,
                                fontWeight = FontWeight(400),
                                color = Gray800
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 2000)
@Composable
private fun LinkCategorizationBottomSheetTest(){
    val folderStateViewModel: FolderStateViewModel = viewModel()
    folderStateViewModel.updateSelectedTopFolder("호호호")
    folderStateViewModel.updateLinkCategorizationBottomSheetVisible(true)
    LinkCategorizationBottomSheet(folderStateViewModel)
}