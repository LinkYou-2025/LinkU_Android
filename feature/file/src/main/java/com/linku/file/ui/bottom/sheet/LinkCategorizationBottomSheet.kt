package com.linku.file.ui.bottom.sheet

import android.util.Log
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.linku.core.model.LinkItemInfo
import com.linku.file.FileViewModel
import com.linku.file.R
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.color.Basic
import com.linku.file.ui.theme.Black
import com.linku.file.ui.theme.DefaultFont
import com.linku.file.ui.theme.Gray100
import com.linku.file.ui.theme.Gray800
import com.linku.file.ui.theme.Purple200
import com.linku.file.ui.theme.domainLogoPainterOrNull
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkCategorizationBottomSheet(
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
) {
    val links by fileViewModel.notCategorizationLinks.collectAsStateWithLifecycle()

    //var link by remember { mutableStateOf<LinkItemInfo?>(null) }

    val selectedLinks = remember { mutableStateListOf<LinkItemInfo>() }

    val scope = rememberCoroutineScope()

    FileBottomSheet(
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true // 부분 확장 없이 바로 전체
        ),
        title = "${folderStateViewModel.selectedTopFolder?.folderName?:""} 폴더의 미분류 링크 목록",
        body = "하위폴더에 추가하실 링크를 선택해주세요!",
        buttonText = "추가",
        isReady = selectedLinks.isNotEmpty(),
        visible = folderStateViewModel.linkCategorizationBottomSheetVisible,
        onOkay = {
            scope.launch{
                val folderId = requireNotNull(folderStateViewModel.selectedBottomFolder?.folderId)

                selectedLinks.forEach {
                    fileViewModel.updateLinkFolder(it, folderId)
                }

                selectedLinks.clear()
            }
        },
        onDismiss = { folderStateViewModel.updateLinkCategorizationBottomSheetVisible(false) }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 210.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(links) {
                val link = it
                val title = it.title
                val url = it.url
                val icon = domainLogoPainterOrNull(it.url)?:painterResource(R.drawable.link_categorization_default)
                val img = painterResource(R.drawable.link_categorization_default)//it.img

                var checked by remember { mutableStateOf(false)}
                Row(
                    modifier = Modifier
                        .height(60.dp)
                        .offset(x = (-20).dp)
                        .noRippleClickable{
                            if(checked){
                                Log.d("LinkCategorizationBottomSheet", "checked: $it")
                                selectedLinks.remove(link)
                                checked = selectedLinks.contains(link)
                            } else {
                                Log.d("LinkCategorizationBottomSheet", "checked: $it")
                                selectedLinks.add(link)
                                checked = selectedLinks.contains(link)
                            }
                        },
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = {
                            if(checked){
                                Log.d("LinkCategorizationBottomSheet", "checked: $it")
                                selectedLinks.remove(link)
                                checked = selectedLinks.contains(link)
                            } else {
                                Log.d("LinkCategorizationBottomSheet", "checked: $it")
                                selectedLinks.add(link)
                                checked = selectedLinks.contains(link)
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Purple200,
                            uncheckedColor = Basic.gray[200],
                        )
                    )

                    Box(
                        modifier = Modifier
                            //.height(60.dp)
                            .background(
                                color = Gray100,
                                shape = RoundedCornerShape(18.dp)
                                )
                            /*.clip(RoundedCornerShape(18.dp))*/,
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxHeight(),
                            painter = img,
                            contentDescription = null
                        )
                    }


                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        //verticalArrangement = Arrangement.SpaceBetween

                    ) {
                        // 링크 상단 패딩
                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontFamily = DefaultFont,
                            fontWeight = FontWeight(500),
                            color = Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
                                Image(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape),
                                    painter = icon,
                                    contentDescription = null
                                )
                            }

                            Text(
                                text = url,
                                fontSize = 12.sp,
                                fontFamily = DefaultFont,
                                fontWeight = FontWeight(400),
                                color = Gray800,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        // 링크 하단 패딩
                        Spacer(modifier = Modifier.height(7.dp))
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
    folderStateViewModel.updateLinkCategorizationBottomSheetVisible(true)
    LinkCategorizationBottomSheet(
        hiltViewModel(),
        folderStateViewModel
    )
}