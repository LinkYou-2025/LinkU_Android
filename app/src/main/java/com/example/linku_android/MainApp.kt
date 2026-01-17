package com.example.linku_android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
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
import com.example.design.theme.ThemeProvider
import com.example.home.HomeViewModel
import com.example.home.screen.SaveLinkResultScreen
import com.example.home.screen.SaveLinkScreen
import com.example.linku_android.navigation.LinkuNavigationItem

//import com.example.login.LoginScreen
import com.example.mypage.MyPageApp
import com.example.mypage.MyPageViewModel
//import com.example.mypage.MyPageScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.navigation


import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.home.HomeApp
import com.example.curation.ui.CurationDetailScreen
import com.example.curation.ui.CurationScreen
import com.example.login.ui.animation.AnimatedLoginScreen
import com.example.login.ui.screen.EmailVerificationScreen
import com.example.login.ui.terms.ServiceTermsScreen
import com.example.login.ui.terms.PrivacyTermsScreenFixed
import com.example.login.ui.terms.MarketingTermsScreenComposable
import com.example.login.ui.screen.SignUpPasswordScreen
import com.example.login.ui.screen.EmailLoginScreen
import com.example.login.ui.screen.InterestContentScreen
import com.example.login.ui.screen.InterestPurposeScreen
import com.example.login.ui.screen.SignUpGenderScreen
import com.example.login.ui.screen.SignUpNicknameScreen
import com.example.login.ui.screen.SignUpJobScreen
import com.example.login.ui.screen.WelcomeScreen
import com.example.login.ui.screen.ResetPasswordScreen
import com.example.login.viewmodel.SignUpViewModel
import java.io.File
import java.io.FileOutputStream

// 링크 공유 앱링크
import androidx.navigation.navDeepLink
import com.example.curation.CurationViewModel
import com.example.file.FileApp
import com.example.file.FileViewModel
import com.example.file.ui.modal.FileModalWindow
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray600
import com.example.file.viewmodel.folder.state.FolderStateViewModel
import com.example.linku_android.deeplink.DeepLinkHandlerViewModel
import com.example.login.viewmodel.LoginViewModel

