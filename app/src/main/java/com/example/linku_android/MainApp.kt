package com.example.linku_android


import android.R.attr.type
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.example.mypage.screen.MyPageScreen
//import com.example.mypage.MyPageScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument


import androidx.navigation.compose.composable
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


@Composable
fun MainApp(
    viewModel: MainViewModel,
) {
    val navigator = rememberNavController()
//    val isLoggedIn by viewModel.isLoggedInState.collectAsState()
    val signUpViewModel: SignUpViewModel = hiltViewModel() // 한 번만
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
//                            viewModel = hiltViewModel()
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