package com.example.linku_android

import android.R.attr.type
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
import com.example.file.FileScreen
import com.example.home.screen.HomeScreen
import com.example.home.HomeViewModel
import com.example.home.screen.SaveLinkResultScreen
import com.example.home.screen.SaveLinkScreen
import com.example.linku_android.component.NavigationItem

//import com.example.login.LoginScreen
import com.example.mypage.MyPageApp
import com.example.mypage.MyPageViewModel
//import com.example.mypage.MyPageScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.navigation


import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.home.HomeApp
import com.example.curation.ui.CurationDetailScreen
import com.example.curation.ui.CurationScreen
import com.example.login.auth.AnimatedLoginScreen
import com.example.login.auth.EmailVerificationScreen
import com.example.login.auth.ServiceTermsScreen
import com.example.login.auth.PrivacyTermsScreenFixed
import com.example.login.auth.MarketingTermsScreenComposable
import com.example.login.auth.SignUpPasswordScreen
import com.example.login.auth.EmailLoginScreen
import com.example.login.auth.InterestContentScreen
import com.example.login.auth.InterestPurposeScreen
import com.example.login.auth.SignUpGenderScreen
import com.example.login.auth.SignUpNicknameScreen
import com.example.login.auth.SignUpJobScreen
import com.example.login.auth.TermsAgreementScreen
import com.example.login.auth.WelcomeScreen
import com.example.login.auth.ResetPasswordScreen
import com.example.login.auth.SignUpViewModel
import java.io.File
import java.io.FileOutputStream

// 링크 공유 앱링크
import androidx.navigation.navDeepLink
import com.example.core.error.UserIdNullException
import com.example.curation.CurationViewModel
import com.example.file.FileApp
import com.example.file.FileViewModel
import com.example.file.ui.modal.FileModalWindow
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray600
import com.example.file.viewmodel.folder.state.FolderStateViewModel
import com.example.linku_android.deeplink.DeepLinkHandlerViewModel
import com.example.linku_android.navigation.DoubleBackToExitIfTop
import com.example.login.auth.LoginViewModel
import com.example.login.auth.TermsAgreementSheet
import dagger.hilt.android.EntryPointAccessors


