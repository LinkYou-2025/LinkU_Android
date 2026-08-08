package com.linku

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.linku.core.model.alarm.AlarmType
import com.linku.core.error.DeepLinkError
import com.linku.core.model.auth.AutoLoginState
import com.linku.core.usecase.AcceptSharedFolderInvitationResult
import com.linku.core.util.logging.LinkuLog
import com.linku.core.util.logging.e
import com.linku.curation.navigation.curationGraph
import com.linku.curation.viewModel.CurationViewModel
import com.linku.deeplink.DeepLinkHandlerViewModel
import com.linku.deeplink.HandleNewIntentDeepLinks
import com.linku.deeplink.OPEN_DEEP_LINK_ROUTE
import com.linku.deeplink.OPEN_DEEP_LINK_TOKEN_ARGUMENT
import com.linku.deeplink.invitationLinkRoute
import com.linku.deeplink.openDeepLinkTokenArgument
import com.linku.deeplink.openDeepLinkUriPattern
import com.linku.deeplink.parseOpenDeepLinkToken
import com.linku.design.AlarmAllowDialog
import com.linku.design.theme.ThemeProvider
import com.linku.file.FileApp
import com.linku.file.FileViewModel
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import com.linku.home.HomeApp
import com.linku.home.HomeViewModel
import com.linku.home.screen.AlarmScreen
import com.linku.home.screen.NoticeScreen
import com.linku.home.viewmodel.AIArticleViewModel
import com.linku.home.viewmodel.LinkViewModel
import com.linku.link.component.LinkCategoryOption
import com.linku.link.screen.LinkDetailScreen
import com.linku.link.screen.SaveLinkScreen
import com.linku.link.util.toTempFile
import com.linku.login.navigation.LoginApp
import com.linku.login.viewmodel.LoginViewModel
import com.linku.mypage.MyPageApp
import com.linku.mypage.MyPageViewModel
import com.linku.mypage.NotificationViewModel
import com.linku.mypage.screen.AlarmSettingScreen
import com.linku.navigation.DoubleBackToExitIfTop
import com.linku.navigation.LinkuNavigationItem
import com.linku.search.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * 앱 전역 UI와 내비게이션 그래프를 구성하고 딥링크 및 로그인 후 화면 전환을 연결합니다.
 *
 * 콜드 스타트와 웜 스타트 딥링크를 내비게이션에 전달하며, 로그인 전에 보류된 초대가 있으면
 * 로그인 성공 후 해당 초대를 이어서 처리합니다.
 *
 * @param viewModel 앱 전역 상태와 세션 및 사이드 이펙트를 제공하는 [MainViewModel]
 */
