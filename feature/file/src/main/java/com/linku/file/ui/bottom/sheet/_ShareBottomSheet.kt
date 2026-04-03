package com.linku.file.ui.bottom.sheet

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.FolderSimpleInfo
import com.linku.design.modifier.noRippleClickable
import com.linku.design.modifier.skeleton
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.color.ThemeColorScheme
import com.linku.design.theme.linkuColors
import com.linku.design.util.OuterShadowResourceImage
import com.linku.file.R

// 전체 화면 높이
private const val FULL_HEIGHT = 917f

/*--------------바텀 시트 메인 화면 요소 간 비율 및 상수 --------------*/

// 바텀 시트 메인 화면 크기 상수
private const val SHEET_MAIN_SCREEN_HEIGHT = 850f   // 바텀 시트 메인 화면 높이
private const val SHEET_MAIN_SCREEN_WIDTH = 412f    // 바텀 시트 메인 화면 너비

private const val SHEET_MAIN_SCREEN_HEIGHT_RATIO = SHEET_MAIN_SCREEN_HEIGHT / FULL_HEIGHT   // 바텀 시트 메인 화면 높이 비율

// 폴더 모형 크기 상수
private const val FOLDER_STRUCTURE_HEIGHT = 154.04047f  // 폴더 모형 높이
private const val FOLDER_STRUCTURE_WIDTH = 174f     // 폴더 모형 너비

private const val FOLDER_STRUCTURE_WIDTH_RATIO = FOLDER_STRUCTURE_WIDTH / SHEET_MAIN_SCREEN_WIDTH   // 폴더 모형 너비 비율

private const val CIRCLE_SIZE_IN_FOLDER = 30.70588f     // 폴더 내 원 크기 상수

private const val FOLDER_TOP_MARGIN_RATIO = 45f / SHEET_MAIN_SCREEN_HEIGHT  // 폴더 위 여백 비율
private const val FOLDER_BOTTOM_MARGIN_RATIO = 29.96f / SHEET_MAIN_SCREEN_HEIGHT    // 폴더 아래 여백 비율

// 폴더 공유 메뉴 크기 상수
private const val SHARE_FOLDER_MENU_WIDTH = 372f    // 폴더 공유 메뉴 너비

private const val SHARE_FOLDER_MENU_HEIGHT_RATIO = 52f / SHEET_MAIN_SCREEN_HEIGHT   // 폴더 공유 메뉴 높이 비율
private const val SHARE_FOLDER_MENU_WIDTH_RATIO = SHARE_FOLDER_MENU_WIDTH / SHEET_MAIN_SCREEN_WIDTH // 폴더 공유 메뉴 너비 비율

private const val SHARE_FOLDER_MENU_ARROW_WIDTH_RATIO = 7f / SHARE_FOLDER_MENU_WIDTH    // 폴더 공유 메뉴바 오른쪽 화살표 이미지 크기 비율

private const val SHARE_FOLDER_MENU_TOP_MARGIN_RATIO = 14f / SHEET_MAIN_SCREEN_HEIGHT   // 폴더 공유 메뉴 위 여백
private const val SHARE_FOLDER_MENU_BOTTOM_MARGIN_RATIO = 10f / SHEET_MAIN_SCREEN_HEIGHT    // 폴더 공유 메뉴 아래 여백

// 공유 링크 크기 상수
private const val SHARE_LINK_HEIGHT = 52f    // 공유 링크 높이
private const val SHARE_LINK_WIDTH = 372f    // 공유 링크 너비

private const val SHARE_LINK_HEIGHT_RATIO = SHARE_LINK_HEIGHT / SHEET_MAIN_SCREEN_HEIGHT    // 공유 링크 높이 비율
private const val SHARE_LINK_WIDTH_RATIO = SHARE_LINK_WIDTH / SHEET_MAIN_SCREEN_WIDTH   // 공유 링크 너비 비율

// 하단 버튼 크기 상수
private const val BUTTON_HEIGHT = 50f    // 하단 버튼 높이
private const val BUTTON_WIDTH = 372f    // 하단 버튼 너비

private const val BUTTON_HEIGHT_RATIO = BUTTON_HEIGHT / SHEET_MAIN_SCREEN_HEIGHT    // 하단 버튼 높이 비율
private const val BUTTON_WIDTH_RATIO = BUTTON_WIDTH / SHEET_MAIN_SCREEN_WIDTH   // 하단 버튼 너비 비율

private const val SMALL_BUTTON_WIDTH = 181f     // 작은 버튼 너비

private const val SMALL_BUTTON_WIDTH_RATIO = SMALL_BUTTON_WIDTH / BUTTON_WIDTH   // 작은 버튼 너비 비율

private const val COPY_ICON_WIDTH_RATIO = 19.00195f / SMALL_BUTTON_WIDTH  // 복사 아이콘 너비 비율

private const val SHARE_ICON_WIDTH_RATIO = 19.00195f / SMALL_BUTTON_WIDTH  // 공유 아이콘 너비 비율