@Composable
fun MainApp(
    viewModel: MainViewModel,
) {


    // 앱 실행 시 실행하여 이전 계정 기록 삭제
    LaunchedEffect(Unit) {
        viewModel.clearRecentQuery()
    }

    val navigator = rememberNavController()
//    val isLoggedIn by viewModel.isLoggedInState.collectAsState()


    // 회원가입에서 사용할 뷰모델
    val signUpViewModel: SignUpViewModel = hiltViewModel() // 한 번만

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

    var currentNavigationItem by remember { mutableStateOf<NavigationItem?>(null) }
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

    //DoubleBackToExitIfTop(navigator)

    ThemeProvider {
        MainScreen(
            navigationBarProp = if (showNavBar) NavigationBarProp(
                currentNavigationItem = currentNavigationItem,
                onNavigate = { item ->
//                    if (item != currentNavigationItem) {
                        val route = when (item) {
                            NavigationItem.HOME -> NavigationRoute.Home.route
                            NavigationItem.FILE -> NavigationRoute.File.route
                            NavigationItem.CURATION -> NavigationRoute.Curation.route
                            NavigationItem.MY_PAGE -> NavigationRoute.MyPage.route
                        }
//                        navigator.navigate(route) {
//                            // 그래프의 시작지점까지 popUpTo 하면서 상태 저장
//                            popUpTo(navigator.graph.findStartDestination().id) {
//                                saveState = true
//                                inclusive = false
//                            }
//                            launchSingleTop = true
//                            // 이전에 저장된 상태 복원
//                            restoreState = true
//                        }
////                        navigator.navigate(route) {
////                            popUpTo(navigator.graph.startDestinationId) { inclusive = false }
////                        }


                        // 현재 화면의 route
                        val currentRoute = navigator.currentBackStackEntry?.destination?.route

//                        // 현재 route와 목표 route가 다를 때만 이동 (savelink 같은 중간 화면에서도 정상 동작)
//                        if (currentRoute != route) {
//                            navigator.navigate(route) {
//                                popUpTo(navigator.graph.findStartDestination().id) {
//                                    saveState = true
//                                    inclusive = false
//                                }
//                                launchSingleTop = true
//                                restoreState = true
//                            }
//                        }
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
//                    }
                },
                onCenterButtonClicked = {
                    // 여기에 중앙 버튼 눌렀을 때 로직 넣기
                    saveLinkEntryTriggered = true  // SaveLinkScreen으로 진입
                }
            ) else null,
            centerButtonProp = null // 바로 이동하므로 null



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

                        Splash(
                            onFinish = {
                                val auth = deps.authPreference()
                                val hasUserId = (auth.userId ?: -1L) > 0L
                                val hasAccess = !auth.accessToken.isNullOrBlank()
                                val hasRefresh = !auth.refreshToken.isNullOrBlank()

                                val canAutoLogin = hasUserId && hasAccess && hasRefresh   // ⬅️ 둘 다 있어야 자동 로그인

                                val target = if (canAutoLogin) NavigationRoute.Home.route else "auth_graph"
                                navigator.navigate(target) {
                                    popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )

//                        Splash(
//                            onFinish = {
//                                val uid = deps.authPreference().userId
//                                if (uid != null && uid > 0L) {
//                                    navigator.navigate(NavigationRoute.Home.route) {
//                                        popUpTo(NavigationRoute.Splash.route) { inclusive = true }
//                                        launchSingleTop = true
//                                    }
//                                } else {
//                                    navigator.navigate("auth_graph") {
//                                        popUpTo(NavigationRoute.Splash.route) { inclusive = true }
//                                        launchSingleTop = true
//                                    }
//                                }
//                            }
//                        )

//                        //  DataStore 구독
//                        val isLoggedIn by viewModel.sessionStore
//                            .isLoggedIn
//                            .collectAsStateWithLifecycle(initialValue = false)
//
//                        Splash(
//                            onFinish = {
//                                if (isLoggedIn) {
//                                    navigator.navigate(NavigationRoute.Home.route) {
//                                        popUpTo(NavigationRoute.Splash.route) { inclusive = true }
//                                        launchSingleTop = true
//                                    }
//                                } else {
//                                    navigator.navigate("auth_graph") {
//                                        popUpTo(NavigationRoute.Splash.route) { inclusive = true }
//                                        launchSingleTop = true
//                                    }
//                                }
//                            }
//                        )
                    }
                }
//                with(NavigationRoute.Splash) {
//                    setNavGraph {
//                        LaunchedEffect(Unit) { showNavBar = false }
//
//                        Splash(
//                            onFinish = {
//                                navigator.navigate("auth_graph") {
//                                    popUpTo(NavigationRoute.Splash.route) { inclusive = true }
//                                }
//                            }
//                        ) //스플래쉬 -> 이후, LoginScreen으로 이동하기.
//                        //추후, 로그인 된 상태라면 Home으로 이동할 수 있도록 수정해야함.
//                    }
//                }

                navigation(
                    route = "auth_graph",
                    startDestination = NavigationRoute.Login.route
                ) {
                //AnimatedLoginScreen으로 교체
                    composable(NavigationRoute.Login.route) { entry ->
                        LaunchedEffect(Unit) { showNavBar = false }

                        // 🔥 auth_graph 스코프의 동일 VM
                        val parentEntry = remember(entry) { navigator.getBackStackEntry("auth_graph") }
                        val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)

                        var showTermsSheet by rememberSaveable { mutableStateOf(false) }

                        // ✅ 약관 상세에서 돌아오면 시트 자동 재오픈
                        LaunchedEffect(Unit) {
                            navigator.currentBackStackEntry?.savedStateHandle
                                ?.getStateFlow("reopen_terms_sheet", false)
                                ?.collect { reopen ->
                                    if (reopen) {
                                        showTermsSheet = true
                                        navigator.currentBackStackEntry?.savedStateHandle
                                            ?.set("reopen_terms_sheet", false)
                                    }
                                }
                        }

                        Box(Modifier.fillMaxSize()) {
                            AnimatedLoginScreen(
                                navigator = navigator,
                                onSignUpClick = { showTermsSheet = true }
                            )

                            if (showTermsSheet) {
                                TermsAgreementSheet(
                                    navController = navigator,
                                    vm = signUpVm,                 // ⬅️ 넘겨서 동일 인스턴스 사용
                                    onClose = { showTermsSheet = false },
                                    // 상세로 갈 땐 시트를 닫고 이동
                                    onClickTerms = {
                                        showTermsSheet = false
                                        navigator.navigate("terms/service")
                                    },
                                    onClickPrivacy = {
                                        showTermsSheet = false
                                        navigator.navigate("terms/privacy")
                                    },
                                    onClickMarketing = {
                                        showTermsSheet = false
                                        navigator.navigate("terms/marketing")
                                    }
                                )
                            }
                        }
                    }
//                with(NavigationRoute.Login) {
//                    setNavGraph {
//                        LaunchedEffect(Unit) { showNavBar = false }
//                        //FinishHandler()
//                        AnimatedLoginScreen(navigator = navigator)
//                    }
//                }

                //스택 구조 상의 문제로, 우선 3개의 이용약관 여기에 넣음
                // 서비스 이용약관
                    composable("terms/service") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navigator.getBackStackEntry("auth_graph") }
                        val vm: SignUpViewModel = hiltViewModel(parentEntry)

                        ServiceTermsScreen(
                            onBackClicked = { navigator.popBackStack() },
                            onAgreeClicked = {
                                vm.setAgreeTerms(true)
                                // 🔔 로그인으로 돌아가면 시트를 다시 열라고 신호
                                navigator.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("reopen_terms_sheet", true)
                                navigator.popBackStack()
                            }
                        )
                    }

//                    ServiceTermsScreen(
//                        onBackClicked = { navigator.popBackStack() },
//                        onAgreeClicked = {
//                            vm.setAgreeTerms(true)   //  VM 업데이트
//                            navigator.popBackStack() //  뒤로만
//                        }
//                        onBackClicked = { navigator.popBackStack() },
//                        onAgreeClicked = {
//                            navigator.previousBackStackEntry
//                                ?.savedStateHandle
//                                ?.set("agree_terms", true)
//                            navigator.popBackStack() // 약관 선택 화면으로 복귀
//                        }
//                    )
//                }
//                composable("terms/service") {
//                    ServiceTermsScreen(
//                        onBackClicked = { navigator.popBackStack() },
//                        onAgreeClicked = { navigator.navigate("terms/privacy") } // 다음 약관으로 이동
//                    )
//                }

                // 개인정보 처리방침
                    composable("terms/privacy") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navigator.getBackStackEntry("auth_graph") }
                        val vm: SignUpViewModel = hiltViewModel(parentEntry)

                        PrivacyTermsScreenFixed(
                            onBackClicked = { navigator.popBackStack() },
                            onAgreeClicked = {
                                vm.setAgreePrivacy(true)
                                navigator.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("reopen_terms_sheet", true)
                                navigator.popBackStack()
                            }
                        )
                    }
