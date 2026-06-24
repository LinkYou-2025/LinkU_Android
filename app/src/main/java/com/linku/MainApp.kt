package com.linku

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
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
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
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
import com.linku.core.model.auth.AutoLoginState
import com.linku.curation.CurationViewModel
import com.linku.deeplink.DeepLinkHandlerViewModel
import com.linku.deeplink.appLinkRoute
import com.linku.design.theme.ThemeProvider
import com.linku.file.FileApp
import com.linku.file.FileViewModel
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import com.linku.home.HomeApp
import com.linku.home.HomeViewModel
import com.linku.home.screen.SaveLinkResultScreen
import com.linku.home.screen.SaveLinkScreen
import com.linku.linku_android.curation.curationGraph
import com.linku.login.navigation.LoginApp
import com.linku.login.viewmodel.LoginViewModel
import com.linku.mypage.MyPageApp
import com.linku.mypage.MyPageViewModel
import com.linku.navigation.DoubleBackToExitIfTop
import com.linku.navigation.LinkuNavigationItem
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun MainApp(
    viewModel: MainViewModel,
) {

    val context = LocalContext.current
    val app = LocalContext.current.applicationContext

    // 네트워크 감지 추가
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    LaunchedEffect(isConnected) {
        if (!isConnected) {
            Toast.makeText(context, "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
            // TODO: 딤처리 (PM 확정 후 - 문구)
        }
    }

    // 앱 실행 시 실행하여 이전 계정 기록 삭제
    // FIXME : 지민님한테 여쭈어보기. 매번 앱 실행할 때마다 최근 기록을 지우는 것보다는 로그아웃 때 지우는건 어떤지
    LaunchedEffect(Unit) {
        viewModel.clearRecentQuery()

        val smallestWidth = app.resources.configuration.smallestScreenWidthDp
        val deviceType = if (smallestWidth >= 600) "TABLET" else "PHONE"
        viewModel.initDeviceInfo(deviceType)
    }

    val navigator = rememberNavController()

    // 로그인에서 사용할 뷰모델
    val loginViewModel: LoginViewModel = hiltViewModel()

    // 홈 화면에서 사용할 뷰모델
    val homeViewModel: HomeViewModel = hiltViewModel()
    // 로그인 혹은 자동 로그인 성공 후 생성함. 여기서는 이미 AuthPreference 주입 끝.

    // 파일 화면에서 사용할 뷰모델
    val fileViewModel: FileViewModel = hiltViewModel()
    val folderStateViewModel: FolderStateViewModel = viewModel()

    // 큐레이션 화면에서 사용할 뷰모델
    val curationViewModel: CurationViewModel = hiltViewModel()

    // 딥링크 접속 시 사용할 뷰모델
    val deepLinkViewModel: DeepLinkHandlerViewModel = hiltViewModel()

    // 마이페이지에서 사용할 뷰모델
    val mypageViewModel: MyPageViewModel = hiltViewModel()

    // TODO : 안드로이드 팀원들에게 물어보기. 이거 rememberSaveable로 변경을 해야하는 건 아닌지.
    var showNavBar by remember { mutableStateOf(false) }

    // TODO : 로그인 뷰모델에서 Success 상태로 바꾸기 전에 세션 갱신하게 수정해야함.
    // 기기가 3대라 이렇게 되면 사용자 정보가 따로 놀 수 있음.


    // FIXME : 변수를 거쳐서 네비게이션에 가야 하는지 궁금합니다. 변수를 제거하는건?
    var saveLinkEntryTriggered by remember { mutableStateOf(false) }

    // 현재 라우트 관찰
    val navBackStackEntry by navigator.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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

    // 알람 권한 요청 트리거 플래그
    //var requestNotificationPermission by remember { mutableStateOf(false) }

    // 권한 요청 런쳐
//    val notificationPermissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        viewModel.setNotificationEnabled(isGranted)
//        Log.d("MainApp", "알림 권한 요청 결과: $isGranted")
//    }


    // 플래그 감지 시 권한 요청 런쳐 실행
//    LaunchedEffect(requestNotificationPermission) {
//        if (requestNotificationPermission) {
//
//            // Android 13 이상에서는 POST_NOTIFICATIONS 런타임 권한이 필요하므로 조건부 요청
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            } else {
//                viewModel.setNotificationEnabled(true)
//            }
//            requestNotificationPermission = false // 한 번만 요청하도록 처리
//        }
//    }
    // NOTE : 이게 App에 있어야 할까..? MainActivity에 있는게 맞을 것 같은데, 리펙 가능한 부분인가..? 고민이 듬
    ThemeProvider {
        DoubleBackToExitIfTop(navigator = navigator)
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
            onFABClick = { saveLinkEntryTriggered = true }
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
                        LaunchedEffect(Unit) { showNavBar = false }

                        var autoLoginTried by rememberSaveable {
                            mutableStateOf(false)
                        }

                        val splashScope = rememberCoroutineScope()

                        val autoLoginState by loginViewModel.autoLoginState.collectAsStateWithLifecycle()

                        LaunchedEffect(autoLoginState) {
                            when (autoLoginState) {
                                is AutoLoginState.Success -> {
                                    homeViewModel.refreshAfterLogin()
                                    navigator.navigate(NavigationRoute.Home.route) {
                                        popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }

                                is AutoLoginState.Failed -> {
                                    navigator.navigate("login_root") {
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
                                        navigator.navigate("login_root") {
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

                composable("login_root") {
                    LaunchedEffect(Unit) { showNavBar = false }
                    LoginApp(
                        //navController = navigator,
                        loginViewModel = loginViewModel,
                        onLoginSuccess = {
                            showNavBar = true
                            // TODO: 지민님 딥링크 대기 작업 처리 확인 필요 요청하기.

                            // 로그인 성공 후 알림 권한 요청 트리거
                            //requestNotificationPermission = true

                            // 딥링크 대기 작업 처리 //지민아 이거 정리해줄 수 있어?
                            deepLinkViewModel.consumePendingShare()?.let { folderId ->
                                fileViewModel.receiveSharedFolder(folderId)
                                folderStateViewModel.updateIsSharedFolders(true)

                                navigator.navigate(NavigationRoute.File.route) {
                                    popUpTo("login_root") { inclusive = true }
                                    launchSingleTop = true
                                }
                                return@LoginApp
                            }


                            navigator.navigate(NavigationRoute.Home.route) {
                                popUpTo("login_root") { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onAutoLoginSuccess = {
                            showNavBar = true
                            homeViewModel.refreshAfterLogin()
                            navigator.navigate(NavigationRoute.Home.route) {
                                popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onAutoLoginFail = {
                            navigator.navigate("login_root") {
                                popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }


                with(NavigationRoute.Home) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                        }

                        // 홈 탭 진입 및 복귀 할 때마다 닉네임 갱신
                        LaunchedEffect(currentRoute) {
                            if (currentRoute == NavigationRoute.Home.route) {
                                viewModel.fetchNickname()
                            }
                        }

                        val nickname by viewModel.nickname.collectAsStateWithLifecycle()

                        HomeApp(
                            viewModel = homeViewModel,
                            nickname = nickname.orEmpty().ifBlank { "링큐" },
                            onNavigateToMyPage = {  // TODO: 추후 알림 설정 페이지로 이동
                                navigator.navigate(NavigationRoute.MyPage.route) {
                                    popUpTo(navigator.graph.findStartDestination().id) {
                                        saveState = true
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onShowNavBar = { showNavBar = it }
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
                            folderStateViewModel = folderStateViewModel
                        )
                    }
                }

                // 큐레이션 파트 리팩토링 적용
                curationGraph(
                    navigator = navigator,
                    showNavBar = { showNavBar = it }
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
                            onLogoutToLogin = {
                                showNavBar = false  // 바텀바 끄기


                                homeViewModel.clearData()// 모든 홈 데이터를 초기화 - 이전 데이터 방지.
                                // 🔐 토큰/세션은 ViewModel 쪽에서 이미 정리한 뒤,
                                // 전역 스택을 지우고 로그인 루트로 이동
                                viewModel.clearNickname()
                                navigator.navigate("login_root") {
                                    // 현재 내비게이션 그래프의 시작점(Splash 등)까지 모두 제거
                                    popUpTo(navigator.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    //popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
//                                navigator.navigate(NavigationRoute.Login.route) {
//                                    popUpTo(0) { inclusive = true } // 전체 스택 제거
//                                    launchSingleTop = true
//                                }
                            }
                        )
                    }
                }

                composable("savelink") {
                    val context = LocalContext.current
                    val vm: HomeViewModel = homeViewModel

                    // 갤러리 런처: Uri -> 임시 File 로 복사해서 뷰모델에 전달
                    val imagePicker = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        if (uri != null) {
                            runCatching { uri.toTempFile(context) }
                                .onSuccess { file -> vm.setImage(file) }
                                .onFailure {
                                    Toast.makeText(context, "이미지 로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }

                    SaveLinkScreen(
                        image = vm.image,
                        url = vm.url,
                        memo = vm.memo,
                        selectedEmotionId = vm.selectedEmotionId,
                        onPickImage = { imagePicker.launch("image/*") },
                        onUrlChange = vm::setUrl,
                        onMemoChange = vm::setMemo,
                        onEmotionSelect = vm::selectEmotion,
                        onSaveClick = {
                            // 저장 버튼 로그 + API 호출
                            Log.d("SaveLink", "try save -> url=${vm.url}, memo=${vm.memo}, emotionId=${vm.selectedEmotionId}, image=${vm.image?.name}")
                            vm.saveLink(
                                onSucceed = { saved ->
                                    Log.d("SaveLink", "success -> id=${saved.linkuId}, title=${saved.title}, domain=${saved.domain}")
                                    vm.loadLinkDetail(saved.linkuId)
                                    vm.resetForm()
                                    navigator.navigate("savelinkresult/${saved.linkuId}")
                                },
                                onFailed = { e ->
                                    Log.e("SaveLink", "failed: ${e.message}", e)
                                    Toast.makeText(context, e.message ?: "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onBack = { navigator.popBackStack() },
                        isCheckingUrl = vm.isCheckingUrl,
                        isDuplicateUrl = vm.isDuplicateUrl,
                        isInvalidLink = vm.isInvalidUrl
                    )
                }


                composable(
                    route = "savelinkresult/{linkuId}",
                    arguments = listOf(navArgument("linkuId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val vm: HomeViewModel = homeViewModel
                    val context = LocalContext.current
                    val linkuId = backStackEntry.arguments?.getLong("linkuId") ?: 0L

                    LaunchedEffect(linkuId) {
                        vm.loadLinkDetail(linkuId)
                        vm.loadCategoryColors()
                    }

                    // 진행률/색상 맵 수집
                    val aiProgress = vm.aiProgress.collectAsState().value
                    val categoryColorMap = vm.categoryColorMap.collectAsState().value

                    // 외부 브라우저 열기
                    fun openUrl(url: String) {
                        runCatching {
                            val fixed = if (url.startsWith("http")) url else "https://$url"
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                fixed.toUri()
                            )
                            context.startActivity(intent)
                        }.onFailure {
                            Toast.makeText(context, "링크를 열 수 없어요.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    SaveLinkResultScreen(
                        link = vm.linkDetail,
                        aiArticle = vm.aiArticleDetail,
                        isLoading = vm.isLoadingLinkDetail || vm.isLoadingAiArticle,
                        isAiLoading = vm.isLoadingAiArticle,
                        onBack = { navigator.popBackStack() },
                        onOpenLink = { url -> openUrl(url) },
                        categoryColorMap = categoryColorMap,
                        onSubmitEdit = { title, memo, categoryId, emotionId ->
                            vm.updateLink(
                                title = title,
                                memo = memo,
                                categoryId = categoryId,
                                emotionId = emotionId,
                                onSucceed = { Toast.makeText(context, "수정 완료", Toast.LENGTH_SHORT).show() },
                                onFailed = { e ->
                                    Log.e("SaveLinkResult", "수정 실패", e)
                                    Toast.makeText(context, e.message ?: "수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onRequestAiSummary = { vm.loadAiArticle(linkuId) },
                        aiProgress = aiProgress,
                        onCancelAi = { vm.cancelAiArticleJob() }
                    )
                }

                // 링크 공유 앱링크
                composable(
                    route = "open?action={action}&folderId={folderId}",
                    arguments = listOf(
                        navArgument("action") { type = NavType.StringType; nullable = true },
                        navArgument("folderId") { type = NavType.LongType; nullable = false },
                    ),
                    deepLinks = listOf(
                        // 앱링크
                        navDeepLink {
                            uriPattern = "https://linkuserver.store/open?action={action}&folderId={folderId}"
                        },
                        // 쿼리 순서가 바뀌는 경우까지 허용
                        navDeepLink {
                            uriPattern = "https://linkuserver.store/open?folderId={folderId}&action={action}"
                        }
                    )
                ) /*content = */ { backStackEntry ->
                    val action = backStackEntry.arguments?.getString("action")
                    val folderId = backStackEntry.arguments?.getLong("folderId")

                    Log.d("MainApp", "route: appLink action: $action, folderId: $folderId")

                    // 딱 한 번만 실행되게 LaunchedEffect 사용
                    LaunchedEffect(action, folderId) {
                        Log.d("MainApp", "route: appLink LaunchedEffect 실행")

                        appLinkRoute(
                            action = action,
                            folderId = folderId,
                            onReceiveSharedFolder = fileViewModel::receiveSharedFolder,
                            onUpdateIsSharedFolders = folderStateViewModel::updateIsSharedFolders,
                            onSetPendingShare = deepLinkViewModel::setPendingShare,
                            navigator = navigator
                        )

//                        if (action == "share" && folderId != null) {
//                            Log.d("MainApp", "route: appLink 파일 화면으로 이동")
//
//                            // FileViewModel로 진입 폴더 설정 등 필요한 로직 실행
//                            try{
//                                Log.d("MainApp", "route: appLink try 진입")
//
//                                // 공유 받는 폴더 처리, UI 업데이트 전 api 결과 우선을 위해 동기 처리.
//                                async{ fileViewModel.receiveSharedFolder(folderId) }.await()
//
//                                Log.d("MainApp", "route: appLink 공유 받는 폴더 처리 완료")
//
//                                // UI 업데이트
//                                folderStateViewModel.updateIsSharedFolders(true)
//
//                                Log.d("MainApp", "route: appLink 공유 받은 폴더 UI 갱신 완료")
//
//                                // 파일 화면으로 이동
//                                navigator.navigate(NavigationRoute.File.route) {
//                                    Log.d("MainApp", "route: appLink 파일 화면으로 이동")
//
//                                    popUpTo(NavigationRoute.Splash.route) { inclusive = false }
//                                    launchSingleTop = true
//
//                                    Log.d("MainApp", "route: appLink 파일 화면으로 이동 완료")
//                                }
//                            }catch (e: Exception/*UserIdNullException*/) {
//                                Log.e("MainApp", "Exception 발생: $e")
//                                // (A) 미로그인: 대기 작업 저장 후 로그인 화면으로
//                                deepLinkViewModel.setPendingShare(folderId)
//                                navigator.navigate("${NavigationRoute.Login.route}?showModal=true") {
//                                    Log.d("MainApp", "route: appLink 미로그인. 대기 작업 저장 후 로그인 화면으로")
//
//                                    popUpTo(NavigationRoute.Splash.route) { inclusive = false }
//                                    launchSingleTop = true
//
//                                    Log.d("MainApp", "route: appLink 로그인 화면으로 이동 완료")
//                                }
////                                navigator.navigate(NavigationRoute.Login.route) {
////                                    popUpTo(NavigationRoute.Splash.route) { inclusive = false }
////                                    launchSingleTop = true
////                                }
//                            }
//                        }
                    }
                }

//                composable(
//                    route = "open?action={action}&folderId={folderId}",
//                    arguments = listOf(
//                        navArgument("action") { type = NavType.StringType; nullable = true },
//                        navArgument("folderId") { type = NavType.LongType; nullable = false },
//                    ),
//                    deepLinks = listOf(
//                        // 앱링크
//                        navDeepLink {
//                            uriPattern = "linku://open?action={action}&folderId={folderId}"
//                        },
//
//                        navDeepLink {
//                            uriPattern = "linku://open?folderId={folderId}&action={action}"
//                        }
//                    )
//                ) /*content = */ { backStackEntry ->
//                    val action = backStackEntry.arguments?.getString("action")
//                    val folderId = backStackEntry.arguments?.getLong("folderId")
//
//                    Log.d("MainApp", "action: $action, folderId: $folderId")
//
//                    // 딱 한 번만 실행되게 LaunchedEffect 사용
//                    LaunchedEffect(action, folderId) {
//                        Log.d("MainApp", "LaunchedEffect 실행")
//
//                        appLinkRoute(
//                            action = action,
//                            folderId = folderId,
//                            onReceiveSharedFolder = fileViewModel::receiveSharedFolder,
//                            onUpdateIsSharedFolders = folderStateViewModel::updateIsSharedFolders,
//                            onSetPendingShare = deepLinkViewModel::setPendingShare,
//                            navigator = navigator
//                        )
//                    }
//                }
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



// 확장 함수: Context -> Activity
fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

private fun Uri.toTempFile(context: Context): File {
    val fileName = "picked_${System.currentTimeMillis()}.jpg"
    val tempFile = File(context.cacheDir, fileName)
    context.contentResolver.openInputStream(this).use { input ->
        FileOutputStream(tempFile).use { output ->
            input?.copyTo(output)
        }
    }
    return tempFile
}