/*--------------바텀 시트 메인 화면 요소 간 비율 및 상수 --------------*/

/*--------------바텀 시트 폴더 선택 화면 요소 간 비율 및 상수 --------------*/

// 바텀 시트 메인 화면 크기 상수
private const val SHEET_Select_SCREEN_HEIGHT = 598f // 바텀 시트 메인 화면 높이
private const val SHEET_Select_SCREEN_WIDTH = 412f  // 바텀 시트 메인 화면 너비

private const val SHEET_SELECT_SCREEN_HEIGHT_RATIO = SHEET_Select_SCREEN_HEIGHT / FULL_HEIGHT   // 바텀 시트 메인 화면 높이 비율

/*--------------바텀 시트 폴더 선택 화면 요소 간 비율 및 상수 --------------*/


// 선택된 폴더 계층 및 선택된 폴더 정보
private sealed class SelectedState {
    object None : SelectedState()
    data class Category(val category: CategoryColorStyle) : SelectedState()
    data class Folder(val category: CategoryColorStyle, val folder: FolderSimpleInfo) : SelectedState()
}

private enum class ScreenState { Main, Select }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun _ShareBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
){
    val colors = MaterialTheme.linkuColors

    val selectedState = remember { mutableStateOf<SelectedState>(SelectedState.None) }
    val menuText = remember(selectedState.value) { mutableStateOf(
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

    val screenState = remember { mutableStateOf(ScreenState.Main) }

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
        onDismissRequest = onDismissRequest
    ) {
        when (screenState.value) {
            ScreenState.Main -> ShareBottomSheetMainScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(SHEET_MAIN_SCREEN_HEIGHT_RATIO),
                colors = colors,
                menuText = menuText.value,
                isGenerateable = selectedState.value is SelectedState.Folder,
                onMenuClick = {
                    screenState.value = ScreenState.Select
                },
                onLinkGenerate = {

                }
            )

            ScreenState.Select -> SelectFolderToShareScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(SHEET_SELECT_SCREEN_HEIGHT_RATIO),
                colors = colors
            )
        }
    }
}

// 폴더 공유 바텀 시트 메인 스크린
@Composable
private fun ShareBottomSheetMainScreen(
    modifier: Modifier,
    colors: ThemeColorScheme,
    menuText: AnnotatedString,
    isGenerateable: Boolean,
    onMenuClick: () -> Unit,
    onLinkGenerate: () -> Unit
){
    val link = remember { mutableStateOf("") }
    val clickedGenerate = remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
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
                .fillMaxWidth(FOLDER_STRUCTURE_WIDTH_RATIO),
            colors = colors
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

        Spacer(modifier = Modifier.fillMaxHeight(SHARE_FOLDER_MENU_TOP_MARGIN_RATIO))

        ShareFolderMenu(
            modifier = Modifier
                .fillMaxWidth(SHARE_FOLDER_MENU_WIDTH_RATIO)
                .fillMaxHeight(SHARE_FOLDER_MENU_HEIGHT_RATIO),
            colors = colors,
            menuText = menuText,
            selectable = true,  // 선택 상태 분기 가능성 확인하기
            onClick = onMenuClick
        )

        Spacer(modifier = Modifier.fillMaxHeight(SHARE_FOLDER_MENU_BOTTOM_MARGIN_RATIO))

        if (clickedGenerate.value) {
            ShareLink(
                modifier = Modifier
                    .fillMaxWidth(SHARE_LINK_WIDTH_RATIO)
                    .fillMaxHeight(SHARE_LINK_HEIGHT_RATIO),
                colors = colors,
                link = link.value
            )

            if (link.value.isNotEmpty()) {
                // 링크 복사 & 공유 버튼 레이아웃
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter as Alignment.Horizontal)
                        .fillMaxWidth(BUTTON_WIDTH_RATIO)
                        .fillMaxHeight(BUTTON_HEIGHT_RATIO),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    // 링크 복사 버튼
                    Row(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = colors.gray[300],
                                shape = RoundedCornerShape(size = 18.dp)
                            )
                            .fillMaxHeight()
                            .fillMaxWidth(SMALL_BUTTON_WIDTH_RATIO)
                            .noRippleClickable { },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            contentDescription = "링크 복사 이미지",
                            modifier = Modifier
                                .fillMaxWidth(COPY_ICON_WIDTH_RATIO),
                            painter = painterResource(R.drawable.icon_copy),
                            colorFilter = ColorFilter.tint(colors.gray[800])
                        )

                        Text(
                            text = "링크 복사",
                            fontSize = 16.sp,
                            color = colors.gray[800]
                        )
                    }

                    // 링크 공유 버튼
                    Row(
                        modifier = Modifier
                            .background(
                                brush = colors.maincolor,
                                shape = RoundedCornerShape(size = 18.dp)
                            )
                            .fillMaxHeight()
                            .fillMaxWidth(SMALL_BUTTON_WIDTH_RATIO)
                            .noRippleClickable { },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            contentDescription = "링크 공유 이미지",
                            modifier = Modifier
                                .fillMaxWidth(SHARE_ICON_WIDTH_RATIO),
                            painter = painterResource(R.drawable.icon_copy),
                            colorFilter = ColorFilter.tint(colors.white)
                        )

                        Text(
                            text = "링크 공유",
                            fontSize = 16.sp,
                            color = colors.white
                        )
                    }
                }
            }
        } else {
            // 링크 생성 버튼
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter as Alignment.Horizontal)
                    .fillMaxWidth(BUTTON_WIDTH_RATIO)
                    .fillMaxHeight(BUTTON_HEIGHT_RATIO)
                    .then(
                        if(isGenerateable){
                            Modifier
                                .background(
                                    brush = colors.maincolor,
                                    shape = RoundedCornerShape(size = 18.dp)
                                )
                                .noRippleClickable {
                                    clickedGenerate.value = true
                                    onLinkGenerate()
                                }
                        }else{
                            Modifier.background(
                                color = colors.gray[300],
                                shape = RoundedCornerShape(size = 18.dp)
                            )
                        }
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "공유 링크 생성",
                    fontSize = 16.sp,
                    color = colors.white
                )
            }
        }
    }
}

