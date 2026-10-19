package com.linku.navigation

import androidx.navigation.NavHostController
import com.linku.MainViewModel
import com.linku.core.navigation.LinkuRoute
import com.linku.file.deeplink.DeepLinkHandlerViewModel
import com.linku.file.FileViewModel
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import com.linku.home.HomeViewModel
import com.linku.search.SearchViewModel
import com.linku.login.navigation.LoginRoute

/**
 * 로그아웃이나 탈퇴가 끝난 뒤 이전 사용자 상태를 정리하고 로그인 화면으로 돌아갑니다.
 *
 * @param navigator 화면 전환에 사용할 내비게이션 컨트롤러
 * @param viewModel 인증 상태와 닉네임을 관리하는 [MainViewModel]
 * @param homeViewModel 홈 데이터를 초기화할 [HomeViewModel]
 * @param searchViewModel 검색 상태를 초기화할 [SearchViewModel]
 * @param deepLinkViewModel 보류된 딥링크를 정리할 [DeepLinkHandlerViewModel]
 * @param folderStateViewModel 폴더 화면 상태를 초기화할 [FolderStateViewModel]
 * @param fileViewModel 공유 폴더 상태를 초기화할 [FileViewModel]
 * @param setShowNavBar 하단 내비게이션 바 표시 여부를 갱신하는 함수
 * @param onEdgeToEdgeChange 상태바와 내비게이션 바 숨김 여부를 갱신하는 함수
 */
internal fun handleLogoutToLogin(
    navigator: NavHostController,
    viewModel: MainViewModel,
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel,
    deepLinkViewModel: DeepLinkHandlerViewModel,
    folderStateViewModel: FolderStateViewModel,
    fileViewModel: FileViewModel,
    setShowNavBar: (Boolean) -> Unit,
    onEdgeToEdgeChange: (hideStatusBar: Boolean, hideNavigationBar: Boolean) -> Unit,
) {
    setShowNavBar(false)
    viewModel.setAuthenticated(false)
    // onImmersiveTransitionChange(true)가 버튼 클릭 시점에 이미
    // 처리했지만, 안전하게 한 번 더 명시함(대칭적으로 로그인 성공 시
    // false로 되돌리는 것과 짝).
    onEdgeToEdgeChange(true, true)

    homeViewModel.clearData()// 모든 홈 데이터를 초기화 - 이전 데이터 방지.
    searchViewModel.reset()
    deepLinkViewModel.clearPendingDeepLinks()
    folderStateViewModel.resetSharedFolderState()
    fileViewModel.resetSharedFolderState()
    // 🔐 토큰/세션은 ViewModel 쪽에서 이미 정리한 뒤,
    // 전역 스택을 지우고 로그인 루트로 이동
    viewModel.clearNickname()

    // saveState = true 로 저장된 탭별 백스택 상태(ViewModel 포함)를 제거.
    // popUpTo(graph.id, inclusive=true)는 백스택만 비우고 저장 상태는
    // NavController 내부 store에 남겨두기 때문에, 재로그인 후 탭 진입 시
    // restoreState = true 가 이전 계정의 ViewModel을 복원해버림.
    navigator.clearBackStack(LinkuRoute.CURATION)
    navigator.clearBackStack(LinkuRoute.FILE)
    navigator.clearBackStack(LinkuRoute.MY_PAGE)
    navigator.navigate(LoginRoute.ROOT) {
        // 그래프 루트까지 백스택 전부 제거.
        // Splash는 로그인 이후 이미 백스택에서 빠져있는 상태라
        // findStartDestination()(Splash)을 popUpTo 타겟으로 쓰면
        // 백스택에서 못 찾아 조용히 no-op 되어(Home/MyPage가 그대로 남음)
        // login_root가 그 위에 얹히기만 하는 문제가 있었음.
        // 그래프 자체의 id는 항상 모든 백스택 엔트리의 조상이라 반드시 제거됨.
        popUpTo(navigator.graph.id) {
            inclusive = true
        }
        launchSingleTop = true
    }
}
