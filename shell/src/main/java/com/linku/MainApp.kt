package com.linku

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.linku.navigation.HandleNewIntentDeepLinks
import com.linku.design.AlarmAllowDialog
import com.linku.navigation.AppNavHost
import com.linku.navigation.AppShell
import com.linku.navigation.ClipboardEntryEffect
import com.linku.navigation.DoubleBackToExitIfTop
import com.linku.navigation.MainSideEffects
import com.linku.navigation.NetworkConnectionEffect
import com.linku.navigation.NicknameRefreshEffect
import com.linku.navigation.SessionNavigationEffect
import com.linku.navigation.SharedUrlEffect
import com.linku.navigation.UserBasicsEffect
import com.linku.navigation.rememberAppState
import com.linku.search.SearchBackHandler

/**
 * 앱 전역 상태와 셸, 내비게이션 호스트를 조립하는 앱의 합성 루트입니다.
 *
 * 내비게이션과 셸 UI 상태는 [com.linku.navigation.AppState], 화면 틀은 [AppShell], 내비게이션
 * 그래프 연결은 [AppNavHost], 앱 전역 효과는 각 Effect 함수가 맡고 이 함수는 순서대로 연결만 합니다.
 *
 * 콜드 스타트와 웜 스타트 딥링크를 내비게이션에 전달하며, 로그인 전에 보류된 초대가 있으면
 * 로그인 성공 후 해당 초대를 이어서 처리합니다. 테마는 호출한 Activity가 이 함수를 감싸 제공합니다.
 *
 * @param viewModel 앱 전역 상태와 세션 및 사이드 이펙트를 제공하는 [MainViewModel]
 * @param deepLinkHost https 앱 링크로 인식할 서버 호스트. 빌드 설정은 앱 모듈이 가지므로 앱이 전달합니다.
 */
@Composable
fun MainApp(
    viewModel: MainViewModel,
    deepLinkHost: String,
) {
    var showPushAlarmDialog by rememberSaveable { mutableStateOf(false) }

    NetworkConnectionEffect(viewModel)

    LaunchedEffect(Unit) {
        viewModel.initDeviceInfo()
    }

    val appState = rememberAppState()
    HandleNewIntentDeepLinks(appState.navigator)
    SessionNavigationEffect(appState, viewModel)

    val viewModels = rememberAppViewModels(viewModel)

    ClipboardEntryEffect(viewModel, viewModels.home)
    UserBasicsEffect(viewModel, viewModels.link)
    NicknameRefreshEffect(appState, viewModel)
    SharedUrlEffect(
        appState = appState,
        viewModel = viewModel,
        onNavigateToSaveLink = { url ->
            appState.navigateToSaveLinkWithUrl(viewModels.link, url)
        },
    )
    MainSideEffects(
        appState = appState,
        viewModel = viewModel,
        onShowPushAlarmDialog = { showPushAlarmDialog = true },
    )

    DoubleBackToExitIfTop(navigator = appState.navigator)

    // 다이알로그를 보여줘야 하면 출력.
    if (showPushAlarmDialog) {
        AlarmAllowDialog(
            onDismissRequest = {
                showPushAlarmDialog = false
                viewModel.denyPushAlarm()
            },
            onConfirmation = {
                showPushAlarmDialog = false
                viewModel.allowPushAlarm() // 성공/실패 토스트는 VM이 쏨
            }
        )
    }

    AppShell(
        appState = appState,
        folderStateViewModel = viewModels.folderState,
        searchViewModel = viewModels.search,
    ) {
        AppNavHost(
            appState = appState,
            viewModels = viewModels,
            deepLinkHost = deepLinkHost,
        )

        // 검색 탑 시트가 열려있을 때 뒤로가기를 최우선으로 가로채 검색창만 닫음.
        // Scaffold의 content 슬롯(NavHost 포함)은 SubcomposeLayout이라 여기서 등록하는
        // 콜백이 NavHost 내부의 기본 뒤로가기(스택 pop)보다 항상 나중에 붙어 우선권을 가짐.
        // searchOverlay()는 Scaffold 바깥의 일반 컴포지션이라 그보다 먼저 등록되어
        // NavHost의 기본 pop에 밀렸었음 — 그래서 여기(NavHost와 같은 서브컴포지션, 그 뒤)에 둠.
        SearchBackHandler(viewModel = viewModels.search)
    }
}