// 공유할 폴더 선택 스크린
@Composable
private inline fun SelectFolderToShareScreen(
    modifier: Modifier,
    colors: ThemeColorScheme,
){

}

// 폴더 모형
@Composable
private inline fun FolderStructure(
    modifier: Modifier,
    colors: ThemeColorScheme,
){
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
        //val bitmap = ImageBitmap.imageResource(id = R.drawable.img_shared_bottom_sheet_folder_mask)
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

// 폴더 공유 메뉴바
@Composable
private inline fun ShareFolderMenu(
    modifier: Modifier,
    colors: ThemeColorScheme,
    menuText: AnnotatedString,
    selectable: Boolean,
    crossinline onClick: () -> Unit
){
    Box(
        modifier = modifier
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
                .noRippleClickable { onClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = menuText,
                fontSize = 14.sp,
                fontWeight = FontWeight(400)
            )

            Image(
                contentDescription = "메뉴 오른쪽 화살표 이미지",
                painter = painterResource(R.drawable.img_right_arrow),
                modifier = Modifier
                    .fillMaxWidth(SHARE_FOLDER_MENU_ARROW_WIDTH_RATIO),
                colorFilter = ColorFilter.tint(colors.gray[600])
            )
        }
    }
}

// 공유 링크 텍스트 바
@Composable
private inline fun ShareLink(
    modifier: Modifier,
    colors: ThemeColorScheme,
    link: String

){
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = colors.gray[200],
                shape = RoundedCornerShape(18.dp)
            )
            .background(colors.gray[100]),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .clip(RoundedCornerShape(18.dp))
                .skeleton(isLoading = link.isEmpty()),
            text = link,
            fontSize = 15.sp,
            fontWeight = FontWeight(400),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true/*, heightDp = 2000*/)
@Composable
private fun ShareBottomSheetMainScreenPreview() {
    val colors = MaterialTheme.linkuColors

    val selectedState = remember { mutableStateOf<SelectedState>(SelectedState.None) }
    val menuText = remember(selectedState.value) {mutableStateOf(
        buildAnnotatedString {append("fdjzjfjfjsdflkjk")}
    )}
    /*val menuText = remember(selectedState.value) { mutableStateOf(
        buildAnnotatedString {
            when (val state = selectedState.value) {
                is SelectedState.None -> {
                    withStyle(SpanStyle(colors.gray[400])) {
                        append("공유하실 폴더를 선택해주세요.")
                    }
                }
                is SelectedState.Category -> {
                    // TODO: 카테고리 구조 기억해내라
                    *//*append(state.category.name)*//*
                }
                is SelectedState.Folder -> {
                    withStyle(SpanStyle(colors.gray[400])){
                        // TODO: 카테고리 구조 기억해내라
                        *//*append(state.category.name + " > ")*//*
                    }
                    append(state.folder.folderName)
                }
            }
        }
    ) }*/

    ThemeProvider(){
        ShareBottomSheetMainScreen(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(SHEET_MAIN_SCREEN_HEIGHT_RATIO),
            colors = MaterialTheme.linkuColors,
            menuText = menuText.value,
            isGenerateable = true,
            onMenuClick = { },
            onLinkGenerate = { }
        )
    }
}

@Preview(showBackground = true/*, heightDp = 2000*/)
@Composable
private fun SelectFolderToShareScreenPreview() {
    val colors = MaterialTheme.linkuColors

    ThemeProvider(){
        SelectFolderToShareScreen(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(SHEET_SELECT_SCREEN_HEIGHT_RATIO),
            colors = colors
        )
    }
}
