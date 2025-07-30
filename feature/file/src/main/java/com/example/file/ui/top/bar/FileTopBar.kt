package com.example.file.ui.top.bar

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.design.BottomNavigationBar
import com.example.design.NavigationItem
import com.example.file.R
import com.example.file.modifier.noRippleClickable
import com.example.file.ui.bottom.sheet.FileBottomSheet
import com.example.file.ui.content.BottomFolderGrid
import com.example.file.ui.content.LinksGrid
import com.example.file.ui.content.TopFolderGrid
import com.example.file.ui.content.categories
import com.example.file.ui.state.EditStateViewModel
import com.example.file.ui.state.FolderState
import com.example.file.ui.state.FolderStateViewModel
import com.example.file.ui.theme.CategoryColorStyle
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.FileTopBarLinkUFont
import com.example.file.ui.theme.Gray300
import com.example.file.ui.theme.Gray400
import com.example.file.ui.theme.Gray600
import com.example.file.ui.theme.Gray800
import com.example.file.ui.theme.MainColor
import com.example.file.ui.theme.White
import com.example.file.ui.top.bar.component.BottomFolderListLayout
import com.example.file.ui.top.bar.component.BottomFolderListMenu
import com.example.file.ui.top.bar.component.EditButton
import com.example.file.ui.top.bar.component.FileSearchBar
import com.example.file.ui.top.bar.component.ShareButton
import com.example.file.ui.top.bar.component.TopFolderListLayout
import com.example.file.ui.top.bar.component.TopFolderListMenu
import com.example.file.ui.top.sheet.FileSearchBarTopSheet


@Composable
fun FileTopBar(
    onOpenTopSheet: () -> Unit,
    onOpenBottomSheet: () -> Unit,
    editStateViewModel: EditStateViewModel,
    folderStateViewModel: FolderStateViewModel,
    ) {
    // 내부 요소들을 겹쳐서 배치하는 Box
    Box(
        // 전체 영역을 가득 채우도록
        modifier = Modifier
            .fillMaxWidth()
            // 세로 길이 지정 (206dp)
            .height(206.dp)
            .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
            // Box 배경에 메인 그라데이션 적용
            .background(brush = MainColor)
    ) {

        // 1. 흐린 로고 (배경 맨 뒤)
        Icon(
            // 투명도 낮게 (alpha=0.2f)
            modifier = Modifier
                .alpha(0.2f)
                // 오른쪽 위에 정렬
                .align(Alignment.TopEnd)
                // 위쪽 여백 (80dp), 오른쪽으로 22.5dp 밀기
                .padding(top = 80.dp)
                .offset(x = 22.5.dp)
                // 아이콘 크기 지정 (149.49 x 106 dp)
                .size(width = 149.49561.dp, height = 106.dp),
            // 아이콘 색상 (흰색)
            tint = White,
            // 사용할 아이콘 이미지 리소스
            painter = painterResource(id = R.drawable.linku_logo),
            // 이미지 설명 ("링큐 투명 로고")
            contentDescription = "링큐 투명 로고"
        )

        // 2. 타이틀(링큐)
        Text(
            // 왼쪽 위에 정렬
            modifier = Modifier
                .align(Alignment.TopStart)
                // 왼쪽 35dp, 위 52dp 여백
                .padding(start = 35.dp, top = 52.dp),
            // 텍스트 내용 ("링큐")
            text = "링큐",
            // 폰트 크기 (24sp)
            fontSize = 24.sp,
            // 사용할 폰트
            fontFamily = FileTopBarLinkUFont,
            // 폰트 굵기 (Normal)
            fontWeight = FontWeight(400),
            // 글자색 (흰색)
            color = White,
        )

        // 3. 검색창 (FileSearchBar)
        Box(
            // 상단 중앙에 정렬
            modifier = Modifier
                .align(Alignment.TopCenter)
                // 위쪽 여백 (91dp)
                .padding(top = 91.dp)
                .noRippleClickable { onOpenTopSheet() },
        ) {
            // 커스텀 검색창
            FileSearchBar()
        }

        // 4. 폴더 리스트 레이아웃
        Box(
            // 왼쪽 위에 정렬
            modifier = Modifier
                .align(Alignment.TopStart)
                // 왼쪽 20dp, 위쪽 153dp 여백
                .padding(start = 20.dp, top = 153.dp)
                .noRippleClickable {
                    folderStateViewModel.updateTopMenuExpanded(true)
                },
        ) {
            // 폴더 리스트 컴포저블
            TopFolderListLayout()

            TopFolderListMenu(
                folderStateViewModel = folderStateViewModel,
                listOf("나의 폴더", "공유받은 폴더"),
                {}
            )

        }

        if(folderStateViewModel.currentFolderState != FolderState.TOP){
            Box(
                // 왼쪽 위에 정렬
                modifier = Modifier
                    .align(Alignment.TopStart)
                    // 왼쪽 20dp, 위쪽 153dp 여백
                    .padding(start = 139.29.dp, top = 153.dp)
                    .noRippleClickable {
                        folderStateViewModel.updateBottomMenuExpanded(true)
                   },
            ) {
                // 폴더 리스트 컴포저블
                BottomFolderListLayout(
                    colorStyle = CategoryColorStyle.categoryStyleList[0],
                    folderStateViewModel = folderStateViewModel
                )

                BottomFolderListMenu(
                    folderStateViewModel = folderStateViewModel,
                    items = categories,
                    onChangeFolder = {}
                )
            }
        }

        // 5. 알람 아이콘 (오른쪽 위)
        Icon(
            // 오른쪽 위에 정렬
            modifier = Modifier
                .align(Alignment.TopEnd)
                // 오른쪽 29.8dp, 위 50.38dp 여백
                .padding(end = 29.8.dp, top = 50.38.dp)
                // 아이콘 크기 (22.26 x 27.18 dp)
                .size(width = 22.25668.dp, height = 27.17871.dp),
            // 아이콘 색상 (흰색)
            tint = White,
            // 사용할 아이콘 이미지 리소스
            painter = painterResource(id = com.example.design.R.drawable.ic_alarm),
            // 이미지 설명 ("알람")
            contentDescription = "알람",
        )

        // 6. 수정 버튼 (오른쪽 아래)
        Box(
            // 오른쪽 위에 정렬(실제 위치는 아래임)
            modifier = Modifier
                .align(Alignment.TopEnd)
                // 오른쪽 30dp, 위 165dp 여백
                .padding(end = 30.dp, top = 165.dp)
                //.noRippleClickable { onOpenBottomSheet() },
        ) {
            // 수정 버튼 컴포저블
            EditButton(
                editStateViewModel = editStateViewModel,
                folderViewModel = folderStateViewModel
            )
        }
    }

}


