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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.FolderSimpleInfo
import com.example.design.modifier.noRippleClickable
import com.example.design.theme.ThemeProvider
import com.example.design.theme.color.CategoryColorStyle
import com.example.design.theme.linkuColors
import com.example.design.util.OuterShadowResourceImage
import com.example.file.R

/*--------------피그마 상 화면과 요소 간 비율 및 상수--------------*/
// 전체 화면 높이
private const val FULL_HEIGHT = 917f

// 바텀 시트 크기 상수
private const val SHARE_BOTTOM_SHEET_HEIGHT = 850f
private const val SHARE_BOTTOM_SHEET_WIDTH = 412f

// 바텀 시트 높이 비율
private const val SHARE_BOTTOM_SHEET_HEIGHT_RATIO = SHARE_BOTTOM_SHEET_HEIGHT / FULL_HEIGHT

// 폴더 모형 크기 상수
private const val FOLDER_STRUCTURE_HEIGHT = 154.04047f
private const val FOLDER_STRUCTURE_WIDTH = 174f

// 폴더 모형 비율
private const val FOLDER_STRUCTURE_WIDTH_RATIO = FOLDER_STRUCTURE_WIDTH / SHARE_BOTTOM_SHEET_WIDTH

// 폴더 내 원 크기 상수
private const val CIRCLE_SIZE_IN_FOLDER = 30.70588f

// 폴더 상하 여백 높이 상수
private const val FOLDER_TOP_MARGIN_RATIO = 45f / SHARE_BOTTOM_SHEET_HEIGHT
private const val FOLDER_BOTTOM_MARGIN_RATIO = 29.96f / SHARE_BOTTOM_SHEET_HEIGHT

// 폴더 공유 메뉴 크기 상수
private const val SHARE_FOLDER_MENU_WIDTH = 372f
// 폴더 공유 메뉴 크기 비율
private const val SHARE_FOLDER_MENU_HEIGHT_RATIO = 52f / SHARE_BOTTOM_SHEET_HEIGHT
private const val SHARE_FOLDER_MENU_WIDTH_RATIO = SHARE_FOLDER_MENU_WIDTH / SHARE_BOTTOM_SHEET_WIDTH


// 폴더 공유 메뉴바 오른쪽 화살표 이미지 크기 비율
private const val SHARE_FOLDER_MENU_ARROW_WIDTH_RATIO = 7f / SHARE_FOLDER_MENU_WIDTH

/*--------------피그마 상 화면과 요소 간 비율 및 상수 --------------*/


