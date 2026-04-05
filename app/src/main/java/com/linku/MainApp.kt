package com.linku

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.linku.design.theme.ThemeProvider
import com.linku.home.HomeViewModel
import com.linku.home.screen.SaveLinkResultScreen
import com.linku.home.screen.SaveLinkScreen


import com.linku.mypage.MyPageApp
import com.linku.mypage.MyPageViewModel
//import com.linku.mypage.MyPageScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument


import androidx.navigation.compose.currentBackStackEntryAsState
import com.linku.home.HomeApp
import java.io.File
import java.io.FileOutputStream

// 링크 공유 앱링크
import androidx.navigation.navDeepLink
import com.linku.curation.CurationViewModel
import com.linku.file.FileApp
import com.linku.file.FileViewModel
import com.linku.file.ui.theme.Gray600
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import com.linku.login.viewmodel.LoginViewModel

import dagger.hilt.android.EntryPointAccessors
import androidx.core.net.toUri
import com.linku.core.model.auth.LoginState
import com.linku.deeplink.DeepLinkHandlerViewModel
import com.linku.deeplink.appLinkRoute
import com.linku.design.modal.ModalWindow
import com.linku.linku_android.curation.curationGraph
import com.linku.login.navigation.LoginApp
import com.linku.navigation.LinkuNavigationItem


