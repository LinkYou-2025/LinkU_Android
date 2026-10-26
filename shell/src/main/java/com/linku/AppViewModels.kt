package com.linku

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.linku.file.deeplink.DeepLinkHandlerViewModel
import com.linku.file.FileViewModel
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import com.linku.home.HomeViewModel
import com.linku.link.viewmodel.LinkViewModel
import com.linku.login.viewmodel.LoginViewModel
import com.linku.mypage.MyPageViewModel
import com.linku.search.SearchViewModel

/**
 * 앱 전역에서 공유하는 ViewModel 모음입니다.
 *
 * 모두 Activity 범위의 인스턴스이며, 내비게이션 그래프와 셸이 같은 인스턴스를 함께 사용합니다.
 *
 * @property main 앱 전역 상태와 세션, 사이드 이펙트를 제공하는 [MainViewModel]
 * @property login 로그인 화면 상태를 제공하는 [LoginViewModel]
 * @property home 홈 화면 상태를 제공하는 [HomeViewModel]
 * @property link 링크 저장, 조회, 수정, 삭제 상태를 제공하는 [LinkViewModel]
 * @property file 파일 화면 데이터를 제공하는 [FileViewModel]
 * @property folderState 폴더 화면 상태를 보관하는 [FolderStateViewModel]
 * @property search 검색 시트 상태를 제공하는 [SearchViewModel]
 * @property deepLink 로그인 후 이어서 처리할 딥링크를 보관하는 [DeepLinkHandlerViewModel]
 * @property myPage 마이페이지 상태를 제공하는 [MyPageViewModel]
 */
@Immutable
class AppViewModels internal constructor(
    val main: MainViewModel,
    val login: LoginViewModel,
    val home: HomeViewModel,
    val link: LinkViewModel,
    val file: FileViewModel,
    val folderState: FolderStateViewModel,
    val search: SearchViewModel,
    val deepLink: DeepLinkHandlerViewModel,
    val myPage: MyPageViewModel,
)

/**
 * 앱 전역 ViewModel 모음을 만들고 컴포지션 동안 기억합니다.
 *
 * @param main 이미 만들어진 [MainViewModel]
 * @return 기억된 [AppViewModels]
 */
@Composable
fun rememberAppViewModels(main: MainViewModel): AppViewModels {
    val login: LoginViewModel = hiltViewModel()
    val home: HomeViewModel = hiltViewModel()
    val link: LinkViewModel = hiltViewModel()
    val file: FileViewModel = hiltViewModel()
    val folderState: FolderStateViewModel = viewModel()
    val search: SearchViewModel = hiltViewModel()
    val deepLink: DeepLinkHandlerViewModel = hiltViewModel()
    val myPage: MyPageViewModel = hiltViewModel()

    return remember(main, login, home, link, file, folderState, search, deepLink, myPage) {
        AppViewModels(main, login, home, link, file, folderState, search, deepLink, myPage)
    }
}
