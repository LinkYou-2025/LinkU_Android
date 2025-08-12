package com.example.file.ui.bottom.sheet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.model.FolderSimpleInfo
import com.example.file.FileViewModel
import com.example.file.R
import com.example.file.ui.item.BottomFolderItemLayout
import com.example.file.ui.item.EmptyFolderItemLayout
import com.example.file.ui.item.TopFolderItemLayout
import com.example.file.ui.theme.CategoryColorStyle
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray400
import com.example.file.ui.theme.Gray800
import com.example.file.ui.theme.MainColor
import com.example.file.ui.theme.White
import com.example.file.viewmodel.edit.state.EditStateViewModel
import com.example.file.viewmodel.folder.state.FolderState
import com.example.file.viewmodel.folder.state.FolderStateViewModel

@Composable
fun ShareBottomSheet(
    userName: String,
    folderStateViewModel: FolderStateViewModel,
    fileViewModel: FileViewModel
){
    var selectedTopFolder by remember { mutableStateOf<FolderSimpleInfo?>(null) }
    var selectedBottomFolder by remember { mutableStateOf<FolderSimpleInfo?>(null) }

    var bottomFolderList by remember { mutableStateOf<List<FolderSimpleInfo>>(emptyList()) }

    var state by remember { mutableStateOf(FolderState.TOP) }

    // 메뉴 레이아웃
    @Composable
    fun ShareFolderMenu(
        folderList: List<FolderSimpleInfo>,
        selectable: Boolean,
        colorStyle: CategoryColorStyle? = null,
        onClick: (FolderSimpleInfo) -> Unit
    ){
        var expanded by remember { mutableStateOf(false) }
        val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "")
        Row(
            modifier = Modifier
                .width(300.dp)
                .height(51.dp)
                .border(1.dp, MainColor, RoundedCornerShape(18.dp))
                .padding(horizontal = 21.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "공유하실 폴더의 카테고리를 선택해주세요.",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = DefaultFont,
                fontWeight = FontWeight(400),
                color = Gray400,
            )

            val modifier = if(expanded) Modifier
                .width(14.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = MainColor,
                            blendMode = BlendMode.SrcAtop,
                            alpha = if (selectable) 1f else 0.5f
                        )
                    }
                } else Modifier.width(14.dp)

            Image(
                painter = painterResource(R.drawable.check_img),
                contentDescription = null,
                modifier = modifier.rotate(rotation)
            )
        }

        if(selectable){
            DropdownMenu(
                modifier = Modifier
                    .width(300.dp)
                    .heightIn(max = 224.dp),
                shape = RoundedCornerShape(18.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = White
            ) {
                for ((i, folder) in folderList.withIndex()) {
                    val categoryColorStyle = fileViewModel.categoryColorMap.collectAsState().value[folder.folderName]

                    DropdownMenuItem(
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(CircleShape)
                                    .background(
                                        color = colorStyle?.color1
                                            ?: categoryColorStyle!!.color4
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = colorStyle?.color4
                                            ?: categoryColorStyle!!.color4,
                                    )
                            )
                        },
                        text = {
                            Text(
                                text = folder.folderName,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                fontFamily = DefaultFont,
                                fontWeight = FontWeight(400),
                                color = Gray800,
                                maxLines = 1,  // 한 줄만 보여주고
                                overflow = TextOverflow.Ellipsis  // 넘치면 ...으로 대체
                            )
                        },
                        onClick = {
                            onClick(folder)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    FileBottomSheet(
        title = "폴더를 공유하시겠습니까?",
        body = "공유하실 파일의 카테고리와 폴더를 선택해주세요!",
        buttonText = "공유 링크 생성",
        visible = folderStateViewModel.shareBottomSheetVisible,
        isShareMode = state == FolderState.LINKS,
        onOkay = {},
        onDismiss = {
            folderStateViewModel.updateShareBottomSheetVisible(false)
        }
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val dummyVM: EditStateViewModel = viewModel()
            val categoryColorStyle = fileViewModel.categoryColorMap.collectAsState().value[selectedTopFolder?.folderName]

            when(state){
                FolderState.BOTTOM -> {
                    TopFolderItemLayout(
                        categoryColorStyle = categoryColorStyle?:CategoryColorStyle.categoryStyleList[0],
                        categoryName = selectedTopFolder!!.folderName,
                        isBookmarked = selectedTopFolder!!.isBookmarked,
                        editStateViewModel = dummyVM,
                    ) { }
                }
                FolderState.LINKS -> {
                    BottomFolderItemLayout(
                        categoryColorStyle = categoryColorStyle?:CategoryColorStyle.categoryStyleList[0],
                        categoryName = selectedBottomFolder!!.folderName,
                        editStateViewModel = dummyVM,
                    )
                }
                else ->{
                    EmptyFolderItemLayout(
                        categoryName = userName
                    )
                }
            }

            Spacer(modifier = Modifier.height(45.59.dp))

            // 중분류 선택
            ShareFolderMenu(
                folderList = fileViewModel.parentFolders.collectAsState().value,
                selectable = true
            ) {
                selectedTopFolder = it
                bottomFolderList = fileViewModel.fetchSubfolders(parentFolderId = it.folderId)
                state = FolderState.BOTTOM
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 소분류 선택
            ShareFolderMenu(
                folderList = bottomFolderList,
                selectable = state == FolderState.BOTTOM,
                colorStyle = categoryColorStyle
            ) {
                selectedBottomFolder = it
                state = FolderState.LINKS
            }
        }
        Spacer(modifier = Modifier.height(43.dp))
    }
}

@Preview(showBackground = true, heightDp = 2000)
@Composable
private fun ShareBottomSheetPreview(){
    ShareBottomSheet(
        "세나의 폴더",
        viewModel(),
        viewModel()
    )
}