@Composable
fun MainApp(
    viewModel: MainViewModel,
) {
    val context = LocalContext.current
    val deepLinkDomain = BuildConfig.SERVER_DOMAIN.trimEnd('/')
    val app = LocalContext.current.applicationContext

    var showPushAlarmDialog by rememberSaveable { mutableStateOf(false) }

    // MainScreen 전체 딤 처리 여부. GlobalDimController를 MyPageViewModel 등과 생성자 주입으로
    // 공유하므로, 콜백을 화면 트리로 relay하지 않고 여기서 바로 구독함.
    val dimmed by viewModel.isDimmed.collectAsStateWithLifecycle()

    // 네트워크 감지 추가
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    LaunchedEffect(isConnected) {
        if (!isConnected) {
            Toast.makeText(context, "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
            // TODO: 딤처리 (PM 확정 후 - 문구)
        }
    }

    // 닉네임 최상단 뒤치(사용하는 스크린)
    val nickname by viewModel.nickname.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        val smallestWidth = app.resources.configuration.smallestScreenWidthDp
        val deviceType = if (smallestWidth >= 600) "TABLET" else "PHONE"
        viewModel.initDeviceInfo(deviceType)
    }

    val navigator = rememberNavController()
    HandleNewIntentDeepLinks(navigator)

    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    var previousLoggedIn by rememberSaveable { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(isLoggedIn) {
        // onLogoutToLogin()이 이미 launchSingleTop으로 NavigationRoute.Login.route에 진입시킨 경우 여기서
        // 또 navigate하면 같은 목적지로 두 번 이동하게 되어 LoginApp의 "login" 컴포저블이
        // dispose→재mount되면서 EdgeToEdgeSystemBars의 hidden 값이 순간적으로
        // true→false→true로 흔들림. 그 결과 OS가 hide() 호출을 애니메이션 중첩으로 무시해서
        // 탈퇴/로그아웃 후 진입한 로그인 화면에 시스템 바가 계속 보이는 버그가 있었음.
        if (previousLoggedIn == true && isLoggedIn == false &&
            navigator.currentDestination?.route != NavigationRoute.Login.route
        ) {
            navigator.navigate("login_root") {
                popUpTo(navigator.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
        previousLoggedIn = isLoggedIn

    }

    // 로그인에서 사용할 뷰모델
    val loginViewModel: LoginViewModel = hiltViewModel()

    // 홈 화면에서 사용할 뷰모델
    val homeViewModel: HomeViewModel = hiltViewModel()
    // 로그인 혹은 자동 로그인 성공 후 생성함. 여기서는 이미 AuthPreference 주입 끝.

    // 링크 관련 뷰모델
    val linkViewModel: LinkViewModel = hiltViewModel()

    // 파일 화면에서 사용할 뷰모델
    val fileViewModel: FileViewModel = hiltViewModel()
    val folderStateViewModel: FolderStateViewModel = viewModel()

    val searchViewModel: SearchViewModel = hiltViewModel()
    val searchUiState by searchViewModel.uiState.collectAsStateWithLifecycle()
    val searchResults = searchViewModel.searchResults

    // 큐레이션 화면에서 사용할 뷰모델
    val curationViewModel: CurationViewModel = hiltViewModel()

    // 딥링크 접속 시 사용할 뷰모델
    val deepLinkViewModel: DeepLinkHandlerViewModel = hiltViewModel()

    // 마이페이지에서 사용할 뷰모델
    val mypageViewModel: MyPageViewModel = hiltViewModel()

    var showNavBar by rememberSaveable { mutableStateOf(false) }

    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()

    // 스플래시 애니메이션, 로그인 그라데이션 화면처럼 상태바 뒤로 콘텐츠가 그대로 비쳐야 하는
    // (edge-to-edge) 화면에서만 true. 그 외 화면은 전부 흰 상태바 스크림을 켜야 하므로 기본은 false.
    // Splash가 시작 화면이라 초기값만 true.
    var edgeToEdgeSystemBars by rememberSaveable { mutableStateOf(true) }
    // 상태바 아이콘 밝기(File 탭 등)는 LocalStatusBarDarkIcons로 화면이 직접 제어함 (MainScreen.kt 참고).

    // TODO : 로그인 뷰모델에서 Success 상태로 바꾸기 전에 세션 갱신하게 수정해야함.
    // 기기가 3대라 이렇게 되면 사용자 정보가 따로 놀 수 있음.


    // FIXME : 변수를 거쳐서 네비게이션에 가야 하는지 궁금합니다. 변수를 제거하는건?
    var saveLinkEntryTriggered by remember { mutableStateOf(false) }

    // 현재 라우트 관찰
    val navBackStackEntry by navigator.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        if (currentRoute == NavigationRoute.Home.route ||
            currentRoute == "curation_list"
        ) {
            viewModel.fetchNickname()
        }
    }

    // 푸시 알림 네비게이션 공통 함수.
    // Home이 백스택에 있어야 뒤로가기 시 Home으로 복귀하므로 popUpTo 사용.
    fun navigateByNotification(type: AlarmType, targetId: Long) {
        when (type) {
            AlarmType.NOTICE -> {
                showNavBar = false
                navigator.navigate("notice_screen/$targetId") {
                    popUpTo(NavigationRoute.Home.route) { inclusive = false }
                }
            }
            AlarmType.LINK ->
                navigator.navigate("savelinkresult/$targetId") {
                    popUpTo(NavigationRoute.Home.route) { inclusive = false }
                }
            AlarmType.FOLDER -> { /* TODO */ }
            AlarmType.CURATION -> { /* TODO */ }
            AlarmType.ALL -> Unit
        }
    }

    // 채널 사이드 이펙트 수신
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SideEffect.ShowToast ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is SideEffect.ShowPushAlarmDialog ->
                    showPushAlarmDialog = true

                is SideEffect.NavigateByNotification -> {
                    // alarmId 포함해서 저장 → consume 시점(인증 완료 후)에 readAlarm 호출됨
                    viewModel.setPendingNotification(effect.type, effect.targetId, effect.alarmId)

                    // 인증 완료 상태면 즉시 이동, 아니면 auth 완료 후 consume
                    if (isAuthenticated) {
                        viewModel.consumePendingNotification()?.let {
                            navigateByNotification(it.type, it.targetId)
                        }
                    }
                }
            }
        }
    }

    /**
     * 현재 경로가 지정한 하단 탭의 루트 또는 하위 경로인지 확인합니다.
     *
     * @param current 현재 내비게이션 경로
     * @param root 비교할 하단 탭의 루트 경로
     * @return 루트 경로와 같거나 경로 및 쿼리 하위에 속하면 `true`
     */
    fun isTabRoute(current: String?, root: String): Boolean =
        current == root || current?.startsWith("$root/") == true || current?.startsWith("$root?") == true

    val currentLinkuNavigationItem = when {
        isTabRoute(currentRoute, NavigationRoute.Curation.route) ->
            LinkuNavigationItem.CURATION

        // 큐레이션은 하위 라우트 존재(디테일 뭐 등등)
        isTabRoute(currentRoute, NavigationRoute.Home.route) ||
                currentRoute == "savelink" ||
                currentRoute == "savelinkresult/{linkuId}" -> LinkuNavigationItem.HOME
        isTabRoute(currentRoute, NavigationRoute.File.route) -> LinkuNavigationItem.FILE
        isTabRoute(currentRoute, NavigationRoute.MyPage.route) -> LinkuNavigationItem.MY_PAGE
        else -> null
    }

    // 액티비티 참조 + 두번뒤로 시간 기록
    // NOTE : 이미 구현된 DoubleBackToExitIfTop이 있어서 불필요함. 일단 주석 처리 후 추후 삭제?
//    val activity = remember(context) { context.findActivity() }
//    var lastBackPressed by remember { mutableLongStateOf(0L) }

    LaunchedEffect(saveLinkEntryTriggered) {
        if (saveLinkEntryTriggered) {
            navigator.navigate("savelink")
            saveLinkEntryTriggered = false
        }
    }

    // NOTE : 이게 App에 있어야 할까..? MainActivity에 있는게 맞을 것 같은데, 리펙 가능한 부분인가..? 고민이 듬
    ThemeProvider {
        DoubleBackToExitIfTop(navigator = navigator)

        // 다이알로그를 보여줘야 하면 출력.
        if (showPushAlarmDialog) {
            AlarmAllowDialog(
                onDismissRequest = { showPushAlarmDialog = false },
                onConfirmation = {
                    showPushAlarmDialog = false
                    viewModel.allowPushAlarm() // 성공/실패 토스트는 VM이 쏨
                }
            )
        }

        MainScreen(
            navigationBarProp = if (showNavBar) NavigationBarProp(
                currentLinkuNavigationItem = currentLinkuNavigationItem,
                onNavigate = { item ->
//                    if (item != currentLinkuNavigationItem) {
                    val route = when (item) {
                        LinkuNavigationItem.HOME -> NavigationRoute.Home.route
                        LinkuNavigationItem.FILE -> NavigationRoute.File.route
                        LinkuNavigationItem.CURATION -> NavigationRoute.Curation.route
                        LinkuNavigationItem.MY_PAGE -> NavigationRoute.MyPage.route
                    }


                    if (currentRoute == route) {
                        // 같은 탭 재선택: 내부 스택 리셋
                        navigator.navigate(route) {
                            // 해당 탭 루트까지 모두 제거하고
                            popUpTo(route) { inclusive = true }
                        }
                        // 다시 동일 라우트 진입 (깨끗한 초기 상태)
                        navigator.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        // 다른 탭으로 이동: 기존 로직 유지
                        navigator.navigate(route) {
                            popUpTo(navigator.graph.findStartDestination().id) {
                                saveState = true
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                },
                onCenterButtonClicked = {
                    // 여기에 중앙 버튼 눌렀을 때 로직 넣기
                    saveLinkEntryTriggered = true  // SaveLinkScreen으로 진입
                }
            ) else null,
            centerButtonProp = null, // 바로 이동하므로 null
            onFABClick = { saveLinkEntryTriggered = true },
            hideSystemBars = edgeToEdgeSystemBars,
            dimmed = dimmed
        ) {
            NavHost(
                navController = navigator,
                startDestination = NavigationRoute.Splash.route,
                modifier = Modifier.fillMaxSize(),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None },
            ) {
                //스플래쉬
                with(NavigationRoute.Splash) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = false
                            edgeToEdgeSystemBars = true // 스플래시: 상태/내비게이션 바 완전히 숨김
                        }

                        var autoLoginTried by rememberSaveable {
                            mutableStateOf(false)
                        }

                        val splashScope = rememberCoroutineScope()

                        val autoLoginState by loginViewModel.autoLoginState.collectAsStateWithLifecycle()

                        LaunchedEffect(autoLoginState) {
                            when (autoLoginState) {
                                is AutoLoginState.Success -> {
                                    showNavBar = true
                                    viewModel.setAuthenticated(true)
                                    edgeToEdgeSystemBars = false
                                    homeViewModel.refreshAfterLogin()
                                    navigator.navigate(NavigationRoute.Home.route) {
                                        popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                    // 자동 로그인 성공 후 pending 알림 처리
                                    viewModel.consumePendingNotification()?.let {
                                        navigateByNotification(it.type, it.targetId)
                                    }
                                }

                                is AutoLoginState.Failed -> {
                                    navigator.navigate(NavigationRoute.Login.route) {
                                        popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                                    }
                                }

                                else -> Unit
                            }

                        }

                        Splash(
                            onResult = {
                                splashScope.launch {
                                    val hasRefresh = viewModel.hasValidRefreshToken()

                                    if (autoLoginTried || !hasRefresh) {
                                        navigator.navigate(NavigationRoute.Login.route) {
                                            popUpTo(NavigationRoute.Splash.route) {
                                                inclusive = true
                                            }
                                        }
                                        return@launch
                                    }

                                    loginViewModel.tryAutoLogin()
                                    autoLoginTried = true
                                }
                            }
                        )
                    }
                }

                composable(NavigationRoute.Login.route) {
                    LaunchedEffect(Unit) { showNavBar = false }
                    val loginScope = rememberCoroutineScope()

                    /**
                     * 비동기 로그인 결과가 도착한 시점에도 로그인 화면에 있을 때만 대상 화면으로 이동합니다.
                     *
                     * @param route 로그인 화면에서 전환할 목적지 경로
                     */
                    fun navigateFromLoginTo(route: String) {
                        if (navigator.currentDestination?.route == NavigationRoute.Login.route) {
                            showNavBar = route != NavigationRoute.Login.route
                            navigator.navigate(route) {
                                popUpTo(NavigationRoute.Login.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }

                    /**
                     * 로그인 화면에서 공유 폴더 상태를 초기화한 뒤 공유 폴더 화면을 새 루트로 엽니다.
                     */
                    fun openSharedFoldersFromLogin() {
                        if (navigator.currentDestination?.route == NavigationRoute.Login.route) {
                            showNavBar = true
                            folderStateViewModel.resetSharedFolderState()
                            folderStateViewModel.updateIsSharedFolders(true)

                            navigator.navigate(NavigationRoute.File.route) {
                                popUpTo(NavigationRoute.Login.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }

                    /**
                     * 보류 중인 초대 처리 실패를 알리고 일반 폴더 상태로 홈 화면을 새 루트로 엽니다.
                     *
                     * @param messageResId 실패 원인을 안내할 문자열 리소스 ID
                     */
                    fun handlePendingInvitationFailure(messageResId: Int) {
                        if (navigator.currentDestination?.route == NavigationRoute.Login.route) {
                            showNavBar = true
                            folderStateViewModel.resetSharedFolderState()
                            folderStateViewModel.updateIsSharedFolders(false)
                            Toast.makeText(
                                context,
                                messageResId,
                                Toast.LENGTH_SHORT,
                            ).show()

                            navigator.navigate(NavigationRoute.Home.route) {
                                popUpTo(NavigationRoute.Login.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }

                    LoginApp(
                        //navController = navigator,
                        loginViewModel = loginViewModel,
                        onEdgeToEdgeChange = { edgeToEdgeSystemBars = it },
                        onLoginSuccess = {
                            showNavBar = true
                            viewModel.setAuthenticated(true)
                            edgeToEdgeSystemBars = false

                            // TODO: 지민님 딥링크 대기 작업 처리 확인 필요 요청하기.
                            // 보류된 초대 토큰을 먼저 처리하고, 없으면 공유 폴더 ID를 처리합니다.
                            // 둘 다 없을 때만 정상 로그인 경로로 홈 화면을 엽니다.
                            val pendingInvitationToken =
                                deepLinkViewModel.consumePendingInvitation()

                            if (pendingInvitationToken.isNotBlank()) {
                                loginScope.launch {
                                    when (
                                        fileViewModel.receiveSharedFolderInvitation(
                                            pendingInvitationToken
                                        )
                                    ) {
                                        is AcceptSharedFolderInvitationResult.Accepted -> {
                                            openSharedFoldersFromLogin()
                                        }

                                        is AcceptSharedFolderInvitationResult.AcceptedButRefreshFailed -> {
                                            // 초대 수락은 완료되었으므로 갱신 실패를 알리고 공유 폴더를 엽니다.
                                            Toast.makeText(
                                                context,
                                                R.string.share_folder_refresh_failed,
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                            openSharedFoldersFromLogin()
                                        }

                                        is AcceptSharedFolderInvitationResult.AuthenticationRequired -> {
                                            // 소비한 토큰을 복원해 다음 로그인 성공 후 초대 수락을 재시도합니다.
                                            deepLinkViewModel.setPendingInvitation(
                                                pendingInvitationToken
                                            )
                                            Toast.makeText(
                                                context,
                                                R.string.authentication_required,
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                            navigateFromLoginTo(NavigationRoute.Login.route)
                                        }

                                        is AcceptSharedFolderInvitationResult.InvalidInvitation -> {
                                            handlePendingInvitationFailure(
                                                R.string.invalid_share_link
                                            )
                                        }

                                        is AcceptSharedFolderInvitationResult.NetworkFailure -> {
                                            handlePendingInvitationFailure(
                                                R.string.network_error
                                            )
                                        }

                                        is AcceptSharedFolderInvitationResult.Failure -> {
                                            handlePendingInvitationFailure(
                                                R.string.undefined_behavior
                                            )
                                        }
                                    }
                                }
                                return@LoginApp
                            }

                            deepLinkViewModel.consumePendingShare()?.let { folderId ->
                                showNavBar = true
                                fileViewModel.receiveSharedFolder(folderId)
                                folderStateViewModel.resetSharedFolderState()
                                folderStateViewModel.updateIsSharedFolders(true)

                                navigator.navigate(NavigationRoute.File.route) {
                                    popUpTo(NavigationRoute.Login.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                                return@LoginApp
                            }

                            // 수동 로그인 성공 후 pending 알림 처리
                            viewModel.consumePendingNotification()?.let {
                                navigator.navigate(NavigationRoute.Home.route) {
                                    popUpTo("login_root") { inclusive = true }
                                    launchSingleTop = true
                                }
                                navigateByNotification(it.type, it.targetId)
                                return@LoginApp
                            }

                            // pending 알림이 없는 경우의 기본 동작

                            showNavBar = true
                            navigator.navigate(NavigationRoute.Home.route) {
                                popUpTo(NavigationRoute.Login.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }


                with(NavigationRoute.Home) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            viewModel.checkAndShowPushAlarmDialog()
                        }

                        HomeApp(
                            viewModel = homeViewModel,
                            searchUiState = searchUiState,
                            searchResults = searchResults,
                            onSearchQueryChange = searchViewModel::search,
                            onSearchOpen = searchViewModel::openSearch,
                            onSearchDismiss = searchViewModel::resetSearchResults,
                            onSearchHistoryDelete = searchViewModel::removeRecentQuery,
                            onSearchHistoryClear = searchViewModel::clearRecentQueries,
                            nickname = nickname.orEmpty().ifBlank { "링큐" },
                            onNavigateToSetting = {
                                navigator.navigate(NavigationRoute.AlarmSetting.route)
                            },
                            onNavigateToSaveLink = { url ->
                                linkViewModel.setSaveUrl(url)
                                navigator.navigate("savelink")
                            },
                            onNavigateToLinkDetail = { linkuId ->
                                navigator.navigate("savelinkresult/$linkuId")
                            },
                            onNavigateToCuration = {
                                navigator.navigate("curation_card1")
                            },
                            onNavigateToAlarm = {
                                navigator.navigate(NavigationRoute.Alarm.route)
                            }
                        )
                    }
                }

                with(NavigationRoute.File) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true

                        }

                        FileApp(
                            fileViewModel = fileViewModel,
                            folderStateViewModel = folderStateViewModel,
                            searchUiState = searchUiState,
                            searchResults = searchResults,
                            onSearchQueryChange = searchViewModel::search,
                            onSearchOpen = searchViewModel::openSearch,
                            onSearchDismiss = searchViewModel::resetSearchResults,
                            onSearchHistoryDelete = searchViewModel::removeRecentQuery,
                            onSearchHistoryClear = searchViewModel::clearRecentQueries,
                        )
                    }
                }

                // 큐레이션 파트 리팩토링 적용
                curationGraph(
                    navigator = navigator,
                    curationViewModel = curationViewModel,
                    showNavBar = { showNavBar = it },
                    nickname = nickname.orEmpty().ifBlank { "링큐" }
                )


                with(NavigationRoute.MyPage) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true

                            mypageViewModel.loadUserInfo()
                        }
                        //FinishHandler()



                        MyPageApp(
                            viewModel = mypageViewModel,
                            // 로그아웃/탈퇴 버튼을 누른 "즉시"(API 응답 기다리지 않고) 시스템 바를
                            // 몰입 모드로 전환함 — API 호출 및 Toast 표시 사이에 시스템 바가
                            // 잠깐 보였다가 사라지는 깜빡임을 없애기 위함. 실패해서 MyPage에 남으면
                            // MyPageApp이 false로 되돌림.
                            onImmersiveTransitionChange = { edgeToEdgeSystemBars = it },
                            onLogoutToLogin = {
                                showNavBar = false
                                viewModel.setAuthenticated(false)
                                // onImmersiveTransitionChange(true)가 버튼 클릭 시점에 이미
                                // 처리했지만, 안전하게 한 번 더 명시함(대칭적으로 로그인 성공 시
                                // false로 되돌리는 것과 짝).
                                edgeToEdgeSystemBars = true

                                homeViewModel.clearData()// 모든 홈 데이터를 초기화 - 이전 데이터 방지.
                                searchViewModel.reset()
                                deepLinkViewModel.clearPendingDeepLinks()
                                folderStateViewModel.resetSharedFolderState()
                                fileViewModel.resetSharedFolderState()
                                // 🔐 토큰/세션은 ViewModel 쪽에서 이미 정리한 뒤,
                                // 전역 스택을 지우고 로그인 루트로 이동
                                viewModel.clearNickname()
                                navigator.navigate(NavigationRoute.Login.route) {
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
                            },
                            onNavigateToAlarm = {
                                navigator.navigate(NavigationRoute.Alarm.route)
                            }
                        )
                    }
                }

                with(NavigationRoute.AlarmSetting) {
                    setNavGraph {
                        LaunchedEffect(Unit) { showNavBar = false }

                        val notificationViewModel: NotificationViewModel = hiltViewModel()

                        AlarmSettingScreen(
                            navController = navigator,
                            viewModel = notificationViewModel
                        )
                    }
                }

                with(NavigationRoute.Alarm) {
                    setNavGraph {
                        LaunchedEffect(Unit) { showNavBar = false }
                        AlarmScreen(
                            onBack = {
                                showNavBar = true
                                navigator.popBackStack()
                            },
                            onNavigateToSetting = { navigator.navigate(NavigationRoute.AlarmSetting.route) },
                            onNavigateToHome = {
                                navigator.navigate(NavigationRoute.Home.route) {
                                    popUpTo(NavigationRoute.Home.route) { inclusive = false }
                                }
                            },
                            onNavigateToLinkDetail = { targetId -> navigator.navigate("savelinkresult/$targetId") },
                            onNavigateToFolder = { /* TODO */ },
                            onNavigateToCuration = { /* TODO */ },
                            onNavigateToNotice = { targetId ->
                                showNavBar = false
                                navigator.navigate("notice_screen/$targetId")
                            }
                        )
                    }
                }

                composable(
                    route = NavigationRoute.Notice.route,
                    arguments = listOf(navArgument("targetId") { type = NavType.LongType })
                ) {
                    LaunchedEffect(Unit) { showNavBar = false }
                    NoticeScreen(
                        onBack = {
                            val prevRoute = navigator.previousBackStackEntry?.destination?.route
                            if (prevRoute != NavigationRoute.Alarm.route) showNavBar = true
                            navigator.popBackStack()
                        }
                    )
                }

                composable("savelink") {
                    val context = LocalContext.current
                    val linkUiState by linkViewModel.uiState.collectAsStateWithLifecycle()

                    fun exitSaveLinkScreen() {
                        linkViewModel.resetSaveForm()
                        navigator.popBackStack()
                    }

                    BackHandler {
                        exitSaveLinkScreen()
                    }

                    SaveLinkScreen(
                        image = linkUiState.saveImage,
                        url = linkUiState.saveUrl,
                        title = linkUiState.saveTitle,
                        memo = linkUiState.saveMemo,
                        selectedEmotionId =
                            linkUiState.selectedSaveEmotionId,
                        selectedSituationId =
                            linkUiState.selectedSaveSituationId,
                        jobId = linkUiState.jobId ?: 3L,
                        onImageSelected = linkViewModel::setSaveImage,
                        onPermissionDenied = {
                            Toast.makeText(
                                context,
                                "사진을 추가하려면 사진 접근 권한이 필요합니다.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                        onImageLoadFailed = {
                            Toast.makeText(
                                context,
                                "이미지 로드에 실패했습니다.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                        onDeleteImage = linkViewModel::deleteSaveImage,
                        onUrlChange = linkViewModel::setSaveUrl,
                        onTitleChange = linkViewModel::setSaveTitle,
                        onMemoChange = linkViewModel::setSaveMemo,
                        onEmotionSelect =
                            linkViewModel::selectSaveEmotion,
                        onSituationClick =
                            linkViewModel::onSaveSituationClick,
                        onBack = {
                            exitSaveLinkScreen()
                        },
                        isSaveButtonEnabled =
                            linkUiState.isSaveButtonEnabled,
                        onSaveButtonClick = {
                            linkViewModel.onSaveButtonClick(
                                onSucceed = { saved ->
                                    linkViewModel.loadLinkDetail(
                                        saved.linkuId,
                                    )
                                    linkViewModel.resetSaveForm()

                                    navigator.navigate(
                                        "savelinkresult/${saved.linkuId}",
                                    )
                                },
                                onFailed = { error ->
                                    Log.e(
                                        "SaveLink",
                                        "failed",
                                        error,
                                    )
                                },
                            )
                        },
                        toastEvent = linkViewModel.toastEvent,
                    )
                }


                composable(
                    route = "savelinkresult/{linkuId}",
                    arguments = listOf(navArgument("linkuId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val vm: HomeViewModel = homeViewModel
                    val context = LocalContext.current
                    val detailCoroutineScope = rememberCoroutineScope()
                    val linkUiState by linkViewModel.uiState.collectAsStateWithLifecycle()

                    val linkuId = backStackEntry.arguments
                        ?.takeIf { it.containsKey("linkuId") }
                        ?.getLong("linkuId")
                        ?.takeIf { it > 0L }
                        ?: return@composable

                    val aiArticleViewModel: AIArticleViewModel = hiltViewModel(backStackEntry)
                    val aiArticleUiState by aiArticleViewModel.uiState.collectAsStateWithLifecycle()

                    var selectedDetailImageUri by rememberSaveable(linkuId) {
                        mutableStateOf<Uri?>(null)
                    }

                    val detailImagePicker = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        selectedDetailImageUri = uri
                    }

                    LaunchedEffect(linkuId) {
                        linkViewModel.loadLinkDetail(linkuId)
                        vm.loadCategoryColors()
                    }

                    fun emotionNameOf(id: Long?): String {
                        return when (id) {
                            1L -> "즐거움"
                            2L -> "평온"
                            3L -> "설렘"
                            4L -> "슬픔"
                            5L -> "짜증"
                            6L -> "분노"
                            else -> "감정"
                        }
                    }

                    // TODO: 카테고리 API 연동 후 categoryId 기준 실제 카테고리명/색상 매핑으로 교체
                    val CATEGORY_MAP = linkedMapOf(
                        1L to "어학",
                        2L to "뉴스",
                        3L to "공부법",
                        4L to "IT·개발",
                        5L to "자기계발",
                        6L to "취업·이직",
                        7L to "비즈니스 인사이트",
                        8L to "생산성·툴",
                        9L to "라이프스타일",
                        10L to "심리·자기이해",
                        11L to "에세이·칼럼",
                        12L to "트렌드",
                        13L to "디자인·예술",
                        14L to "영상·뮤직",
                        15L to "맛집·여행",
                        16L to "기타"
                    )

                    fun categoryNameOf(id: Long?): String {
                        return CATEGORY_MAP[id] ?: "카테고리"
                    }

                    fun categoryIdOf(name: String): Long? {
                        return CATEGORY_MAP.entries
                            .firstOrNull { it.value == name }
                            ?.key
                    }

                    // 색상 맵 수집
                    val categoryColorMap = vm.categoryColorMap.collectAsState().value

                    val categoryOptions = categoryColorMap.mapNotNull { (name, style) ->
                        val id = categoryIdOf(name) ?: return@mapNotNull null

                        LinkCategoryOption(
                            id = id,
                            name = name,
                            color = style.color4
                        )
                    }

                    val linkDetail = linkUiState.linkDetail

                    LaunchedEffect(
                        linkDetail?.keyword,
                        linkDetail?.summary,
                    ) {
                        aiArticleViewModel.setLinkContent(
                            keyword = linkDetail?.keyword,
                            summary = linkDetail?.summary,
                        )
                    }

                    LinkDetailScreen(
                        linkuId = linkuId,
                        linkTitle = linkDetail?.title.orEmpty(),
                        category = categoryNameOf(linkDetail?.categoryId),
                        emotion = emotionNameOf(linkDetail?.emotionId),
                        situationId = linkDetail?.situationId,
                        linkUrl = linkDetail?.linku.orEmpty(),
                        imageUrl = linkDetail?.linkuImageUrl.toImageUrl(),
                        selectedImageUri = selectedDetailImageUri,
                        memo = linkDetail?.memo.orEmpty(),
                        tags = aiArticleUiState.displayTags,
                        aiSummary = aiArticleUiState.displaySummary,
                        isAiArticleLoading = aiArticleUiState.isLoading,
                        aiArticleErrorMessage = aiArticleUiState.errorMessage,
                        onRequestAiArticle = aiArticleViewModel::getAiArticle,
                        onClearAiArticleError =
                            aiArticleViewModel::clearErrorMessage,
                        categoryOptions = categoryOptions,
                        onBack = {
                            navigator.popBackStack()
                        },
                        onPickImage = {
                            detailImagePicker.launch("image/*")
                        },
                        onSubmitEdit = { title, memo, categoryId, emotionId, situationId, onSuccess, onFailed ->
                            detailCoroutineScope.launch {
                                val selectedTempImage = runCatching {
                                    withContext(Dispatchers.IO) {
                                        selectedDetailImageUri?.toTempFile(context)
                                    }
                                }.getOrElse { error ->
                                    LinkuLog.e(
                                        "LinkDetail",
                                        "selected image conversion failed",
                                        error,
                                    )
                                    onFailed()
                                    return@launch
                                }

                                linkViewModel.updateLink(
                                    image = selectedTempImage,
                                    title = title,
                                    memo = memo,
                                    categoryId = categoryId,
                                    emotionId = emotionId,
                                    situationId = situationId,
                                    onSucceed = {
                                        selectedDetailImageUri = null
                                        homeViewModel.loadRecentLinks()
                                        onSuccess()
                                    },
                                    onFailed = { error ->
                                        LinkuLog.e(
                                            "LinkDetail",
                                            "update failed",
                                            error,
                                        )
                                        onFailed()
                                    },
                                )
                            }
                        },
                        onDeleteLink = { onSuccess, onFailed ->
                            linkViewModel.deleteCurrentLink(
                                onSucceed = {
                                    homeViewModel.refreshHomeData()
                                    onSuccess()
                                },
                                onFailed = {
                                    onFailed()
                                },
                            )
                        }
                    )
                }

                composable(
                    route = OPEN_DEEP_LINK_ROUTE,
                    arguments = listOf(
                        openDeepLinkTokenArgument(),
                    ),
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = openDeepLinkUriPattern(deepLinkDomain)
                        }
                    )
                ) { backStackEntry ->

                    // 백 스택 항목마다 한 번 처리하고 같은 ID를 비동기 결과의 유효성 확인에 사용합니다.
                    LaunchedEffect(backStackEntry.id) {

                        try {
                            // non-null 인자의 빈 기본값은 파서에서 구체적인 딥링크 오류로 변환합니다.
                            val token = parseOpenDeepLinkToken(
                                backStackEntry.arguments?.getString(
                                    OPEN_DEEP_LINK_TOKEN_ARGUMENT
                                ).orEmpty()
                            )

                            invitationLinkRoute(
                                token = token,
                                isLoggedIn = viewModel.hasValidRefreshToken(),
                                onReceiveSharedFolderInvitation = fileViewModel::receiveSharedFolderInvitation,
                                onUpdateIsSharedFolders = { isSharedFolders ->
                                    folderStateViewModel.resetSharedFolderState()
                                    folderStateViewModel.updateIsSharedFolders(
                                        isSharedFolders
                                    )
                                },
                                onSetPendingInvitation = deepLinkViewModel::setPendingInvitation,
                                onInvalidLink = {
                                    Toast.makeText(
                                        context,
                                        R.string.invalid_share_link,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onAuthenticationRequired = {
                                    Toast.makeText(
                                        context,
                                        R.string.authentication_required,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onNetworkFailure = {
                                    Toast.makeText(
                                        context,
                                        R.string.network_error,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onRefreshFailed = {
                                    Toast.makeText(
                                        context,
                                        R.string.share_folder_refresh_failed,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onFailure = {
                                    Toast.makeText(
                                        context,
                                        R.string.undefined_behavior,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                navigator = navigator,
                                deepLinkEntryId = backStackEntry.id,
                            )

                        } catch (e: CancellationException) {
                            // Compose effect 취소를 일반 오류로 변환하지 않고 구조화된 취소를 전파합니다.
                            throw e

                        } catch (e: DeepLinkError.MissingInvitationToken) {
                            // 토큰 누락은 현재 화면과 초대 상태를 바꾸지 않고 안내만 표시합니다.
                            Toast.makeText(
                                context,
                                R.string.invalid_share_link,
                                Toast.LENGTH_SHORT
                            ).show()

                        } catch (e: IllegalArgumentException) {
                            // 추후 공통 토스트 메시지로 변경
                            Toast.makeText(context, R.string.undefined_behavior, Toast.LENGTH_SHORT).show()

                        } catch (e: Exception) {
                            // 추후 공통 토스트 메시지로 변경
                            Toast.makeText(context, R.string.undefined_behavior, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            // 바텀탭의 루트 라우트인지 판정 (바텀바가 보일 때만)
//            val isAtTabRoot = showNavBar && when (currentRoute) {
//                NavigationRoute.Home.route,
//                NavigationRoute.File.route,
//                NavigationRoute.Curation.route, // curation_graph의 리스트 루트
//                NavigationRoute.MyPage.route -> true
//                else -> false
//            }

            // 루트에서만 '두 번 뒤로 종료'
//            BackHandler(enabled = isAtTabRoot) {
//                val now = System.currentTimeMillis()
//                if (now - lastBackPressed < 2000L) {
//                    activity?.finish()
//                } else {
//                    Toast.makeText(context, "뒤로 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
//                    lastBackPressed = now
//                }
//            }

        }
    }


}

/**
 * 이미지 URL에 스킴이 없으면 HTTPS 스킴을 추가한다.
 *
 * 빈 문자열이나 null은 null로 반환한다.
 * 스킴이 포함된 URI는 스킴의 종류와 대소문자에 관계없이 원본 값을 유지하며,
 * 프로토콜 상대 URL은 HTTPS 스킴을 추가한다.
 */
private fun String?.toImageUrl(): String? {
    val value = this?.trim()?.takeIf { it.isNotEmpty() } ?: return null

    return when {
        value.startsWith("//") -> "https:$value"
        Uri.parse(value).scheme != null -> value
        else -> "https://$value"
    }
}

// 확장 함수: Context -> Activity
fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