@Preview(
    name = "Pixel 8 Size",
    widthDp = 412,
    heightDp = 915,
    showBackground = true)
@Composable
fun FileTopBarTest() {

    var isTopSheetVisible by remember { mutableStateOf(false) }
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    val editStateViewModel:EditStateViewModel = viewModel()

    val folderStateViewModel: FolderStateViewModel = viewModel()

    // 뒤로가기 핸들러
    BackHandler(enabled = folderStateViewModel.currentFolderState != FolderState.TOP) {
        editStateViewModel.updateEditMode(false)
        when (folderStateViewModel.currentFolderState) {
            FolderState.LINK -> {
                folderStateViewModel.updateFolderState(FolderState.BOTTOM)
                folderStateViewModel.updateSelectedBottomFolder(null)
            }
            FolderState.BOTTOM -> {
                folderStateViewModel.updateFolderState(FolderState.TOP)
                folderStateViewModel.updateSelectedTopFolder(null)
            }
            else -> {}
        }
    }

    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable {  },
        containerColor = White,
        topBar = {
            FileTopBar(
                onOpenTopSheet = { isTopSheetVisible = true },
                onOpenBottomSheet = { isBottomSheetVisible = true },
                editStateViewModel = editStateViewModel,
                folderStateViewModel = folderStateViewModel
        )},
        bottomBar = { BottomNavigationBar(
            selectedTab = NavigationItem.FILE,
            onTabSelected = {},
            onFabClick = {}
        ) }
    ){ innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val itemList = listOf(
                listOf("태그1", "태그2"),
                listOf("태그2", "태그3"),
                listOf("태그3", "태그4")
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(20.dp)
            ) {
                item {
                    when(folderStateViewModel.currentFolderState) {
                        FolderState.TOP -> {
                            TopFolderGrid { folderName ->
                                folderStateViewModel.updateSelectedTopFolder(folderName)
                                folderStateViewModel.updateFolderState(FolderState.BOTTOM)
                            }
                        }
                        FolderState.BOTTOM -> {
                            BottomFolderGrid(
                                folderList = listOf("나의 폴더", "공유받은 폴더"), // 실제 폴더 데이터로
                                linkList = listOf("링크1", "링크2"),
                                editStateViewModel = editStateViewModel
                            ) { bottomFolderName ->
                                folderStateViewModel.updateSelectedBottomFolder(bottomFolderName)
                                folderStateViewModel.updateFolderState(FolderState.LINK)
                            }
                        }
                        FolderState.LINK -> {
                            LinksGrid(
                                linkList = listOf("링크1", "링크2", "링크3")
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 19.dp, bottom = 8.dp)
            ) {
                ShareButton()
            }
        }

        if (
            folderStateViewModel.topMenuExpanded ||
            folderStateViewModel.bottomMenuExpanded
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
        }
    }

    FileSearchBarTopSheet(
        visible = isTopSheetVisible,
        onDismiss = { isTopSheetVisible = false }
    )
    FileBottomSheet(
        title = "해당 카테고리를 수정하시겠습니까?",
        body = "새 카테고리명을 입력하고 대표 색상을 지정해주세요!",
        buttonText = "저장",
        visible = isBottomSheetVisible,
        onDismiss = { isBottomSheetVisible = false }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(1.dp, MainColor, RoundedCornerShape(18.dp))
                .padding(horizontal = 21.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var text by remember { mutableStateOf("") }
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Gray400,
                    fontFamily = DefaultFont,
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 입력값이 비어 있으면 placeholder 보여줌
                        if (text.isEmpty()) {
                            Text(
                                text = "카테고리명은 최대 10자입니다", // placeholder
                                color = Gray400,
                                fontSize = 14.sp,
                                fontFamily = DefaultFont,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                        innerTextField() // 실제 입력란
                    }
                }
            )

        }
        Spacer(modifier = Modifier.height(19.dp))
        if (true) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "색상",
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontFamily = DefaultFont,
                    fontWeight = FontWeight.Normal,
                    color = Gray800,
                )
                Text(
                    modifier = Modifier
                        .padding(start = 1.dp),
                    text = "(색상은 한 번 지정하면 변경 불가합니다)",
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontFamily = DefaultFont,
                    fontWeight = FontWeight.Normal,
                    color = Gray400,
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    modifier = Modifier
                        .size(25.dp),
                    color = Gray300,
                    shape = CircleShape
                ) { }
                Icon(
                    modifier = Modifier
                        .padding(start = 10.dp),
                    tint = Gray600,
                    painter = painterResource(id = R.drawable.check_img),
                    contentDescription = "아래 화살표"
                )
            }
        }
    }
}