//                    PrivacyTermsScreenFixed(
//                        onBackClicked = { navigator.popBackStack() },
//                        onAgreeClicked = {
//                            vm.setAgreePrivacy(true) // ✅
//                            navigator.popBackStack() // ✅
//                        }
//                    )
//                }
//                composable("terms/privacy") {
//                    PrivacyTermsScreenFixed(
//                        onBackClicked = { navigator.popBackStack() },
//                        onAgreeClicked = {
//                            navigator.previousBackStackEntry
//                                ?.savedStateHandle
//                                ?.set("agree_privacy", true)
//                            navigator.popBackStack()
//                        }
//                    )
//                }
//                composable("terms/privacy") {
//                    PrivacyTermsScreenFixed(
//                        onBackClicked = { navigator.popBackStack() },
//                        onAgreeClicked = { navigator.navigate("terms/marketing") } // 다음 약관으로 이동
//                    )
//                }

                // 마케팅 수신 동의
                    // 마케팅 수신 동의
                    composable("terms/marketing") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navigator.getBackStackEntry("auth_graph") }
                        val vm: SignUpViewModel = hiltViewModel(parentEntry)

                        MarketingTermsScreenComposable(
                            onBackClicked = { navigator.popBackStack() },
                            onAgreeClicked = {
                                vm.setAgreeMarketing(true)
                                navigator.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("reopen_terms_sheet", true)
                                navigator.popBackStack()
                            }
                        )
                    }
//                    MarketingTermsScreenComposable(
//                        onBackClicked = { navigator.popBackStack() },
//                        onAgreeClicked = {
//                            vm.setAgreeMarketing(true) // ✅
//                            navigator.popBackStack()   // ✅
//                        }
//                    )
//                }
                }
