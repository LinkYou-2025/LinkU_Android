package com.linku.file.ui.top.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.linku.core.model.ParentFolderSort
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.font.Taebaek
import com.linku.design.theme.linkuColors
import com.linku.file.FileViewModel
import com.linku.file.R
import com.linku.file.ui.top.bar.component.FileSearchBar
import com.linku.file.ui.top.bar.component.FolderScopeListLayout
import com.linku.file.ui.top.bar.component.FolderScopeListMenu
import com.linku.file.ui.top.bar.component.MyListLayout
import com.linku.file.ui.top.bar.component.MyListMenu
import com.linku.file.ui.top.bar.component.ParentFolderSortMenu
import com.linku.file.viewmodel.folder.state.FolderState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

/**
 * 파일 화면의 로고, 검색, 폴더 범위 선택과 상위 폴더 정렬 액션을 표시합니다.
 *
 * @param fileViewModel 폴더 범위 메뉴에서 목록을 조회하는 파일 화면 ViewModel입니다.
 * @param folderStateViewModel 현재 폴더 단계와 상단 메뉴 상태를 관리하는 ViewModel입니다.
 * @param parentFolderSort 현재 기기에 적용된 상위 폴더 정렬 기준입니다.
 * @param onParentFolderSortSelected 사용자가 정렬 기준을 선택했을 때 호출되는 콜백입니다.
 * @param onSearchClick 빠른 링크 검색 영역을 눌렀을 때 호출되는 콜백입니다.
 */
@Composable
fun FileTopBar(
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    parentFolderSort: ParentFolderSort,
    onParentFolderSortSelected: (ParentFolderSort) -> Unit,
    onSearchClick: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors
    val density = LocalDensity.current
    var sortMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var myListAnchorWidth by remember { mutableStateOf(0.dp) }
    val showParentFolderSort =
        folderStateViewModel.currentFolderState == FolderState.TOP &&
            !folderStateViewModel.isSharedFolders

    LaunchedEffect(
        showParentFolderSort,
        folderStateViewModel.topMenuExpanded,
        folderStateViewModel.bottomMenuExpanded,
    ) {
        if (
            !showParentFolderSort ||
            folderStateViewModel.topMenuExpanded ||
            folderStateViewModel.bottomMenuExpanded
        ) {
            sortMenuExpanded = false
        }
    }

    // 내부 요소들을 겹쳐서 배치하는 Box
    Box(
        // 전체 영역을 가득 채우도록
        modifier = Modifier
            .fillMaxWidth()
            // 세로 길이 지정 (206dp)
            .height(206.dp)
            .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
            // Box 배경에 메인 그라데이션 적용
            .background(brush = colors.maincolor)
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
            tint = colors.white,
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
            fontFamily = Taebaek.font,
            // 폰트 굵기 (Normal)
            fontWeight = FontWeight(400),
            // 글자색 (흰색)
            color = colors.white,
        )

        // 3. 검색창 (FileSearchBar)
        Box(
            // 상단 중앙에 정렬
            modifier = Modifier
                .align(Alignment.TopCenter)
                // 위쪽 여백 (91dp)
                .padding(top = 91.dp, start = 16.dp, end = 16.dp)
                .noRippleClickable(onClick = onSearchClick),
        ) {
            // 커스텀 검색창
            FileSearchBar()
        }

        // 4. 폴더 리스트 레이아웃
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                // 왼쪽 20dp, 위쪽 153dp 여백
                .padding(start = 20.dp, top = 153.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ){
            Box(
                // 왼쪽 위에 정렬
                modifier = Modifier
                    .noRippleClickable {
                        folderStateViewModel.updateTopMenuExpanded(true)
                    },
            ) {
                // 폴더 리스트 컴포저블
                FolderScopeListLayout(
                    folderStateViewModel = folderStateViewModel
                )

                FolderScopeListMenu(
                    folderStateViewModel = folderStateViewModel,
                    fileViewModel = fileViewModel
                )

            }

            if (folderStateViewModel.currentFolderState != FolderState.TOP) {
                Box(
                    // 왼쪽 위에 정렬
                    modifier = Modifier
                        .onSizeChanged { size ->
                            myListAnchorWidth = with(density) { size.width.toDp() }
                        }
                        .noRippleClickable {
                            folderStateViewModel.updateBottomMenuExpanded(true)
                        },
                ) {
                    // 폴더 리스트 컴포저블
                    MyListLayout(
                        fileViewModel = fileViewModel,
                        folderStateViewModel = folderStateViewModel
                    )

                    MyListMenu(
                        fileViewModel = fileViewModel,
                        folderStateViewModel = folderStateViewModel,
                        parentFolderAnchorWidth = myListAnchorWidth,
                        onChangeFolder = {}
                    )
                }
            }
        }

        // 5. 알람 아이콘 (오른쪽 위)
        // 다인누나의 요구사항에 맞춰 제거함.
//        Icon(
//            // 오른쪽 위에 정렬
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                // 오른쪽 29.8dp, 위 50.38dp 여백
//                .padding(end = 29.8.dp, top = 50.38.dp)
//                // 아이콘 크기 (22.26 x 27.18 dp)
//                .size(width = 22.25668.dp, height = 27.17871.dp),
//            // 아이콘 색상 (흰색)
//            tint = colors.white,
//            // 사용할 아이콘 이미지 리소스
//            painter = painterResource(id = com.linku.design.R.drawable.ic_alarm),
//            // 이미지 설명 ("알람")
//            contentDescription = "알람",
//        )

        // 6. 상위 폴더 정렬 메뉴 (오른쪽 아래)
        if (showParentFolderSort) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 20.dp, top = 153.dp),
            ) {
                ParentFolderSortMenu(
                    selectedSort = parentFolderSort,
                    expanded = sortMenuExpanded,
                    onExpandedChange = { expanded ->
                        if (expanded) {
                            folderStateViewModel.updateTopMenuExpanded(false)
                            folderStateViewModel.updateBottomMenuExpanded(false)
                        }
                        sortMenuExpanded = expanded
                    },
                    onSortSelected = onParentFolderSortSelected,
                )
            }
        }
    }

}


@Preview(
    name = "Pixel 8 Size",
    widthDp = 412,
    heightDp = 915,
    showBackground = true)
@Composable
private fun FileTopBarTest() {
    FileTopBar(
        viewModel(),
        viewModel(),
        ParentFolderSort.NAME,
        {},
        {},
    )
}
