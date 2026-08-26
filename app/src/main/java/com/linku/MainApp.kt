package com.linku

import android.app.Activity
import java.util.Calendar
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
import com.linku.core.error.DeepLinkError
import com.linku.core.model.CategoryType
import com.linku.core.model.alarm.AlarmType
import com.linku.core.model.auth.AutoLoginState
import com.linku.core.usecase.AcceptSharedFolderInvitationResult
import com.linku.core.util.logging.LinkuLog
import com.linku.core.util.logging.e
import com.linku.curation.navigation.curationGraph
import com.linku.deeplink.CUSTOM_SCHEME_OPEN_DEEP_LINK_URI_PATTERN
import com.linku.deeplink.DeepLinkHandlerViewModel
import com.linku.deeplink.HandleNewIntentDeepLinks
import com.linku.deeplink.OPEN_DEEP_LINK_ROUTE
import com.linku.deeplink.OPEN_DEEP_LINK_TOKEN_ARGUMENT
import com.linku.deeplink.invitationLinkRoute
import com.linku.deeplink.openDeepLinkTokenArgument
import com.linku.deeplink.openDeepLinkUriPattern
import com.linku.deeplink.parseOpenDeepLinkToken
import com.linku.deeplink.showAcceptedSharedFolder
import com.linku.design.AlarmAllowDialog
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.data.util.toCategoryColorStyleMap
import com.linku.design.top.search.SearchBarTopSheet
import com.linku.file.FileApp
import com.linku.file.FileViewModel
import com.linku.file.viewmodel.folder.state.FileNavigationState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import com.linku.home.HomeApp
import com.linku.home.HomeViewModel
import com.linku.home.screen.AlarmScreen
import com.linku.home.screen.NoticeScreen
import com.linku.home.viewmodel.AIArticleViewModel
import com.linku.home.viewmodel.LinkViewModel
import com.linku.link.component.LinkCategoryOption
import com.linku.link.screen.LinkDetailLoadErrorScreen
import com.linku.link.screen.LinkDetailLoadingScreen
import com.linku.link.screen.LinkDetailScreen
import com.linku.link.screen.SaveLinkScreen
import com.linku.link.screen.SharedLinkDetailScreen
import com.linku.link.screen.verifiedLinkDetailJobId
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

private const val LINK_DETAIL_USER_LINKU_ID_ARGUMENT = "userLinkuId"
private const val LINK_DETAIL_ROUTE_PATTERN =
    "savelinkresult/{$LINK_DETAIL_USER_LINKU_ID_ARGUMENT}"

/** 새 링크 정보를 입력하는 앱 루트 경로입니다. */
private const val SAVE_LINK_ROUTE = "savelink"

private fun linkDetailRoute(userLinkuId: Long): String =
    "savelinkresult/$userLinkuId"

/**
 * 공유폴더 링크의 읽기 전용 상세 화면 경로입니다.
 *
 * 공유폴더 링크는 소유자 상세 API를 호출할 수 없어(다른 사용자 소유라 404), 목록 조회 시점의
 * 값을 [FolderStateViewModel.selectedSharedLink]에 담아 인자 없이 이 경로로만 이동합니다.
 */