//                composable("terms/marketing") {
//                    MarketingTermsScreenComposable(
//                        onBackClicked = { navigator.popBackStack() },
//                        onAgreeClicked = {
//                            // 이후 진행 (예: 회원가입 완료 or 홈으로 이동 등)
//                            navigator.navigate("email_verification") {
//                                popUpTo("terms/service") { inclusive = true }
//                            }
//                        }
//                    )
//                }


                // 이메일 인증
                composable("email_verification") {
                    LaunchedEffect(Unit) { showNavBar = false }
                    //FinishHandler()
                    EmailVerificationScreen(navigator = navigator, signUpViewModel = signUpViewModel)
                }

                //ViewModel 사용
                composable("sign_up_password") {
                    LaunchedEffect(Unit) { showNavBar = false }



                    SignUpPasswordScreen(navigator = navigator, signUpViewModel = signUpViewModel)
                }

                //닉네임.
                composable("sign_up_nickname") {
                    SignUpNicknameScreen(navigator = navigator, signUpViewModel = signUpViewModel)
                }

                //성별
                composable("sign_up_gender") {
                    SignUpGenderScreen(navigator = navigator, signUpViewModel = signUpViewModel)
                }

                // 직업 선택 화면
                composable("sign_up_job") {
                    SignUpJobScreen(navigator = navigator, signUpViewModel = signUpViewModel)
                }

                // 목적 선택 화면
                composable("sign_up_purpose") {
                    //InterestPurposeScreen(navigator = navigator)
                    InterestPurposeScreen(navigator = navigator, signUpViewModel = signUpViewModel)
                }

                composable("sign_up_interest") {
//                    InterestContentScreen(
//                        navigator = navigator,
//                        signUpViewModel = hiltViewModel()
//                    )
                    InterestContentScreen(navigator = navigator, signUpViewModel = signUpViewModel)
                }

                // 회원가입 완료 → 환영 화면
                composable("welcome") {
                    WelcomeScreen(navigator = navigator, signUpViewModel = signUpViewModel)
                }

                composable("email_login") {
                    LaunchedEffect(Unit) { showNavBar = false }

                    // ✅ 로그인 상태 관찰
                    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()

                    // ✅ 로그인 성공 시 즉시 재로드
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
                    )
                }

//                composable("email_login") {
//                    //EmailLoginScreen(navigator = navigator)
//                    EmailLoginScreen(
//                        loginViewModel = loginViewModel,
//                        navigator = navigator,
////                        onLoginSuccess = {
////                            //  네비게이션 로직은 app 모듈에서만 관리
////                            navigator.navigate(NavigationRoute.Home.route) {
////                                popUpTo(NavigationRoute.Login.route) { inclusive = true }
////                            }
////                        }
//                    )
//                }

                //이메일 로그인 -> 회원가입
//                composable("terms_agreement") {
//                    TermsAgreementSheet(navController = navigator)
//                    //TermsAgreementScreen(navController = navigator)
//                }

                //비밀번호 재설정 화면
                composable("resetPassword") {
                    LaunchedEffect(Unit) { showNavBar = false }
                    //FinishHandler()
                    ResetPasswordScreen(navigator = navigator)
                }




                with(NavigationRoute.Home) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentNavigationItem = NavigationItem.HOME
                        }
                        //FinishHandler()

                        HomeApp(viewModel = homeViewModel)
                    }
                }

                with(NavigationRoute.File) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentNavigationItem = NavigationItem.FILE
                        }
                        //FinishHandler()
                        FileApp(
                            fileViewModel = fileViewModel,
                            folderStateViewModel = folderStateViewModel
                        )
                    }
                }