// 선택된 폴더 계층 및 선택된 폴더 정보
private sealed class SelectedState {
    object None : SelectedState()
    data class Category(val category: CategoryColorStyle) : SelectedState()
    data class Folder(val category: CategoryColorStyle, val folder: FolderSimpleInfo) : SelectedState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun _ShareBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
){
    
    val colors = MaterialTheme.linkuColors

    // 폴더 모형 추상화
    @Composable
    fun FolderStructure(modifier: Modifier){

        // 폴더 모형 레이아웃
        Box(
            modifier = modifier
        ){
            Image(
                contentDescription = "폴더 배경 레이어",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.img_shared_bottom_sheet_folder_background),
            )

            // TODO: 그림자 해결
            val bitmap = ImageBitmap.imageResource(id = R.drawable.img_shared_bottom_sheet_folder_mask)
            OuterShadowResourceImage(
                resId = R.drawable.img_shared_bottom_sheet_folder_mask,
                contentDescription = "폴더 앞 부분 레이어",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )

            /*PngImageWithShadow(
                contentDescription = "폴더 앞 부분 레이어",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                painter = painterResource(id = R.drawable.img_shared_bottom_sheet_folder_mask)
            )*/

            Row(
                modifier = Modifier
                    .padding(start = 18.42.dp, bottom = 18.93.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.87.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(CIRCLE_SIZE_IN_FOLDER.dp)
                        .background(
                            shape = CircleShape,
                            color = colors.gray[400]
                        )
                )

                Box(
                    modifier = Modifier
                        .width(58.dp)
                        .height(21.dp)
                        .background(
                            shape = RoundedCornerShape(size = 8.dp),
                            color = colors.gray[300]
                        )
                )
            }
        }
    }

    val selectedState = remember { mutableStateOf<SelectedState>(SelectedState.None) }
    val menuText = remember { mutableStateOf(
        buildAnnotatedString {
            when (val state = selectedState.value) {
                is SelectedState.None -> {
                    withStyle(SpanStyle(colors.gray[400])) {
                        append("공유하실 폴더를 선택해주세요.")
                    }
                }
                is SelectedState.Category -> {
                    // TODO: 카테고리 구조 기억해내라
                    /*append(state.category.name)*/
                }
                is SelectedState.Folder -> {
                    withStyle(SpanStyle(colors.gray[400])){
                        // TODO: 카테고리 구조 기억해내라
                        /*append(state.category.name + " > ")*/
                    }
                    append(state.folder.folderName)
                }
            }
        }
    ) }

    // 폴더 공유 메뉴 추상화
    @Composable
    fun ShareFolderMenu(
        modifier: Modifier = Modifier,
        //folderTree: List<FolderSimpleInfo>,
        selectable: Boolean,
        onClick: (FolderSimpleInfo) -> Unit
    ){
        var expanded by remember { mutableStateOf(false) }
        val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "")

        Box(
            modifier = modifier
                .fillMaxWidth(SHARE_FOLDER_MENU_WIDTH_RATIO)
                .fillMaxHeight(SHARE_FOLDER_MENU_HEIGHT_RATIO)
        ){
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        color = colors.gray[200],
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 22.dp)
                    .noRippleClickable {
                        if (selectable) {
                            expanded = true
                        }
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = menuText.value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400)
                )

                Image(
                    contentDescription = "메뉴 오른쪽 회전하는 화살표 이미지",
                    painter = painterResource(R.drawable.check_img),
                    modifier = Modifier
                        .fillMaxWidth(SHARE_FOLDER_MENU_ARROW_WIDTH_RATIO)
                        /* 메인 그라데이션 지정하는 코드. 피그마 완성안에 따를 예정.
                        .then(
                            if (expanded) Modifier
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
                                } else Modifier
                        )*/
                        .rotate(rotation)
                )
            }

            // UI 수정 전 사용하던 코드 차용
            /*DropdownMenu(
                modifier = Modifier
                    .width(300.dp)
                    .heightIn(max = 224.dp),
                shape = RoundedCornerShape(18.dp),
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                    menuOpen = false
                },
                containerColor = White
            ) {
                for ((i, folder) in folderList.withIndex()) {
                    val categoryColorStyle =
                        fileViewModel.categoryColorMap.collectAsState().value[folder.folderName]

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
                                        shape = CircleShape
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
                            menuOpen = false
                        }
                    )
                }
            }*/
        }
    }

    // 공유 링크 레이어 추상화
    @Composable
    fun ShareLink(){
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // 바로 Expanded 상태
    )

    ModalBottomSheet(
        modifier = modifier
            .fillMaxWidth(),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                width = 40.dp,
                color = colors.gray[300]
            )
        },
        containerColor = colors.white,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(SHARE_BOTTOM_SHEET_HEIGHT_RATIO),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text(
                text = "폴더 공유하기",
                fontSize = 16.sp,
                fontWeight = FontWeight(500)
            )

            Spacer(modifier = Modifier.fillMaxHeight(FOLDER_TOP_MARGIN_RATIO))

            FolderStructure(
                modifier = Modifier
                    .fillMaxWidth(FOLDER_STRUCTURE_WIDTH_RATIO)
            )

            Spacer(modifier = Modifier.fillMaxHeight(FOLDER_BOTTOM_MARGIN_RATIO))

            Text(
                modifier = Modifier
                    .padding(start = 24.dp)
                    .align(Alignment.Start),
                text = "폴더 이름",
                fontSize = 15.sp,
                fontWeight = FontWeight(500)
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 2000)
@Composable
private fun _ShareBottomSheetPreview() {
    ThemeProvider(){
        _ShareBottomSheet(
        ) {}
    }
}