private const val SHARED_LINK_DETAIL_ROUTE = "sharedlinkdetail"

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
    val deepLinkHost = BuildConfig.SERVER_HOST
    val app = LocalContext.current.applicationContext

    var showPushAlarmDialog by rememberSaveable { mutableStateOf(false) }

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
    val isNicknameLoading by viewModel.isNicknameLoading.collectAsStateWithLifecycle()


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
        if (isLoggedIn == false) {
            // 토큰 재발급 실패로 로컬 세션만 정리된 경우에도 메모리 인증 상태를 함께 종료합니다.
            viewModel.setAuthenticated(false)
        }

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
    val searchVisible by searchViewModel.visible.collectAsStateWithLifecycle()

    // 딥링크 접속 시 사용할 뷰모델
    val deepLinkViewModel: DeepLinkHandlerViewModel = hiltViewModel()

    // 마이페이지에서 사용할 뷰모델
    val myPageViewModel: MyPageViewModel = hiltViewModel()

    var showNavBar by rememberSaveable { mutableStateOf(false) }

    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()

    LaunchedEffect(isLoggedIn, isAuthenticated) {
        if (isLoggedIn == true && isAuthenticated) {
            // 자동·수동 로그인 모두 세션 저장과 인증 확인이 끝난 뒤 최신 직업을 조회합니다.
            linkViewModel.loadUserBasics()
        } else {
            // 로그아웃·탈퇴·토큰 만료 및 초기 인증 확인 전에는 이전 사용자 직업을 제거합니다.
            linkViewModel.clearUserBasics()
        }
    }

    // 스플래시 애니메이션, 로그인 그라데이션 화면처럼 상태바 뒤로 콘텐츠가 그대로 비쳐야 하는
    // (edge-to-edge) 화면에서만 true. 그 외 화면은 전부 흰 상태바 스크림을 켜야 하므로 기본은 false.
    // Splash가 시작 화면이라 초기값만 true.
    var edgeToEdgeSystemBars by rememberSaveable { mutableStateOf(true) }
    // 내비게이션 바 숨김 여부는 기본적으로 edgeToEdgeSystemBars와 함께 움직이지만, 약관 동의
    // 바텀시트(social_login_gate)처럼 상태바는 숨긴 배경을 유지하면서도 내비게이션 바는 항상
    // 보여야 하는 화면에서만 따로 false로 둠.
    var hideNavigationBar by rememberSaveable { mutableStateOf(true) }
    // 활성 탭의 시스템 바 아이콘 기본값은 아래 MainScreen 호출부에서 route 기준으로 결정합니다.

    // TODO : 로그인 뷰모델에서 Success 상태로 바꾸기 전에 세션 갱신하게 수정해야함.
    // 기기가 3대라 이렇게 되면 사용자 정보가 따로 놀 수 있음.


    // FIXME : 변수를 거쳐서 네비게이션에 가야 하는지 궁금합니다. 변수를 제거하는건?
    var saveLinkEntryTriggered by remember { mutableStateOf(false) }

    // 현재 라우트 관찰
    val navBackStackEntry by navigator.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 탭별 showNavBar 상태는 보존하고 앱 루트의 링크 저장·상세 화면에서는 하단 네비게이션을 가립니다.
    val shouldShowNavigationBar =
        showNavBar &&
                currentRoute != SAVE_LINK_ROUTE &&
                currentRoute != LINK_DETAIL_ROUTE_PATTERN &&
                currentRoute != SHARED_LINK_DETAIL_ROUTE

    // 기기 3대까지 지원하므로 다른 기기에서 닉네임을 바꾸면 즉시 반영되도록 Home/Curation
    // 진입마다 재호출함. 로그인 시점 선호출(MainViewModel.setAuthenticated)과 별개로 유지.
    LaunchedEffect(currentRoute) {
        if (currentRoute == NavigationRoute.Home.route ||
            currentRoute == NavigationRoute.Curation.route
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
            AlarmType.LINK -> {
                val userLinkuId = targetId
                navigator.navigate(linkDetailRoute(userLinkuId)) {
                    popUpTo(NavigationRoute.Home.route) { inclusive = false }
                }
            }
            AlarmType.FOLDER -> { /* TODO */ }
            AlarmType.CURATION -> {
                showNavBar = false
                val cal = Calendar.getInstance()

                // 알림 payload에 month 없으므로 로컬 현재 월을 yyyy-MM 형식으로 사용
                val localMonth = "%04d-%02d".format(
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1
                )
                navigator.navigate("curation/detail/$localMonth/$targetId") {
                    popUpTo(NavigationRoute.Home.route) { inclusive = false }
                }
            }
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
                currentRoute == SAVE_LINK_ROUTE ||
                currentRoute == LINK_DETAIL_ROUTE_PATTERN -> LinkuNavigationItem.HOME
        isTabRoute(currentRoute, NavigationRoute.File.route) -> LinkuNavigationItem.FILE
        isTabRoute(currentRoute, NavigationRoute.MyPage.route) -> LinkuNavigationItem.MY_PAGE
        else -> null
    }

    val isFileTab = currentLinkuNavigationItem == LinkuNavigationItem.FILE
    val shouldDimMyListMenu = isFileTab &&
        folderStateViewModel.bottomMenuExpanded &&
        (folderStateViewModel.navigationState is FileNavigationState.PersonalBottom ||
            folderStateViewModel.navigationState is FileNavigationState.PersonalLinks)

    // 액티비티 참조 + 두번뒤로 시간 기록
    // NOTE : 이미 구현된 DoubleBackToExitIfTop이 있어서 불필요함. 일단 주석 처리 후 추후 삭제?
//    val activity = remember(context) { context.findActivity() }
//    var lastBackPressed by remember { mutableLongStateOf(0L) }

    LaunchedEffect(saveLinkEntryTriggered) {
        if (saveLinkEntryTriggered) {
            navigator.navigate(SAVE_LINK_ROUTE)
            saveLinkEntryTriggered = false
        }
    }

    // NOTE : 이게 App에 있어야 할까..? MainActivity에 있는게 맞을 것 같은데, 리펙 가능한 부분인가..? 고민이 듬
    ThemeProvider {
        DoubleBackToExitIfTop(navigator = navigator)

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

        MainScreen(
            navigationBarProp = if (shouldShowNavigationBar) NavigationBarProp(
                currentLinkuNavigationItem = currentLinkuNavigationItem,
                onNavigate = { item ->
                    // 다른 탭을 선택한 경우에만 탭 내비게이션을 수행한다.
                    if (currentLinkuNavigationItem != item) {
                        // 목표 라우트
                        val route = when (item) {
                            LinkuNavigationItem.HOME -> NavigationRoute.Home.route
                            LinkuNavigationItem.FILE -> NavigationRoute.File.route
                            LinkuNavigationItem.CURATION -> NavigationRoute.Curation.route
                            LinkuNavigationItem.MY_PAGE -> NavigationRoute.MyPage.route
                        }

                        // 홈 화면만 백스택에 남겨두고 이동
                        navigator.navigate(route) {
                            popUpTo(NavigationRoute.Home.route) {
                                saveState = true
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else if (item == LinkuNavigationItem.FILE) {
                        // MainApp 범위 상태는 같은 탭 재선택만으로 초기화되지 않으므로 카테고리 루트로 되돌린다.
                        folderStateViewModel.resetSharedFolderState()
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
            hideNavigationBar = hideNavigationBar,
            statusBarDarkIcons = !isFileTab,
            // File은 흰 상태바 아이콘과 검은 내비게이션 아이콘을 서로 독립적으로 유지합니다.
            navigationBarDarkIcons = if (isFileTab) true else null,
            dimmed = shouldDimMyListMenu,
            searchOverlay = {
                // 검색 탑 시트 호출을 여기 한 곳으로 통일함. Home/File은 각자 데이터로 배선하지
                // 않고 onSearchOpen()으로 열기만 요청하며, 실제 상태(visible/쿼리/결과)는
                // searchViewModel(app 모듈)이 전담함.
                SearchBarTopSheet(
                    visible = searchVisible,
                    onDismiss = searchViewModel::dismissSearch,
                    onQueryChange = searchViewModel::search,
                    onQueryDelete = searchViewModel::removeRecentQuery,
                    onQueryClear = searchViewModel::clearRecentQueries,
                    onLinkClick = { userLinkuId ->
                        searchViewModel.dismissSearch()
                        navigator.navigate(linkDetailRoute(userLinkuId))
                    },
                    searchResults = searchResults,
                    uiState = searchUiState,
                )
            },
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
                            hideNavigationBar = true
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
                                    // 캐시된 닉네임을 먼저 반영한 뒤 홈으로 이동해야 "링큐" 기본값이
                                    // 잠깐 보였다가 실제 닉네임으로 바뀌는 깜빡임이 없음.
                                    viewModel.awaitCachedNickname()
                                    edgeToEdgeSystemBars = false
                                    hideNavigationBar = false
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
                     * 로그인 화면에서 초대 수락 결과에 맞는 공유 폴더 화면을 새 루트로 엽니다.
                     *
                     * @param acceptedResult 상세로 열 초대 수락 결과. 목록 갱신에 실패한 부분 성공이면
                     * `null`을 전달해 공유 폴더 그룹 화면으로 대체합니다.
                     */
                    fun openSharedFoldersFromLogin(
                        acceptedResult: AcceptSharedFolderInvitationResult.Accepted? = null,
                    ) {
                        if (navigator.currentDestination?.route == NavigationRoute.Login.route) {
                            showNavBar = true
                            if (acceptedResult == null) {
                                folderStateViewModel.resetSharedFolderState()
                                folderStateViewModel.showSharedFolderGroups()
                            } else {
                                folderStateViewModel.showAcceptedSharedFolder(acceptedResult)
                            }

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
                        onEdgeToEdgeChange = { hideStatusBar, hideNavBar ->
                            edgeToEdgeSystemBars = hideStatusBar
                            hideNavigationBar = hideNavBar
                        },
                        onLoginSuccess = {
                            showNavBar = true
                            viewModel.setAuthenticated(true)
                            edgeToEdgeSystemBars = false
                            hideNavigationBar = false

                            // 보류된 초대 토큰을 먼저 처리하고, 없으면 공유 폴더 ID를 처리합니다.
                            // 둘 다 없을 때만 정상 로그인 경로로 홈 화면을 엽니다.
                            val pendingInvitationToken =
                                deepLinkViewModel.consumePendingInvitation()

                            if (pendingInvitationToken.isNotBlank()) {
                                loginScope.launch {
                                    when (val result = fileViewModel.receiveSharedFolderInvitation(
                                        pendingInvitationToken
                                    )) {
                                        is AcceptSharedFolderInvitationResult.Accepted -> {
                                            openSharedFoldersFromLogin(result)
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
                                loginScope.launch {
                                    // 캐시된 닉네임을 먼저 반영한 뒤 홈으로 이동해야 "링큐" 기본값이
                                    // 잠깐 보였다가 실제 닉네임으로 바뀌는 깜빡임이 없음.
                                    viewModel.awaitCachedNickname()
                                    navigator.navigate(NavigationRoute.Home.route) {
                                        popUpTo("login_root") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                    navigateByNotification(it.type, it.targetId)
                                }
                                return@LoginApp
                            }

                            // pending 알림이 없는 경우의 기본 동작

                            showNavBar = true
                            loginScope.launch {
                                // 캐시된 닉네임을 먼저 반영한 뒤 홈으로 이동해야 "링큐" 기본값이
                                // 잠깐 보였다가 실제 닉네임으로 바뀌는 깜빡임이 없음.
                                viewModel.awaitCachedNickname()
                                navigator.navigate(NavigationRoute.Home.route) {
                                    popUpTo(NavigationRoute.Login.route) { inclusive = true }
                                    launchSingleTop = true
                                }
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
                            onSearchOpen = searchViewModel::openSearch,
                            nickname = nickname.orEmpty().ifBlank { "링큐" },
                            isNicknameLoading = isNicknameLoading,
                            onNavigateToSetting = {
                                navigator.navigate(NavigationRoute.AlarmSetting.route)
                            },
                            onNavigateToSaveLink = { url ->
                                linkViewModel.setSaveUrl(url)
                                navigator.navigate(SAVE_LINK_ROUTE)
                            },
                            onNavigateToLinkDetail = { userLinkuId ->
                                navigator.navigate(linkDetailRoute(userLinkuId))
                            },
                            onDeleteLink = { userLinkuId, onSuccess, onFailed ->
                                linkViewModel.deleteLink(
                                    userLinkuId = userLinkuId,
                                    onSucceed = onSuccess,
                                    onFailed = onFailed,
                                )
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
                            // 스플래시·로그인 화면을 거치지 않는 콜드 스타트 딥링크에서도 File
                            // 화면이 자신의 비몰입형 시스템 바 정책을 명시적으로 복원합니다.
                            edgeToEdgeSystemBars = false
                            hideNavigationBar = false
                        }

                        FileApp(
                            fileViewModel = fileViewModel,
                            folderStateViewModel = folderStateViewModel,
                            onNavigateToLinkDetail = { userLinkuId ->
                                navigator.navigate(linkDetailRoute(userLinkuId))
                            },
                            onNavigateToSharedLinkDetail = {
                                navigator.navigate(SHARED_LINK_DETAIL_ROUTE)
                            },
                            onSearchOpen = searchViewModel::openSearch,
                        )
                    }
                }

                // 큐레이션 파트 리팩토링 적용
                curationGraph(
                    navigator = navigator,
                    showNavBar = { showNavBar = it },
                    // 그래프 빌더는 최초 1회만 실행되므로 값이 아닌 람다로 넘겨 매번 최신 nickname을 읽게 함.
                    nickname = { nickname.orEmpty().ifBlank { "링큐" } },
                    onNavigateToSaveLink = { saveLinkEntryTriggered = true },
                    onNavigateToLinkDetail = { userLinkuId ->
                        navigator.navigate(linkDetailRoute(userLinkuId))
                    },
                )


                with(NavigationRoute.MyPage) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            myPageViewModel.loadUserInfo()
                        }
                        //FinishHandler()



                        MyPageApp(
                            viewModel = myPageViewModel,
                            // MyPageScreen(마이페이지 메인 화면)일 때만 하단 네비게이션 바를 표시.
                            // 계정설정/탈퇴/FAQ 등 마이페이지 내부 하위 화면에서는 숨김.
                            onShowNavBarChange = { showNavBar = it },
                            // 로그아웃/탈퇴 버튼을 누른 "즉시"(API 응답 기다리지 않고) 시스템 바를
                            // 몰입 모드로 전환함 — API 호출 및 Toast 표시 사이에 시스템 바가
                            // 잠깐 보였다가 사라지는 깜빡임을 없애기 위함. 실패해서 MyPage에 남으면
                            // MyPageApp이 false로 되돌림.
                            onImmersiveTransitionChange = {
                                edgeToEdgeSystemBars = it
                                hideNavigationBar = it
                            },
                            onLogoutToLogin = {
                                showNavBar = false
                                viewModel.setAuthenticated(false)
                                // onImmersiveTransitionChange(true)가 버튼 클릭 시점에 이미
                                // 처리했지만, 안전하게 한 번 더 명시함(대칭적으로 로그인 성공 시
                                // false로 되돌리는 것과 짝).
                                edgeToEdgeSystemBars = true
                                hideNavigationBar = true

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
                                navigator.clearBackStack(NavigationRoute.Curation.route)
                                navigator.clearBackStack(NavigationRoute.File.route)
                                navigator.clearBackStack(NavigationRoute.MyPage.route)
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
                            },
                            onNavigateToLinkDetail = { userLinkuId ->
                                navigator.navigate(linkDetailRoute(userLinkuId))
                            },
                            onDeleteLink = { userLinkuId, onSuccess, onFailed ->
                                linkViewModel.deleteLink(
                                    userLinkuId = userLinkuId,
                                    onSucceed = {
                                        // AI 목록에서 삭제한 링크가 홈의 최근 링크에도 남지 않도록
                                        // 홈 캐시를 같은 식별자로 즉시 정리한 뒤 화면 성공 처리를 알립니다.
                                        homeViewModel.onLinkDeleted(userLinkuId)
                                        onSuccess()
                                    },
                                    onFailed = onFailed,
                                )
                            },
                        )
                    }
                }

                with(NavigationRoute.AlarmSetting) {
                    setNavGraph {
                        LaunchedEffect(Unit) { showNavBar = false }

                        val notificationViewModel: NotificationViewModel = hiltViewModel()

                        AlarmSettingScreen(
                            onBackClick = { navigator.popBackStack() },
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
                            onNavigateToLinkDetail = { targetId ->
                                val userLinkuId = targetId
                                navigator.navigate(linkDetailRoute(userLinkuId))
                            },
                            onNavigateToFolder = { /* TODO */ },
                            onNavigateToCuration = { targetId ->
                                showNavBar = false
                                val cal = Calendar.getInstance()

                                // 알림 payload에 month 없으므로 로컬 현재 월을 yyyy-MM 형식으로 사용
                                val localMonth = "%04d-%02d".format(
                                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1
                                )
                                navigator.navigate("curation/detail/$localMonth/$targetId")
                            },
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

                composable(SAVE_LINK_ROUTE) {
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
                            val submittedUrl = linkUiState.saveUrl
                            val submittedClipboardCandidate =
                                homeViewModel.captureClipboardCandidate(submittedUrl)
                            linkViewModel.onSaveButtonClick(
                                onSucceed = { saved ->
                                    submittedClipboardCandidate?.let(
                                        homeViewModel::markClipboardCandidateHandled,
                                    )
                                    linkViewModel.loadLinkDetail(
                                        saved.userLinkuId,
                                    )
                                    linkViewModel.resetSaveForm()

                                    navigator.navigate(
                                        linkDetailRoute(saved.userLinkuId),
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
                    route = LINK_DETAIL_ROUTE_PATTERN,
                    arguments = listOf(
                        navArgument(LINK_DETAIL_USER_LINKU_ID_ARGUMENT) {
                            type = NavType.LongType
                        }
                    )
                ) { backStackEntry ->
                    val context = LocalContext.current
                    val detailCoroutineScope = rememberCoroutineScope()
                    val linkUiState by linkViewModel.uiState.collectAsStateWithLifecycle()

                    val userLinkuId = backStackEntry.arguments
                        ?.takeIf { it.containsKey(LINK_DETAIL_USER_LINKU_ID_ARGUMENT) }
                        ?.getLong(LINK_DETAIL_USER_LINKU_ID_ARGUMENT)
                        ?.takeIf { it > 0L }
                        ?: return@composable

                    val aiArticleViewModel: AIArticleViewModel = hiltViewModel(backStackEntry)
                    val aiArticleUiState by aiArticleViewModel.uiState.collectAsStateWithLifecycle()

                    var selectedDetailImageUri by rememberSaveable(userLinkuId) {
                        mutableStateOf<Uri?>(null)
                    }
                    var detailUserJobRequestFloor by remember(backStackEntry.id) {
                        mutableStateOf<Long?>(null)
                    }

                    val detailImagePicker = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        selectedDetailImageUri = uri
                    }

                    LaunchedEffect(backStackEntry.id, userLinkuId) {
                        // 같은 링크를 다시 열어도 이번 상세 진입 이후 성공한 직업 조회만 사용합니다.
                        detailUserJobRequestFloor = linkViewModel.loadUserBasics()
                        linkViewModel.loadLinkDetail(userLinkuId)
                        linkViewModel.loadLinkEditCategories()
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

                    val linkEditCategories = linkUiState.linkEditCategories
                    val fallbackCategoryColorStyle = CategoryColorStyle.DEFAULT

                    // 기존 공통 매핑을 읽기 전용으로 재사용해 서버의 네 가지 색상 단계를 보존합니다.
                    val categoryColorMap = remember(linkEditCategories) {
                        runCatching {
                            linkEditCategories.toCategoryColorStyleMap()
                        }.getOrDefault(emptyMap())
                    }

                    // 원본 목록 순서와 실제 ID를 유지하면서 이름과 색상 스타일을 UI 항목으로 묶습니다.
                    val categoryOptions = remember(
                        linkEditCategories,
                        categoryColorMap,
                        fallbackCategoryColorStyle,
                    ) {
                        linkEditCategories.map { category ->
                            LinkCategoryOption(
                                id = category.categoryId,
                                name = category.categoryName,
                                colorStyle = categoryColorMap[category.categoryName]
                                    ?: fallbackCategoryColorStyle,
                            )
                        }
                    }

                    // 라우트 ID와 일치하는 상세만 실제 화면에 전달해 이전 링크가 한 프레임도 노출되지 않게 합니다.
                    val linkDetail = linkUiState.linkDetail?.takeIf { detail ->
                        detail.userLinkuId == userLinkuId
                    }
                    val detailJobId = verifiedLinkDetailJobId(
                        jobId = linkUiState.jobId,
                        minimumUserJobRequestId = detailUserJobRequestFloor,
                        currentUserJobRequestId = linkUiState.userJobRequestId,
                        isUserJobReady = linkUiState.isUserJobReady,
                    )
                    val verifiedDetailUserJobRequestId =
                        linkUiState.userJobRequestId.takeIf { detailJobId != null }

                    LaunchedEffect(
                        linkDetail?.keyword,
                        linkDetail?.summary,
                    ) {
                        aiArticleViewModel.setLinkContent(
                            keyword = linkDetail?.keyword,
                            summary = linkDetail?.summary,
                        )
                    }

                    when {
                        linkDetail != null -> {
                            // 동일 ID 콘텐츠는 서버 재검증 중에도 유지하며 스켈레톤으로 다시 덮지 않습니다.
                            LinkDetailScreen(
                                userLinkuId = userLinkuId,
                                linkTitle = linkDetail.title,
                                categoryId = linkDetail.categoryId,
                                emotion = emotionNameOf(linkDetail.emotionId),
                                situationId = linkDetail.situationId,
                                jobId = detailJobId,
                                linkUrl = linkDetail.linku,
                                imageUrl = linkDetail.linkuImageUrl.toImageUrl(),
                                selectedImageUri = selectedDetailImageUri,
                                memo = linkDetail.memo.orEmpty(),
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
                                onDiscardSelectedImage = {
                                    selectedDetailImageUri = null
                                },
                                onSubmitEdit = { title, memo, categoryId, emotionId, situationIdToUpdate, onSuccess, onFailed ->
                                    val expectedUserJobRequestId =
                                        verifiedDetailUserJobRequestId
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
                                            expectedUserLinkuId = userLinkuId,
                                            image = selectedTempImage,
                                            title = title,
                                            memo = memo,
                                            categoryId = categoryId,
                                            emotionId = emotionId,
                                            situationIdToUpdate = situationIdToUpdate,
                                            expectedUserJobRequestId = expectedUserJobRequestId,
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

                        linkUiState.requestedLinkDetailId == userLinkuId &&
                            !linkUiState.isLoadingLinkDetail &&
                            linkUiState.linkDetailLoadError != null -> {
                            LinkDetailLoadErrorScreen(
                                onBack = {
                                    navigator.popBackStack()
                                },
                                onRetry = {
                                    linkViewModel.loadLinkDetail(
                                        userLinkuId = userLinkuId,
                                        forceRefresh = true,
                                    )
                                },
                            )
                        }

                        else -> {
                            // 첫 composition에서도 ID가 다른 이전 상세 대신 곧바로 스켈레톤을 렌더링합니다.
                            LinkDetailLoadingScreen(
                                onBack = {
                                    navigator.popBackStack()
                                },
                            )
                        }
                    }
                }

                composable(route = SHARED_LINK_DETAIL_ROUTE) {
                    val sharedLink = folderStateViewModel.selectedSharedLink

                    if (sharedLink == null) {
                        LaunchedEffect(Unit) { navigator.popBackStack() }
                    } else {
                        // 서버 카테고리 목록은 소유자별로 다를 수 있어, 고정된 16종 카테고리
                        // 마스터(CategoryType)로 categoryId를 이름·색상에 매핑합니다.
                        val sharedLinkCategoryType = sharedLink.categoryId?.let { categoryId ->
                            CategoryType.fromId(categoryId)
                        }

                        SharedLinkDetailScreen(
                            linkTitle = sharedLink.title,
                            linkUrl = sharedLink.url,
                            imageUrl = sharedLink.linkuImageUrl,
                            tags = sharedLink.tags,
                            categoryName = sharedLinkCategoryType?.tagName ?: "카테고리",
                            categoryColorStyle = sharedLinkCategoryType?.let { categoryType ->
                                CategoryColorStyle.categoryStyleList.getOrNull(categoryType.ordinal)
                            } ?: CategoryColorStyle.DEFAULT,
                            onBack = { navigator.popBackStack() },
                        )
                    }
                }

                composable(
                    route = OPEN_DEEP_LINK_ROUTE,
                    arguments = listOf(
                        openDeepLinkTokenArgument(),
                    ),
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = openDeepLinkUriPattern(deepLinkHost)
                        },
                        navDeepLink {
                            uriPattern = CUSTOM_SCHEME_OPEN_DEEP_LINK_URI_PATTERN
                        },
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
                                onOpenAcceptedSharedFolder = { acceptedResult ->
                                    folderStateViewModel.showAcceptedSharedFolder(acceptedResult)
                                },
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
            } // NavHost 끝

            // 검색 탑 시트가 열려있을 때 뒤로가기를 최우선으로 가로채 검색창만 닫음.
            // Scaffold의 content 슬롯(NavHost 포함)은 SubcomposeLayout이라 여기서 등록하는
            // 콜백이 NavHost 내부의 기본 뒤로가기(스택 pop)보다 항상 나중에 붙어 우선권을 가짐.
            // searchOverlay()는 Scaffold 바깥의 일반 컴포지션이라 그보다 먼저 등록되어
            // NavHost의 기본 pop에 밀렸었음 — 그래서 여기(NavHost와 같은 서브컴포지션, 그 뒤)에 둠.
            BackHandler(enabled = searchVisible) {
                searchViewModel.dismissSearch()
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