//                with(NavigationRoute.Curation) {
//                    setNavGraph {
//                        LaunchedEffect(Unit) {
//                            showNavBar = true
//                            currentNavigationItem = NavigationItem.CURATION
//                        }
//                        FinishHandler()
//                        CurationScreen(
//                            onOpenDetail = { navigator.navigate("curation_detail") }   //  디테일로 이동
//                        )
//                    }
//                }
//                composable("curation_detail") {
//                    // 바텀바 그대로 보이고 싶으면 showNavBar=true 유지
//                    CurationDetailScreen(
//                        onBack = { navigator.popBackStack() }   // ← 뒤로가기 처리
//                    )
//                }
                navigation(
                    startDestination = NavigationRoute.Curation.route, // 예: "curation"
                    route = "curation_graph"                           // 그래프 스코프 이름
                ) {
                    // 리스트(하이라이트) 화면
                    composable(NavigationRoute.Curation.route) { backStackEntry ->
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentNavigationItem = NavigationItem.CURATION
                        }
                        //FinishHandler()

                        // 그래프 스코프 BackStackEntry를 기억
                        val parentEntry = remember(backStackEntry) {
                            navigator.getBackStackEntry("curation_graph")
                        }
                        //그래프 스코프의 VM (재컴포지션/탭 전환에도 동일 인스턴스 유지)
                        val curationVm: com.example.curation.CurationViewModel = hiltViewModel(parentEntry)

                        CurationScreen(
                            viewModel = curationViewModel,
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
                        val homeVm: com.example.curation.CurationViewModel = hiltViewModel(parentEntry)

                        // 디테일 VM은 "현재 destination(backStack)" 스코프에서 생성해야 함!
                        val detailVm: com.example.curation.CurationDetailViewModel = hiltViewModel(backStack)

                        CurationDetailScreen(
                            userId = userId,
                            curationId = curationId,
                            detailViewModel = detailVm,   // 디테일 전용 VM
                            homeViewModel   = homeVm,     // 리스트 화면과 같은 CurationViewModel 공유
                            onBack = { navigator.popBackStack() }
                        )
                    }
                }

