package com.example.file.ui.top.bar.component

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.file.ui.state.FolderStateViewModel
import com.example.file.ui.theme.Black
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.MainColor
import com.example.file.ui.theme.White

@Composable
fun TopFolderListMenu(
    folderStateViewModel: FolderStateViewModel,
    items: List<String>,
    onChangeFolder: () -> Unit
){
    DropdownMenu(
        modifier = Modifier
            .width(150.dp),
        shape = RoundedCornerShape(18.dp),
        offset = DpOffset(0.dp, 10.dp),
        expanded = folderStateViewModel.topMenuExpanded,
        onDismissRequest = { folderStateViewModel.updateTopMenuExpanded(false) },
        containerColor = White
    ) {
        var selectedText = remember { mutableStateOf(items[0]) }
        items.forEach{ selectedOption ->
            DropdownMenuItem(
                leadingIcon = if (selectedOption == selectedText.value){
                    @Composable
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier
                                .graphicsLayer(alpha = 0.99f) // 강제 레이어
                                .drawWithCache {
                                    onDrawWithContent {
                                        drawContent() // 기본 아이콘 먼저 그림
                                        drawRect(
                                            MainColor,
                                            blendMode = BlendMode.SrcAtop // 아이콘 영역만 그라데이션 입힘!
                                        )
                                    }
                                }
                        )
                    }
                }else null,
                text = {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    // 폰트 크기 (15sp)
                                    fontSize = 15.sp,

                                    // 사용할 폰트 (paperlogy 폰트)
                                    fontFamily = DefaultFont,

                                    // 폰트 굵기 (500)
                                    fontWeight = FontWeight(500),

                                    // 텍스트 그라데이션 색상(링큐 메인 색상)
                                    brush = if (selectedOption == selectedText.value) MainColor
                                    else Brush.horizontalGradient(listOf(Black, Black))
                                )
                            ) {
                                // 실제 표시할 텍스트
                                append(selectedOption)
                            }
                        },
                        lineHeight = 22.sp,
                    )
                },
                onClick = {
                    selectedText.value = selectedOption
                    onChangeFolder()
                }
            )
        }
    }
}

@Preview()
@Composable
fun FolderListMenuTest(){
    val folderStateViewModel: FolderStateViewModel = viewModel()
    TopFolderListMenu(
        folderStateViewModel = folderStateViewModel,
        listOf("나의 폴더", "공유받은 폴더"),
        {}
    )
}