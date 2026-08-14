package com.linku.file

import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.linku.file.viewmodel.delete.state.DeleteStateViewModel
import com.linku.file.viewmodel.edit.state.EditStateViewModel
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import com.linku.file.viewmodel.leave.state.LeaveStateViewModel

/**
 * 파일 탭의 화면과 내부 폴더 탐색 그래프를 구성합니다.
 *
 * 링크 상세 화면은 앱 루트 내비게이션 그래프가 소유하므로, 링크 선택 시
 * [onNavigateToLinkDetail]에 링크 ID를 전달해 상위에서 화면 전환을 처리하도록 합니다.
 *
 * @param fileViewModel 파일 및 폴더 데이터를 제공하는 ViewModel
 * @param editStateViewModel 폴더 편집 상태를 관리하는 ViewModel
 * @param deleteStateViewModel 폴더 삭제 대상 선택 상태를 관리하는 ViewModel
 * @param leaveStateViewModel 공유폴더 나가기 대상 선택 모드를 관리하는 ViewModel
 * @param folderStateViewModel 현재 폴더 단계와 파일 화면 UI 상태를 관리하는 ViewModel
 * @param onNavigateToLinkDetail 선택한 사용자 링크의 상세 화면으로 이동시키는 콜백
 * @param onSearchOpen 검색 UI가 열릴 때 호출되는 콜백
 */
@Composable
fun FileApp(
    fileViewModel: FileViewModel = hiltViewModel(),
    editStateViewModel: EditStateViewModel = viewModel(),
    deleteStateViewModel: DeleteStateViewModel = viewModel(),
    leaveStateViewModel: LeaveStateViewModel = viewModel(),
    folderStateViewModel: FolderStateViewModel = viewModel(),
    onNavigateToLinkDetail: (Long) -> Unit,
    onSearchOpen: () -> Unit = {},
) {
    val navController = rememberNavController()

    // 상태바/내비게이션 바는 MainScreen(app 모듈)에서 공통으로 흰색 처리함.

    NavHost(
        navController = navController,
        startDestination = "onboarding",
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable("onboarding") {
            FileScreen(
                fileViewModel = fileViewModel,
                editStateViewModel = editStateViewModel,
                deleteStateViewModel = deleteStateViewModel,
                leaveStateViewModel = leaveStateViewModel,
                folderStateViewModel = folderStateViewModel,
                onLinkClick = onNavigateToLinkDetail,
                onSearchOpen = onSearchOpen,
            )
        }
    }
}