//                with(NavigationRoute.Curation) {
//                    setNavGraph {
//                        LaunchedEffect(Unit) {
//                            showNavBar = true
//                            currentNavigationItem = NavigationItem.CURATION
//                        }
//                        FinishHandler()
//
////                        CurationScreen(
////                            onOpenDetail = { userId: Long, curationId: Long ->
////                                // 상세로 넘길 값 저장
////                                navigator.currentBackStackEntry?.savedStateHandle?.set("userId", userId)
////                                navigator.currentBackStackEntry?.savedStateHandle?.set("curationId", curationId)
////                                // 파라미터 없는 단순 라우트로 이동
////                                navigator.navigate("curation_detail")
////                            }
////                        )
//                        CurationScreen(
//                            onOpenDetail = { userId: Long, curationId: Long ->
//                                navigator.navigate("curation_detail/$userId/$curationId")
//                            }
//                        )
//                    }
//                }
////                composable("curation_detail") {
////                    // 이전 화면에서 저장한 값 꺼내기
////                    val userId = navigator.previousBackStackEntry?.savedStateHandle?.get<Long>("userId")
////                    val curationId = navigator.previousBackStackEntry?.savedStateHandle?.get<Long>("curationId")
////
////                    if (userId == null || curationId == null) {
////                        // 값이 없으면 그냥 뒤로 가도 됨
////                        navigator.popBackStack()
////                        return@composable
////                    }
////
////                    CurationDetailScreen(
////                        userId = userId,
////                        curationId = curationId,
////                        onBack = { navigator.popBackStack() }
////                    )
////                }
//                // "curation_detail" → "curation_detail/{userId}/{curationId}"
//                composable("curation_detail/{userId}/{curationId}") { backStack ->
//                    val userId = backStack.arguments?.getString("userId")!!.toLong()
//                    val curationId = backStack.arguments?.getString("curationId")!!.toLong()
//
//                    CurationDetailScreen(
//                        userId = userId,
//                        curationId = curationId,
//                        onBack = { navigator.popBackStack() }
//                    )
//                }
                with(NavigationRoute.MyPage) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentNavigationItem = NavigationItem.MY_PAGE
                        }
                        //FinishHandler()

                        val mypageViewModel: MyPageViewModel = hiltViewModel()

                        MyPageApp(
                            viewModel = mypageViewModel,
                            onLogoutToLogin = {
                                showNavBar = false  // 바텀바 끄기
                                currentNavigationItem = null
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

//                composable("savelinkresult") {
//                    val vm: HomeViewModel = hiltViewModel()
//
//                    SaveLinkResultScreen(
//                        // 선택 이미지(없으면 null 유지)
//                        selectedImageUri = null,
//                        // 뷰모델이 들고 있는 상세 데이터
//                        link = vm.linkDetail,
//                        // 로딩 중 여부
//                        isLoading = vm.isLoadingLinkDetail,
//                        onBack = { navigator.popBackStack() }
//                    )
//                }
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
                                Uri.parse(fixed)
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
                    val showModal = backStackEntry.arguments?.getBoolean("showModal") ?: false

                    Log.d("MainApp", "showModal: $showModal")

                    // ❸ 모달 표시 상태는 저장 가능하게
                    var visible by rememberSaveable { mutableStateOf(true) }

                    Log.d("MainApp", "visible: $visible")

                    if (showModal && visible) {
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

                    // ❹ 로그인 상태는 화면에서 '수집'하고, 그 값을 Effect key로 사용
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
                    AnimatedLoginScreen(navigator = navigator)
                }



                // TODO: 앱 링크 처리
                // 링크 공유 앱링크
                composable(
                    route = "open?action={action}&folderId={folderId}",
                    arguments = listOf(
                        navArgument("action") { type = NavType.StringType; nullable = true },
                        navArgument("folderId") { type = NavType.LongType; nullable = false },
                    ),
                    deepLinks = listOf(
                        // 앱링크
                        navDeepLink { uriPattern = "linku://open?action={action}&folderId={folderId}" }
                    )
                ) { backStackEntry ->
                    val action = backStackEntry.arguments?.getString("action")
                    val folderId = backStackEntry.arguments?.getLong("folderId")

                    Log.d("MainApp", "action: $action, folderId: $folderId")

                    // 딱 한 번만 실행되게 LaunchedEffect 사용
                    LaunchedEffect(action, folderId) {
                        Log.d("MainApp", "LaunchedEffect 실행")

                        if (action == "share" && folderId != null) {
                            Log.d("MainApp", "파일 화면으로 이동")

                            // FileViewModel로 진입 폴더 설정 등 필요한 로직 실행
                            try{
                                Log.d("MainApp", "파일 화면으로 이동")

                                // 공유 받는 폴더 처리
                                fileViewModel.receiveSharedFolder(folderId)

                                Log.d("MainApp", "공유 받는 폴더 처리 완료")

                                // 파일 항목의 탑 바에 공유 받은 폴더 클릭 시와 같은 콜백
                                fileViewModel.getSharedFolders()
                                folderStateViewModel.updateIsSharedFolders(true)

                                Log.d("MainApp", "공유 받은 폴더 목록 및 상태 갱신 완료")

                                // 파일 화면으로 이동
                                navigator.navigate(NavigationRoute.File.route) {
                                    popUpTo(NavigationRoute.Splash.route) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }catch (e: Exception/*UserIdNullException*/) {
                                Log.e("MainApp", "Exception 발생: $e")
                                // (A) 미로그인: 대기 작업 저장 후 로그인 화면으로
                                deepLinkViewModel.setPendingShare(folderId)
                                navigator.navigate("${NavigationRoute.Login.route}?showModal=true") {
                                    popUpTo(NavigationRoute.Splash.route) { inclusive = false }
                                    launchSingleTop = true
                                }
//                                navigator.navigate(NavigationRoute.Login.route) {
//                                    popUpTo(NavigationRoute.Splash.route) { inclusive = false }
//                                    launchSingleTop = true
//                                }
                            }
                        }
                    }
                }

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

//    // 로그인 상태에 따라 네비게이션 처리
//    LaunchedEffect(key1 = isLoggedIn) {
//        isLoggedIn?.let { loggedIn ->
//            val targetRoute = if (loggedIn) NavigationRoute.Home.route else NavigationRoute.Login.route
//            navigator.navigate(targetRoute) {
//                popUpTo(navigator.graph.startDestinationId) { inclusive = false }
//            }
//        } ?: navigator.popBackStack(
//            destinationId = navigator.graph.startDestinationId,
//            inclusive = false
//        )
//    }
}

//@Composable
//private fun FinishHandler() {
//    val context = LocalContext.current
//    val activity = remember(context) { context.findActivity() }
//    var lastBackPressed by remember { mutableLongStateOf(0L) }
//
//    BackHandler {
//        val now = System.currentTimeMillis()
//        if (now - lastBackPressed < 2000L) {
//            activity?.finish()
//        } else {
//            Toast.makeText(context, "뒤로 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
//            lastBackPressed = now
//        }
//    }
//}

// 확장 함수: Context -> Activity
fun android.content.Context.findActivity(): android.app.Activity? {
    var ctx = this
    while (ctx is android.content.ContextWrapper) {
        if (ctx is android.app.Activity) return ctx
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