@Composable
fun MainApp(
    viewModel: MainViewModel,

    ) {

    // 앱 실행 시 실행하여 이전 계정 기록 삭제
    LaunchedEffect(Unit) {
        viewModel.clearRecentQuery()
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


    var showNavBar by remember { mutableStateOf(false) }

    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            Log.d("SOCIAL_VM", "LoginState.Success 감지 → 홈 이동")
            homeViewModel.refreshAfterLogin()
            //mypageViewModel.refreshUserInfo() //로그인시 세션을 주기에 불필요함.
            showNavBar = true
            navigator.navigate(NavigationRoute.Home.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }


    var saveLinkEntryTriggered by remember { mutableStateOf(false) }

    // 현재 라우트 관찰
    val navBackStackEntry by navigator.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    //루트 기반 자동 계싼으로 변경함.
    // 큐레이션으로 시작하는 모든 라우트를 큐레이션으로 인식하기에, 하위 화면에 있어도 바텀 네비게이션에
    // 큐레이션 탭이 선택된 상태를 유지할 수 있도록 함.
    // 하위 라우트 추가 시에도 바텀탭 선택 상태 유지를 위해 startsWith로 통일 상태 추가
    // TODO 지민 : 코드 확인 부탁.
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
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    var lastBackPressed by remember { mutableLongStateOf(0L) }

    LaunchedEffect(saveLinkEntryTriggered) {
        if (saveLinkEntryTriggered) {
            navigator.navigate("savelink")
            saveLinkEntryTriggered = false
        }
    }



    ThemeProvider {
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

            val app = LocalContext.current.applicationContext
            val deps = remember {
                EntryPointAccessors.fromApplication(app, SplashDeps::class.java)
            }



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

                        Splash(

                            onResult = {
                                val auth = deps.authPreference()
                                //스플래쉬에서 자동 로그인 조건 = refresh 토큰 존재 여부 확인
                                //자동 로그인 판단을 여기서 한다고 생각하면 됨.


                                val hasRefresh = !auth.refreshToken.isNullOrBlank()

                                //  이미 자동 로그인 시도했으면 강제 로그인
                                if (autoLoginTried) {
                                    navigator.navigate("login_root") {
                                        popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                                    }
                                    return@Splash
                                }

                                if (!hasRefresh) {
                                    // refresh 없음 → 로그인 화면으로 이동
                                    navigator.navigate("login_root") {
                                        popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                                    }

                                    return@Splash
                                }


                                // refresh 있음 → 자동로그인 시도
                                loginViewModel.tryAutoLogin(
                                    onSuccess = {
                                        navigator.navigate(NavigationRoute.Home.route) {
                                            popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    },
                                    onFail = {
                                        navigator.navigate("login_root") {
                                            popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                                        }

                                    }
                                )
                            }
                        )
                    }
                }

                composable("login_root") {
                    LoginApp(
                        //navController = navigator,
                        loginViewModel = loginViewModel,
                        showNavBar = { showNavBar = it },
                        onLoginSuccess = {
                            // 세선 정보가 저장 후, 홈 화면 데이터 즉시 로드
                            homeViewModel.refreshAfterLogin()
                            // 마이페이지 정보도 미리 로그(자연스럽게?)
                            // mypageViewModel.refreshUserInfo() //중복 호출 제거함.

                            showNavBar = true

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
                        }
                    )
                }


                with(NavigationRoute.Home) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true

                        }


                        HomeApp(
                            viewModel = homeViewModel,
                            onNavigateToMyPage = {  // TODO: 추후 알림 설정 페이지로 이동
                                navigator.navigate(NavigationRoute.MyPage.route) {
                                    popUpTo(navigator.graph.findStartDestination().id) {
                                        saveState = true
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
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

                            // 화면 진입 시 최신 정보 로드
                            mypageViewModel.refreshUserInfo()
                            //mypageViewModel.loadUserInfo()
                        }
                        //FinishHandler()



                        MyPageApp(
                            viewModel = mypageViewModel,
                            onLogoutToLogin = {
                                showNavBar = false  // 바텀바 끄기


                                homeViewModel.clearData()// 모든 홈 데이터를 초기화 - 이전 데이터 방지.
                                // 🔐 토큰/세션은 ViewModel 쪽에서 이미 정리한 뒤,
                                // 전역 스택을 지우고 로그인 루트로 이동
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

                // 딥링크 접속 시, 로그인이 안됐을 때 로그인 화면 -> 이걸 처리 어떻게 할지 몰라 일단 주석처리.
                composable(
                    route = "${NavigationRoute.Login.route}?showModal={showModal}",
                    arguments = listOf(navArgument("showModal") { type = NavType.BoolType; defaultValue = false })
                ) { backStackEntry ->

                    // 로그인 계열에서는 바텀바 숨김
                    LaunchedEffect(Unit) { showNavBar = false }

                    Log.d("MainApp", "딥링크 접속 시, 로그인이 안됐을 때 로그인 화면")

                    // ❶ 쓸데없는 자기-네비게이션 제거 (!!! 중요)
                    //   → 여기서는 머무르며 모달을 보여주고 로그인만 처리한다.

                    // ❷ showModal 안전하게 파싱
                    val showModal = backStackEntry.arguments?.getBoolean("showModal") == true

                    Log.d("MainApp", "showModal: $showModal")

                    // ❸ 모달 표시 상태는 저장 가능하게
                    var visible by rememberSaveable { mutableStateOf(true) }

                    Log.d("MainApp", "visible: $visible")

                    // 모달 떠 있을 때 → 뒤로가면 모달 닫기
                    if (showModal && visible) {

                        //백버튼 빠짐. 추가.
                        BackHandler {
                            visible = false
                        }


                        Log.d("MainApp", "On Modal")

                        ModalWindow(
                            visible = visible,
                            title = "접근 권한이 없습니다.",
                            onOkay = { visible = false },
                            onDismiss = { visible = false },
                            positiveText = "확인"
                        ) {
                            Text(
                                text = "링큐 회원만 폴더를 공유받을 수 있습니다.\n폴더를 확인하기 위해 로그인/회원가입 해주세요.",
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Normal,
                                color = Gray600,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    // 모달 닫힌 뒤 → 딥링크 로그인 화면 뒤로가기 = 앱 종료 처리
                    if (!showModal || !visible) {
                        BackHandler {
                            val now = System.currentTimeMillis()
                            if (now - lastBackPressed < 2000L) {
                                activity?.finish()
                            } else {
                                Toast.makeText(context, "뒤로 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                                lastBackPressed = now
                            }
                        }
                    }

                    // 로그인 상태는 화면에서 '수집'하고, 그 값을 Effect key로 사용 -> 정: sealed class로 변경했는데 문제 있으면 말씀해주세요.
                    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()
                    //Log.d("MainApp", "loginState: $loginState")


                    Log.d("MainApp", "loginState: $loginState")

                    LaunchedEffect(loginState) {
//                        val loggedIn = (loginState.result != null) && (loginState.errorTag == null) && !loginState.loading
//                        Log.d("MainApp", "loggedIn (deeplink): $loggedIn")

//                        if (loggedIn) {
                        if (loginState is LoginState.Success) {
                            Log.d("MainApp", "로그인 완료 (deeplink)")
                            // pending 공유 폴더가 있으면 처리
                            deepLinkViewModel.consumePendingShare()?.let { pendingFolderId ->
                                try {
                                    fileViewModel.receiveSharedFolder(pendingFolderId)
                                    fileViewModel.getSharedFolders()
                                    folderStateViewModel.updateIsSharedFolders(true)
                                } catch (_: Exception) { /* 네트워크/서버 오류 무시 */ }
                            }

                            // 파일 화면으로 이동하면서 Login 제거
                            navigator.navigate(NavigationRoute.File.route) {
                                popUpTo(NavigationRoute.Login.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }

                    // ❺ 실제 로그인 UI(AnimatedLoginScreen 등) 렌더링
                    // AnimatedLoginScreen(navigator = navigator)
                    val skipAnimation =
                        backStackEntry.savedStateHandle
                            .get<Boolean>("skip_login_animation") == true

//                    AnimatedLoginScreen( // 이전 딥링크 접속시, 로그인이 안됐을 때 로그인 화면 처리 확인을 몰라 일단 주석처리를 진행함.
//                        navigator = navigator, //TODO : 추후 수정하기
//                        skipAnimation = skipAnimation,
//                        onSignUpClick = {}
//                    )
                }



                // TODO: 로그인 되어 있지 않은 상황 처리 ?이게 뭐람
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
            val isAtTabRoot = showNavBar && when (currentRoute) {
                NavigationRoute.Home.route,
                NavigationRoute.File.route,
                NavigationRoute.Curation.route, // curation_graph의 리스트 루트
                NavigationRoute.MyPage.route -> true
                else -> false
            }

            // 루트에서만 '두 번 뒤로 종료'
            BackHandler(enabled = isAtTabRoot) {
                val now = System.currentTimeMillis()
                if (now - lastBackPressed < 2000L) {
                    activity?.finish()
                } else {
                    Toast.makeText(context, "뒤로 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                    lastBackPressed = now
                }
            }
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