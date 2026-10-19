package com.linku.file.navigation

import androidx.navigation.NavGraphBuilder
import com.linku.core.navigation.AppNavigator
import com.linku.core.navigation.LinkuRoute
import com.linku.file.FileApp
import com.linku.file.FileViewModel
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import androidx.navigation.compose.composable

/**
 * 파일 탭을 내비게이션 그래프에 등록합니다.
 *
 * 다른 모듈의 화면으로 이동하는 동작과 앱 전역 UI 상태는 호출한 쪽이 넘겨주는 콜백으로 이어받습니다.
 *
 * @param appNavigator 다른 모듈의 화면으로 이동할 때 사용하는 [AppNavigator]
 * @param fileViewModel 파일 화면 데이터를 제공하는 [FileViewModel]
 * @param folderStateViewModel 폴더 화면 상태를 보관하는 [FolderStateViewModel]
 * @param onSearchOpen 검색 화면을 여는 함수
 */
fun NavGraphBuilder.fileGraph(
    appNavigator: AppNavigator,
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    onSearchOpen: () -> Unit,
) {
    composable(LinkuRoute.FILE) {
        FileApp(
            fileViewModel = fileViewModel,
            folderStateViewModel = folderStateViewModel,
            onNavigateToLinkDetail = appNavigator::navigateToLinkDetail,
            onNavigateToSharedLinkDetail = appNavigator::navigateToSharedLinkDetail,
            onSearchOpen = onSearchOpen,
        )
    }
}
