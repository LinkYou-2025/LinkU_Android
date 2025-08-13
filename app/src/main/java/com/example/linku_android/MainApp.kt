package com.example.linku_android


import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.design.theme.ThemeProvider
import com.example.file.FileScreen
import com.example.home.HomeScreen
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

// 링크 공유 앱링크
import androidx.navigation.navDeepLink
import com.example.core.error.UserIdNullException
import com.example.file.FileViewModel
import com.example.file.ui.modal.FileModalWindow
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray600
import com.example.file.viewmodel.folder.state.FolderStateViewModel
import com.example.linku_android.deeplink.DeepLinkHandlerViewModel
import com.example.login.auth.LoginViewModel


@Composable
fun MainApp(
    viewModel: MainViewModel,
) {
    val navigator = rememberNavController()
//    val isLoggedIn by viewModel.isLoggedInState.collectAsState()

    // 회원가입에서 사용할 뷰모델
    val signUpViewModel: SignUpViewModel = hiltViewModel() // 한 번만

    // 로그인에서 사용할 뷰모델
    val loginViewModel: LoginViewModel = hiltViewModel()

    // 파일 화면에서 사용할 뷰모델
    val fileViewModel: FileViewModel = hiltViewModel()
    val folderStateViewModel: FolderStateViewModel = viewModel()

    // 딥링크 접속 시 사용할 뷰모델
    val deepLinkViewModel: DeepLinkHandlerViewModel = hiltViewModel()

    var currentNavigationItem by remember { mutableStateOf<NavigationItem?>(null) }
    var showNavBar by remember { mutableStateOf(false) }

    var saveLinkEntryTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(saveLinkEntryTriggered) {
        if (saveLinkEntryTriggered) {
            navigator.navigate("savelink")
            saveLinkEntryTriggered = false
        }
    }

    ThemeProvider {
        MainScreen(
            navigationBarProp = if (showNavBar) NavigationBarProp(
                currentNavigationItem = currentNavigationItem,
                onNavigate = { item ->
                    if (item != currentNavigationItem) {
                        val route = when (item) {
                            NavigationItem.HOME -> NavigationRoute.Home.route
                            NavigationItem.FILE -> NavigationRoute.File.route
                            NavigationItem.CURATION -> NavigationRoute.Curation.route
                            NavigationItem.MY_PAGE -> NavigationRoute.MyPage.route
                        }
                        navigator.navigate(route) {
                            popUpTo(navigator.graph.startDestinationId) { inclusive = false }
                        }
                    }
                },
                onCenterButtonClicked = {
                    // 여기에 중앙 버튼 눌렀을 때 로직 넣기
                    saveLinkEntryTriggered = true  // SaveLinkScreen으로 진입
                }
            ) else null,
            centerButtonProp = null // 바로 이동하므로 null
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

                        Splash(
                            onFinish = {
                                navigator.navigate(NavigationRoute.Login.route) {
                                    popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                                }
                            }
                        ) //스플래쉬 -> 이후, LoginScreen으로 이동하기.
                        //추후, 로그인 된 상태라면 Home으로 이동할 수 있도록 수정해야함.
                    }
                }
                //AnimatedLoginScreen으로 교체
                with(NavigationRoute.Login) {
                    setNavGraph {
                        LaunchedEffect(Unit) { showNavBar = false }
                        FinishHandler()
                        AnimatedLoginScreen(navigator = navigator)
                    }
                }

                //스택 구조 상의 문제로, 우선 3개의 이용약관 여기에 넣음
                // 서비스 이용약관
                composable("terms/service") {
                    ServiceTermsScreen(
                        onBackClicked = { navigator.popBackStack() },
                        onAgreeClicked = { navigator.navigate("terms/privacy") } // 다음 약관으로 이동
                    )
                }

                // 개인정보 처리방침
                composable("terms/privacy") {
                    PrivacyTermsScreenFixed(
                        onBackClicked = { navigator.popBackStack() },
                        onAgreeClicked = { navigator.navigate("terms/marketing") } // 다음 약관으로 이동
                    )
                }

                // 마케팅 수신 동의
                composable("terms/marketing") {
                    MarketingTermsScreenComposable(
                        onBackClicked = { navigator.popBackStack() },
                        onAgreeClicked = {
                            // 이후 진행 (예: 회원가입 완료 or 홈으로 이동 등)
                            navigator.navigate("email_verification") {
                                popUpTo("terms/service") { inclusive = true }
                            }
                        }
                    )
                }


                // 이메일 인증
                composable("email_verification") {
                    LaunchedEffect(Unit) { showNavBar = false }
                    FinishHandler()
                    EmailVerificationScreen(navigator = navigator, signUpViewModel = signUpViewModel)
                }

                //ViewModel 사용
                composable("sign_up_password") {
                    LaunchedEffect(Unit) { showNavBar = false }
                    FinishHandler()
                    //SignUpPasswordScreen(navigator = navigator)
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
                    //EmailLoginScreen(navigator = navigator)
                    EmailLoginScreen(
                        loginViewModel = loginViewModel,
                        navigator = navigator,
//                        onLoginSuccess = {
//                            //  네비게이션 로직은 app 모듈에서만 관리
//                            navigator.navigate(NavigationRoute.Home.route) {
//                                popUpTo(NavigationRoute.Login.route) { inclusive = true }
//                            }
//                        }
                    )
                }

                //이메일 로그인 -> 회원가입
                composable("terms_agreement") {
                    TermsAgreementScreen(navController = navigator)
                }

                //비밀번호 재설정 화면
                composable("resetPassword") {
                    LaunchedEffect(Unit) { showNavBar = false }
                    FinishHandler()
                    ResetPasswordScreen(navigator = navigator)
                }




                with(NavigationRoute.Home) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentNavigationItem = NavigationItem.HOME
                        }
                        FinishHandler()
                        val homeViewModel: HomeViewModel = hiltViewModel()
                        HomeScreen(
                            userName = "지현",
                            recentLinks = homeViewModel.recentLinks
                        )
                    }
                }

                with(NavigationRoute.File) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentNavigationItem = NavigationItem.FILE
                        }
                        FinishHandler()
                        FileScreen(
                            fileViewModel = fileViewModel,
                            folderStateViewModel = folderStateViewModel
                        )
                    }
                }

                with(NavigationRoute.Curation) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentNavigationItem = NavigationItem.CURATION
                        }
                        FinishHandler()
                        CurationScreen(
                            onOpenDetail = { navigator.navigate("curation_detail") }   // ✅ 디테일로 이동
                        )
                    }
                }
                composable("curation_detail") {
                    // 바텀바 그대로 보이고 싶으면 showNavBar=true 유지
                    CurationDetailScreen(
                        onBack = { navigator.popBackStack() }   // ← 뒤로가기 처리
                    )
                }

                with(NavigationRoute.MyPage) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentNavigationItem = NavigationItem.MY_PAGE
                        }
                        FinishHandler()

                        val mypageViewModel: MyPageViewModel = hiltViewModel()
                        MyPageApp(mypageViewModel)
                    }
                }

                composable("savelink") {
                    SaveLinkScreen(
                        onSaveSuccess = {
                            navigator.navigate("savelinkresult")
                        }
                    )
                }

                composable("savelinkresult") {
                    SaveLinkResultScreen()
                }

                // 딥링크 접속 시, 로그인이 안됐을 때 로그인 화면
                composable(
                    "${NavigationRoute.Login.route}?showModal={showModal}",
                    arguments = listOf(navArgument("showModal"){ type = NavType.BoolType; defaultValue = false })
                ) { backStackEntry ->

                    LaunchedEffect(Unit) { showNavBar = false }
                    FinishHandler()
                    AnimatedLoginScreen(navigator = navigator)

                    val showModal = backStackEntry.arguments?.getBoolean("showModal") ?: false
                    var visible by remember { mutableStateOf(true) }
                    if (showModal) {
                        FileModalWindow(
                            visible = visible,
                            title = "접근 권한이 없습니다.",
                            onOkay = {visible = false},
                            onDismiss = {},
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

                    LaunchedEffect(loginViewModel.loginState) {
                        val loggedIn = loginViewModel.loginState != null
                        if (loggedIn) {
                            // 1) 대기 중인 폴더가 있으면 꺼내서
                            deepLinkViewModel.consumePendingShare()?.let { pendingFolderId ->
                                // 2) 재실행
                                try {
                                    fileViewModel.receiveSharedFolder(pendingFolderId)
                                } catch (_: Exception) {
                                    // 여기까지 왔는데 또 실패하면(아주 이례적) 폴백만
                                }
                                // 3) 파일 화면으로 이동
                                navigator.navigate(NavigationRoute.File.route) {
                                    popUpTo(navigator.graph.findStartDestination().id) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                }


                // TODO: 앱 링크 처리
                // 링크 공유 앱링크
                composable(
                    route = "open?action={action}&folderId={folderId}",
                    arguments = listOf(
                        navArgument("action") { type = NavType.StringType; nullable = true },
                        navArgument("folderId") { type = NavType.StringType; nullable = true },
                    ),
                    deepLinks = listOf(
                        // HTTPS 앱링크
                        navDeepLink { uriPattern = "linku://open?action={action}&folderId={folderId}" }
                    )
                ) { backStackEntry ->
                    val action = backStackEntry.arguments?.getString("action")
                    val folderId = backStackEntry.arguments?.getString("folderId")?.toLongOrNull()

                    Log.d("MainApp", "action: $action, folderId: $folderId")

                    // 딱 한 번만 실행되게 LaunchedEffect 사용
                    LaunchedEffect(action, folderId) {
                        Log.d("MainApp", "LaunchedEffect 실행")

                        if (action == "share" && folderId != null) {
                            Log.d("MainApp", "파일 화면으로 이동")

                            // FileViewModel로 진입 폴더 설정 등 필요한 로직 실행
                            try{

                                // 공유 받는 폴더 처리
                                fileViewModel.receiveSharedFolder(folderId)

                                // 파일 항목의 탑 바에 공유 받은 폴더 클릭 시와 같은 콜백
                                fileViewModel.getSharedFolders()
                                folderStateViewModel.updateIsSharedFolders(true)

                                // 파일 화면으로 이동
                                navigator.navigate(NavigationRoute.File.route) {
                                    popUpTo(NavigationRoute.Splash.route) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }catch (e: Exception/*UserIdNullException*/) {
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

@Composable
private fun FinishHandler() {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    var lastBackPressed by remember { mutableLongStateOf(0L) }

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

// 확장 함수: Context -> Activity
fun android.content.Context.findActivity(): android.app.Activity? {
    var ctx = this
    while (ctx is android.content.ContextWrapper) {
        if (ctx is android.app.Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}