package com.linku.navigation

import androidx.compose.runtime.Composable
import com.linku.CenterButtonProp
import com.linku.MainScreen
import com.linku.NavigationBarProp
import com.linku.file.viewmodel.folder.state.FileNavigationState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import com.linku.search.SearchOverlay
import com.linku.search.SearchViewModel

/**
 * [AppState]와 앱 전역 상태를 [MainScreen]의 UI 속성으로 옮겨 연결하는 앱 셸입니다.
 *
 * 하단 내비게이션 바와 시스템 바 정책, 파일 탭의 딤 처리, 검색 오버레이를 담당하고 실제 화면 영역은
 * [content]로 받습니다. 내비게이션 그래프는 이 셸 안에 놓입니다.
 *
 * @param appState 내비게이션과 셸 UI 상태를 제공하는 [AppState]
 * @param folderStateViewModel 파일 탭의 하단 메뉴 상태를 제공하는 [FolderStateViewModel]
 * @param searchViewModel 검색 오버레이 상태를 제공하는 [SearchViewModel]
 * @param content 셸 안에 그릴 화면 영역
 */
@Composable
internal fun AppShell(
    appState: AppState,
    folderStateViewModel: FolderStateViewModel,
    searchViewModel: SearchViewModel,
    content: @Composable () -> Unit,
) {
    val currentTab = appState.currentTab
    val isFileTab = currentTab == LinkuNavigationItem.FILE
    val shouldDimMyListMenu = isFileTab &&
        folderStateViewModel.bottomMenuExpanded &&
        (folderStateViewModel.navigationState is FileNavigationState.PersonalBottom ||
            folderStateViewModel.navigationState is FileNavigationState.PersonalLinks)

    MainScreen(
        navigationBarProp = if (appState.shouldShowNavigationBar) NavigationBarProp(
            currentLinkuNavigationItem = currentTab,
            onNavigate = { item ->
                // 다른 탭을 선택한 경우에만 탭 내비게이션을 수행한다.
                if (currentTab != item) {
                    appState.navigateToTab(item)
                } else if (item == LinkuNavigationItem.FILE) {
                    // MainApp 범위 상태는 같은 탭 재선택만으로 초기화되지 않으므로 카테고리 루트로 되돌린다.
                    folderStateViewModel.resetSharedFolderState()
                }
            },
            onCenterButtonClicked = {
                // 여기에 중앙 버튼 눌렀을 때 로직 넣기
                appState.requestSaveLink()  // SaveLinkScreen으로 진입
            }
        ) else null,
        centerButtonProp = null, // 바로 이동하므로 null
        onFABClick = appState::requestSaveLink,
        hideSystemBars = appState.hideSystemBars,
        hideNavigationBar = appState.hideNavigationBar,
        statusBarDarkIcons = !isFileTab,
        // File은 흰 상태바 아이콘과 검은 내비게이션 아이콘을 서로 독립적으로 유지합니다.
        navigationBarDarkIcons = if (isFileTab) true else null,
        dimmed = shouldDimMyListMenu,
        searchOverlay = {
            // 검색 탑 시트 호출을 여기 한 곳으로 통일함. Home/File은 각자 데이터로 배선하지
            // 않고 onSearchOpen()으로 열기만 요청하며, 실제 상태(visible/쿼리/결과)는
            // searchViewModel(search 모듈)이 전담함.
            SearchOverlay(
                viewModel = searchViewModel,
                onLinkClick = appState::navigateToLinkDetail,
            )
        },
        content = content,
    )
}