import dagger.hilt.android.EntryPointAccessors
import androidx.core.net.toUri
import com.example.curation.CurationDetailViewModel
import com.example.linku_android.deeplink.appLinkRoute
import com.example.login.ui.bottom_sheet.TermsAgreementSheet


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

    // 파일 화면에서 사용할 뷰모델
    val fileViewModel: FileViewModel = hiltViewModel()
    val folderStateViewModel: FolderStateViewModel = viewModel()

    // 큐레이션 화면에서 사용할 뷰모델
    val curationViewModel: CurationViewModel = hiltViewModel()

    // 딥링크 접속 시 사용할 뷰모델
    val deepLinkViewModel: DeepLinkHandlerViewModel = hiltViewModel()

    var currentLinkuNavigationItem by remember { mutableStateOf<LinkuNavigationItem?>(null) }
    var showNavBar by remember { mutableStateOf(false) }

    var saveLinkEntryTriggered by remember { mutableStateOf(false) }

    // 현재 라우트 관찰
    val navBackStackEntry by navigator.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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



                        // 현재 화면의 route
                        val currentRoute = navigator.currentBackStackEntry?.destination?.route


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
            val loginVM: LoginViewModel = hiltViewModel()


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

                        Splash(

                            onResult = {
                                val auth = deps.authPreference()


                                val hasRefresh = !auth.refreshToken.isNullOrBlank()

                                if (!hasRefresh) {
                                    // refresh 없음 → 로그인 화면으로 이동
                                    navigator.navigate("auth_graph") {
                                        popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                    return@Splash
                                }

                                // refresh 있음 → 자동로그인 시도
                                loginVM.tryAutoLogin(
                                    onSuccess = {
                                        navigator.navigate(NavigationRoute.Home.route) {
                                            popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    },
                                    onFail = {
                                        navigator.navigate("auth_graph") {
                                            popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        )
                    }
                }


                navigation(
                    route = "auth_graph",
                    startDestination = NavigationRoute.Login.route
                ) {

                    /* ① Login composable */
                    composable(NavigationRoute.Login.route) { entry ->
                        val parentEntry = entry
                        val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)

                        val showTermsSheet by parentEntry.savedStateHandle
                            .getStateFlow("show_terms_sheet", false)
                            .collectAsStateWithLifecycle()


                        // 이메일 인증에서 백버튼으로 갔을 때, 약관 페이지 나오는게 맞는지.

                        //  이메일 인증에서 돌아오는지 확인
                        var cameFromEmail by remember { mutableStateOf(false) }

                        LaunchedEffect(navigator.currentBackStackEntry) {
                            if (parentEntry.savedStateHandle.get<Boolean>("from_email_verification") == true) {
                                cameFromEmail = true
                                parentEntry.savedStateHandle["show_terms_sheet"] = true

                                kotlinx.coroutines.delay(120)

                                cameFromEmail = false
                                parentEntry.savedStateHandle["from_email_verification"] = false
                            }
                        }

                        // 약간의 지연 + 재렌더링 위해 빈 박스 만듬.
                        if (cameFromEmail) {
                            Box(Modifier.fillMaxSize()) {}
                            return@composable
                        }


                        val skipAnimation =
                            parentEntry.savedStateHandle
                                .get<Boolean>("skip_login_animation") == true

                        AnimatedLoginScreen(
                            navigator = navigator,
                            skipAnimation = skipAnimation,   // 백버튼시 애니메이션 스탑 플래그 전달
                            onSignUpClick = {
                                parentEntry.savedStateHandle["show_terms_sheet"] = true
                            }
                        )

                    }

                    /* ② Service Terms */
                    composable("terms/service") { entry ->
                        val parentEntry = remember(entry) { navigator.getBackStackEntry("auth_graph") }
                        val vm: SignUpViewModel = hiltViewModel(parentEntry)

                        //시스템 백버튼 처리
                        BackHandler {
                            parentEntry.savedStateHandle["show_terms_sheet"] = true
                            navigator.popBackStack()
                        }

                        ServiceTermsScreen(
                            onBackClicked = {
                                parentEntry.savedStateHandle["show_terms_sheet"] = true
                                navigator.popBackStack()
                            },
                            onAgreeClicked = {
                                vm.setAgreeTerms(true)
                                parentEntry.savedStateHandle["show_terms_sheet"] = true
                                navigator.popBackStack()
                            }
                        )
                    }

                    /* ③ Privacy Terms */
                    composable("terms/privacy") { entry ->
                        val parentEntry = remember(entry) { navigator.getBackStackEntry("auth_graph") }
                        val vm: SignUpViewModel = hiltViewModel(parentEntry)

                        PrivacyTermsScreenFixed(
                            onBackClicked = {
                                parentEntry.savedStateHandle["show_terms_sheet"] = true
                                navigator.popBackStack()
                            },
                            onAgreeClicked = {
                                vm.setAgreePrivacy(true)
                                parentEntry.savedStateHandle["show_terms_sheet"] = true
                                navigator.popBackStack()
                            }
                        )
                    }

                    /* ④ Marketing Terms */
                    composable("terms/marketing") { entry ->
                        val parentEntry = remember(entry) { navigator.getBackStackEntry("auth_graph") }
                        val vm: SignUpViewModel = hiltViewModel(parentEntry)

                        MarketingTermsScreenComposable(
                            onBackClicked = {
                                parentEntry.savedStateHandle["show_terms_sheet"] = true
                                navigator.popBackStack()
                            },
                            onAgreeClicked = {
                                vm.setAgreeMarketing(true)
                                parentEntry.savedStateHandle["show_terms_sheet"] = true
                                navigator.popBackStack()
                            }
                        )
                    }

                    // 이메일 인증
                    composable("email_verification") { entry ->

                        val parentEntry = remember(entry) {
                            navigator.getBackStackEntry("auth_graph")
                        }


                        val vm: SignUpViewModel = hiltViewModel(parentEntry)
                        //백버튼으로 온 경우 애니메이션 적용X
                        BackHandler {
                            // 로그인 화면(AnimatedLoginScreen)에 애니메이션 스킵 플래그 전달함.
                            parentEntry.savedStateHandle["skip_login_animation"] = true
                            parentEntry.savedStateHandle["from_email_verification"] = true

                            navigator.popBackStack()
                        }

                        EmailVerificationScreen(
                            navigator = navigator,
                            parentEntry = parentEntry,     // ⬅ 추가
                            signUpViewModel = vm
                        )
                    }
                    //ViewModel 사용
                    // 비밀번호
                    composable("sign_up_password") { entry ->
                        val parentEntry = remember(entry) { navigator.getBackStackEntry("auth_graph") }
                        val vm: SignUpViewModel = hiltViewModel(parentEntry)

                        SignUpPasswordScreen(navigator = navigator, signUpViewModel = vm)
                    }

                    // 닉네임
                    composable("sign_up_nickname") { entry ->
                        val parentEntry = remember(entry) { navigator.getBackStackEntry("auth_graph") }
                        val vm: SignUpViewModel = hiltViewModel(parentEntry)

                        SignUpNicknameScreen(navigator = navigator, signUpViewModel = vm)
                    }

                    // 성별
                    composable("sign_up_gender") { entry ->
                        val parentEntry = remember(entry) { navigator.getBackStackEntry("auth_graph") }
                        val vm: SignUpViewModel = hiltViewModel(parentEntry)

                        SignUpGenderScreen(navigator = navigator, signUpViewModel = vm)
                    }

                    // 직업
                    composable("sign_up_job") { entry ->
                        val parentEntry = remember(entry) { navigator.getBackStackEntry("auth_graph") }
                        val vm: SignUpViewModel = hiltViewModel(parentEntry)

                        SignUpJobScreen(navigator = navigator, signUpViewModel = vm)
                    }

                    // 목적
                    composable("sign_up_purpose") { entry ->
                        val parentEntry = remember(entry) { navigator.getBackStackEntry("auth_graph") }
                        val vm: SignUpViewModel = hiltViewModel(parentEntry)

                        InterestPurposeScreen(navigator = navigator, signUpViewModel = vm)
                    }

                    // 관심사
                    composable("sign_up_interest") { entry ->
                        val parentEntry = remember(entry) { navigator.getBackStackEntry("auth_graph") }
                        val vm: SignUpViewModel = hiltViewModel(parentEntry)

                        InterestContentScreen(navigator = navigator, signUpViewModel = vm)
                    }

                    // 환영 화면
                    composable("welcome") { entry ->
                        val parentEntry = remember(entry) { navigator.getBackStackEntry("auth_graph") }
                        val vm: SignUpViewModel = hiltViewModel(parentEntry)

                        WelcomeScreen(navigator = navigator, signUpViewModel = vm)
                    }

                    composable("email_login") {

                        val parentEntry = remember {
                            navigator.getBackStackEntry("auth_graph")
                        }

                        val showTermsSheet by parentEntry.savedStateHandle
                            .getStateFlow("show_terms_sheet", false)
                            .collectAsStateWithLifecycle()

                        //약관 바텀시트 떠 있을 때 백버튼 = 시트 닫기
                        BackHandler(enabled = showTermsSheet) {
                            parentEntry.savedStateHandle["show_terms_sheet"] = false
                        }

                        LaunchedEffect(Unit) { showNavBar = false }

                        //  로그인 상태 관찰
                        val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()

                        //  로그인 성공 시 즉시 재로드
                        LaunchedEffect(loginState) {
                            val loggedIn = (loginState.result != null) &&
                                    (loginState.errorTag == null) &&
                                    !loginState.loading
                            if (loggedIn) {
                                // 큐레이션 재시도 가능하게 잠금 해제 후 로드
                                curationViewModel.invalidate()
                                curationViewModel.loadMonthlyCuration()

                                homeViewModel.refreshAfterLogin()
                                // (최소 변경 원하시면: homeViewModel.loadUserBasics(); homeViewModel.loadRecentLinks())

                                // 필요하면 Home/File 등 다른 화면도 같은 패턴으로 리프레시 트리거

                                // 그리고 홈으로 이동
                                navigator.navigate(NavigationRoute.Home.route) {
                                    popUpTo(NavigationRoute.Login.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }

                        EmailLoginScreen(
                            loginViewModel = loginViewModel,
                            navigator = navigator,
                            onSignUpClick = {
                                parentEntry.savedStateHandle["show_terms_sheet"] = true
                            }
                        )

                        // EmailLogin 위에서 바텀 시트 렌더
                        TermsAgreementSheet(
                            navController = navigator,
                            vm = hiltViewModel(parentEntry),
                            visible = showTermsSheet,
                            onClose = {
                                parentEntry.savedStateHandle["show_terms_sheet"] = false
                            },
                            onClickTerms = {
                                parentEntry.savedStateHandle["show_terms_sheet"] = false
                                navigator.navigate("terms/service")
                            },
                            onClickPrivacy = {
                                parentEntry.savedStateHandle["show_terms_sheet"] = false
                                navigator.navigate("terms/privacy")
                            },
                            onClickMarketing = {
                                parentEntry.savedStateHandle["show_terms_sheet"] = false
                                navigator.navigate("terms/marketing")
                            }
                        )
                    }


                    //비밀번호 재설정 화면
                    composable("resetPassword") {
                        LaunchedEffect(Unit) { showNavBar = false }
                        //FinishHandler()
                        ResetPasswordScreen(navigator = navigator)
                    }
                }

                with(NavigationRoute.Home) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentLinkuNavigationItem = LinkuNavigationItem.HOME
                        }


                        HomeApp(viewModel = homeViewModel)
                    }
                }

                with(NavigationRoute.File) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentLinkuNavigationItem = LinkuNavigationItem.FILE
                        }

                        FileApp(
                            fileViewModel = fileViewModel,
                            folderStateViewModel = folderStateViewModel
                        )
                    }
                }


                navigation(
                    startDestination = NavigationRoute.Curation.route, // 예: "curation"
                    route = "curation_graph"                           // 그래프 스코프 이름
                ) {
                    // 리스트(하이라이트) 화면
                    composable(NavigationRoute.Curation.route) { backStackEntry ->
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentLinkuNavigationItem = LinkuNavigationItem.CURATION
                        }


                        // 그래프 스코프 BackStackEntry를 기억
                        val parentEntry = remember(backStackEntry) {
                            navigator.getBackStackEntry("curation_graph")
                        }
                        //그래프 스코프의 VM (재컴포지션/탭 전환에도 동일 인스턴스 유지)
                        val curationVm: CurationViewModel = hiltViewModel(parentEntry)

                        CurationScreen(
                            viewModel = curationVm,
                            onOpenDetail = { userId: Long, curationId: Long ->
                                navigator.navigate("curation_detail/$userId/$curationId") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    // 디테일 화면
                    composable("curation_detail/{userId}/{curationId}") { backStack ->
                        val userId = backStack.arguments?.getString("userId")!!.toLong()
                        val curationId = backStack.arguments?.getString("curationId")!!.toLong()

                        // 같은 그래프 스코프의 홈 VM은 parent에서
                        val parentEntry = remember(backStack) {
                            navigator.getBackStackEntry("curation_graph")
                        }
                        val homeVm: CurationViewModel = hiltViewModel(parentEntry)

                        // 디테일 VM은 "현재 destination(backStack)" 스코프에서 생성해야 함!
                        val detailVm: CurationDetailViewModel = hiltViewModel(backStack)

                        CurationDetailScreen(
                            userId = userId,
                            curationId = curationId,
                            detailViewModel = detailVm,   // 디테일 전용 VM
                            homeViewModel   = homeVm,     // 리스트 화면과 같은 CurationViewModel 공유
                            onBack = { navigator.popBackStack() }
                        )
                    }
                }


                with(NavigationRoute.MyPage) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentLinkuNavigationItem = LinkuNavigationItem.MY_PAGE
                        }
                        //FinishHandler()

                        val mypageViewModel: MyPageViewModel = hiltViewModel()

                        MyPageApp(
                            viewModel = mypageViewModel,
                            onLogoutToLogin = {
                                showNavBar = false  // 바텀바 끄기
                                currentLinkuNavigationItem = null
                                // 🔐 토큰/세션은 ViewModel 쪽에서 이미 정리한 뒤,
                                // 전역 스택을 지우고 로그인 루트로 이동
                                navigator.navigate(NavigationRoute.Login.route) {
                                    popUpTo(0) { inclusive = true } // 전체 스택 제거
                                    launchSingleTop = true
                                }
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

                // 딥링크 접속 시, 로그인이 안됐을 때 로그인 화면
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

                        FileModalWindow(
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
                                fontFamily = DefaultFont,
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

                    // 로그인 상태는 화면에서 '수집'하고, 그 값을 Effect key로 사용
                    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()

                    Log.d("MainApp", "loginState: $loginState")

                    LaunchedEffect(loginState) {
                        val loggedIn = (loginState.result != null) && (loginState.errorTag == null) && !loginState.loading
                        Log.d("MainApp", "loggedIn (deeplink): $loggedIn")

                        if (loggedIn) {
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

                    AnimatedLoginScreen(
                        navigator = navigator,
                        skipAnimation = skipAnimation,
                        onSignUpClick = {}
                    )
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
    while (ctx is android.content.ContextWrapper) {
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
