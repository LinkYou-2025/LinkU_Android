package com.example.login

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.login.ui.animation.AnimatedLoginScreen
import com.example.login.ui.bottom_sheet.TermsAgreementSheet
import com.example.login.ui.screen.email.EmailLoginScreen
import com.example.login.ui.screen.email.EmailVerificationScreen
import com.example.login.ui.screen.email.InterestContentScreen
import com.example.login.ui.screen.email.InterestPurposeScreen
import com.example.login.ui.screen.email.ResetPasswordScreen
import com.example.login.ui.screen.email.SignUpGenderScreen
import com.example.login.ui.screen.email.SignUpJobScreen
import com.example.login.ui.screen.email.SignUpNicknameScreen
import com.example.login.ui.screen.email.SignUpPasswordScreen
import com.example.login.ui.screen.email.WelcomeScreen
import com.example.login.ui.terms.MarketingTermsScreenComposable
import com.example.login.ui.terms.PrivacyTermsScreenFixed
import com.example.login.ui.terms.ServiceTermsScreen
import com.example.login.viewmodel.LoginViewModel
import com.example.login.viewmodel.SignUpViewModel
//import com.example.linku_android.deeplink.DeepLinkHandlerViewModel
import androidx.navigation.compose.NavHost
import com.example.login.ui.screen.social.SocialEntryScreen
import com.example.login.ui.screen.social.SocialGenderScreen
import com.example.login.ui.screen.social.SocialInterestScreen
import com.example.login.ui.screen.social.SocialJobScreen
import com.example.login.ui.screen.social.SocialNicknameScreen
import com.example.login.ui.screen.social.SocialPurposeScreen
import com.example.login.ui.screen.social.WelcomeSocialScreen
import com.example.login.viewmodel.EmailAuthViewModel
import com.example.login.viewmodel.SocialAuthViewModel

/**
 * 안전하게 auth_graph의 BackStackEntry를 가져오는 확장 함수
 *  @param currentEntry 현재 composable의 NavBackStackEntry
 *  @return auth_graph의 NavBackStackEntry 또는 null (백스택에 없는 경우)
 * */
private fun NavHostController.getAuthGraphEntry(
    currentEntry: NavBackStackEntry
): NavBackStackEntry? {
    return runCatching {
        getBackStackEntry("auth_graph")
    }.getOrNull()
}

/**
 * parentEntry가 null일 때 로그인 화면으로 안전하게 이동
 * 피드백 반영해서 수정함.
* */
@Composable
private fun NavigateToLoginOnError(navController: NavHostController) {
    LaunchedEffect(Unit) {
        navController.navigate("login") {
            popUpTo("auth_graph") { inclusive = true }
        }
    }
}

/**
 * Navigation Graph 내에서 부모 엔트리를 안전하게 가져옴.
 * */
@Composable
fun rememberAuthParentEntry(
    navController: NavHostController,
    currentEntry: NavBackStackEntry
): NavBackStackEntry? {
    return remember(currentEntry) {
        try {
            navController.getBackStackEntry("auth_graph")
        } catch (e: Exception) {
            null
        }
    }
}

@Composable
fun LoginApp(
    //navController: NavHostController, //꼬일 수 있기에 일단 사용하지 않음.
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel,
    showNavBar: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "auth_graph"
    ) {

        navigation(
            route = "auth_graph",
            startDestination = "login"
        ) {

            // 공통 화면 정의용 헬퍼 함수 (내부 중복 제거)
            fun authComposable(
                route: String,
                content: @Composable (NavBackStackEntry) -> Unit // VM을 직접 주입하지 않고 엔트리만 전달
            ) {
                composable(route) { entry ->
                    val parentEntry = rememberAuthParentEntry(navController, entry)
                    if (parentEntry == null) { //팀장 피드백 반영 수정,  부모 엔트리 없는 경우 화면 없이 로그인으로 보냄.
                        NavigateToLoginOnError(navController)
                    } else {
                        // 정상일 때 화면 그림.
                        content(parentEntry)
                    }
                }
            }
            // 1. 로그인 화면
            authComposable("login") { parentEntry ->
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)
                val skipAnimation = parentEntry.savedStateHandle.get<Boolean>("skip_login_animation") ?: false

                LaunchedEffect(skipAnimation) {
                    if (skipAnimation) parentEntry.savedStateHandle["skip_login_animation"] = false
                }

                AnimatedLoginScreen(
                    navigator = navController,
                    skipAnimation = skipAnimation,
                    onSignUpClick = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = true
                        navController.navigate("email_login")
                    }
                )
            }

            // 2. 이메일 로그인 + 약관 바텀시트
            authComposable("email_login") { parentEntry ->
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)
                LaunchedEffect(Unit) { showNavBar(false) }

                val showTermsSheet by parentEntry.savedStateHandle
                    .getStateFlow("show_terms_sheet", false).collectAsStateWithLifecycle()

                BackHandler(enabled = showTermsSheet) {
                    parentEntry.savedStateHandle["show_terms_sheet"] = false
                }

                EmailLoginScreen(
                    loginViewModel = loginViewModel,
                    navigator = navController,
                    onSignUpClick = { parentEntry.savedStateHandle["show_terms_sheet"] = true },
                    onLoginSuccess = onLoginSuccess
                )

                TermsAgreementSheet(
                    navController = navController,
                    vm = signUpVm,
                    visible = showTermsSheet,
                    onClose = { parentEntry.savedStateHandle["show_terms_sheet"] = false },
                    onClickTerms = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = false
                        navController.navigate("terms/service")
                    },
                    onClickPrivacy = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = false
                        navController.navigate("terms/privacy")
                    },
                    onClickMarketing = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = false
                        navController.navigate("terms/marketing")
                    }
                )
            }

            // 3. 약관 관련 (반복 로직 처리)
            val termsSteps = listOf(
                "terms/service" to { vm: SignUpViewModel -> vm.setAgreeTerms(true) },
                "terms/privacy" to { vm: SignUpViewModel -> vm.setAgreePrivacy(true) },
                "terms/marketing" to { vm: SignUpViewModel -> vm.setAgreeMarketing(true) }
            )

            termsSteps.forEach { (route, agreeAction) ->
                authComposable(route) { parentEntry ->
                    val vm: SignUpViewModel = hiltViewModel(parentEntry)

                    // 반환 타입을 Unit으로 수정 (오류 1, 2, 3 해결)
                    val onBack: () -> Unit = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = true
                        navController.popBackStack()
                        Unit

                    }
                    BackHandler { onBack() }

                    when(route) {
                        "terms/service" -> ServiceTermsScreen(onBackClicked = onBack, onAgreeClicked = { agreeAction(vm); onBack() })
                        "terms/privacy" -> PrivacyTermsScreenFixed(onBackClicked = onBack, onAgreeClicked = { agreeAction(vm); onBack() })
                        "terms/marketing" -> MarketingTermsScreenComposable(onBackClicked = onBack, onAgreeClicked = { agreeAction(vm); onBack() })
                    }
                }
            }

            // 4. 이메일 인증 EmailAuthViewModel 사용
            authComposable("email_verification") { parentEntry ->
                // auth_graph 스코프의 EmailAuthViewModel 인스턴스 생성
                val emailVm: EmailAuthViewModel = hiltViewModel(parentEntry)
                // auth_graph 스코프의 SignUpViewModel 인스턴스 생성 (필요시)
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)

                BackHandler {
                    parentEntry.savedStateHandle["skip_login_animation"] = true
                    navController.popBackStack()
                }

                EmailVerificationScreen(
                    navigator = navController,
                    parentEntry = parentEntry,
                    viewModel = emailVm,      // 파라미터 이름을 viewModel로 수정
                    signUpViewModel = signUpVm // SignUpViewModel도 동일한 스코프로 전달
                )
            }

            // 5. 회원가입 나머지 단계 (SignUpViewModel 사용)
            authComposable("sign_up_password") { parentEntry ->
                SignUpPasswordScreen(navController, hiltViewModel(parentEntry))
            }
            authComposable("sign_up_nickname") { parentEntry ->
                SignUpNicknameScreen(navController, hiltViewModel(parentEntry))
            }
            authComposable("sign_up_gender") { parentEntry ->
                SignUpGenderScreen(navController, hiltViewModel(parentEntry))
            }
            authComposable("sign_up_job") { parentEntry ->
                SignUpJobScreen(navController, hiltViewModel(parentEntry))
            }
            authComposable("sign_up_purpose") { parentEntry ->
                InterestPurposeScreen(navController, hiltViewModel(parentEntry))
            }
            authComposable("sign_up_interest") { parentEntry ->
                InterestContentScreen(navController, hiltViewModel(parentEntry))
            }
            authComposable("welcome") { parentEntry ->
                WelcomeScreen(navController, hiltViewModel(parentEntry))
            }

            composable("reset_password") {
                ResetPasswordScreen(navigator = navController)
            }
        }

        /**
         *  소셜 로그인 회원가입 순서(소셜 로그인(자동로그인) 미구현 -> 하게 된다면 따로 SocialLoginViewModel 파겠습니다.
         *
         *  1. 스플래쉬 -> 2. 로그인 스크린 -> 3. 여기서 카카오, 네이버, 구글은 선택시 -> 4. 딥링크로 이동함. ->
         *  5. 딥링크 회원가입 끝나면 -> 5. SocialNicknameScreen으로 이동함. 6. SocialGenderScreen ->
         *  7. SocialJobScreen -> 8. SocialPurposeScreen -> 9. SocialContentScreen ->
         *  10. WelcomeSocialScreen -> 11. 홈 스크린으로 이동하기!
         *
         *  소셜 로그인, 자동 로그인
         *  지민아 이거는 한 번 서원이 통해서 정리해줄 수 있어?
         *  지금 이름이 세션스토어이지 이게 DataStore를 이용해서 만든거야.
         *  현재는 로그인 할 때,  userId를 통해서, 아예 로그인할 때, /api/users/{userId} 통해서
         *  마이페이지 조회까지 1번에 해서 변경사항이 없다면,
         *  데이터 스토어(파일 이름 세션 스토어)로 저장했는데 소셜 로그인인 경우는 어떻게 처리해야할지 모르겠네
         *  유저 id 반환하는지 확인 해줄 수 있을까? 서원이에게 소셜 로그인에서 자동 로그인을 위한 토큰 발급 해주는 api에서
         *  userId만 있어도 괜찮을 것 같기는 한데, 한 번 확인 좀 부탁할게.
         *
         * */

        navigation(
            route = "social_auth_graph",
            startDestination = "social_entry"
        ) {

            /**
             *  딥링크 진입 → ACTIVE / TEMP 분기
             */
            composable("social_entry") { backStackEntry ->
                val socialToken =
                    backStackEntry.savedStateHandle.get<String>("socialToken") ?: ""
                val status =
                    backStackEntry.savedStateHandle.get<String>("status") ?: ""

                SocialEntryScreen(
                    navController = navController,
                    socialToken = socialToken,
                    status = status,
                    onLoginSuccess = onLoginSuccess
                )
            }

            /**
             *  약관 게이트 (로그인 화면 + 약관 바텀시트)
             *  약관 동의 상태만 필요 → SignUpViewModel 사용
             */
            composable("social_login_gate") { entry ->

                val signUpVm: SignUpViewModel = hiltViewModel(
                    navController.getBackStackEntry("social_auth_graph")
                )

                val showTermsSheet by entry.savedStateHandle
                    .getStateFlow("show_terms_sheet", true)
                    .collectAsStateWithLifecycle()

                BackHandler(enabled = showTermsSheet) {
                    entry.savedStateHandle["show_terms_sheet"] = false
                }

                LoginScreen(
                    navigator = navController
                )

                TermsAgreementSheet(
                    navController = navController,
                    vm = signUpVm,
                    visible = showTermsSheet,
                    onClose = {
                        entry.savedStateHandle["show_terms_sheet"] = false
                    },
                    onClickTerms = {
                        entry.savedStateHandle["show_terms_sheet"] = false
                        navController.navigate("social_terms/service")
                    },
                    onClickPrivacy = {
                        entry.savedStateHandle["show_terms_sheet"] = false
                        navController.navigate("social_terms/privacy")
                    },
                    onClickMarketing = {
                        entry.savedStateHandle["show_terms_sheet"] = false
                        navController.navigate("social_terms/marketing")
                    }
                )
            }

            /**
             *  소셜 약관 상세
             */
            composable("social_terms/service") {
                val vm: SignUpViewModel = hiltViewModel(
                    navController.getBackStackEntry("social_auth_graph")
                )

                val onBack = {
                    navController.popBackStack()
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("show_terms_sheet", true)
                    Unit
                }

                ServiceTermsScreen(
                    onBackClicked = onBack,
                    onAgreeClicked = {
                        vm.setAgreeTerms(true)
                        onBack()
                    }
                )
            }

            composable("social_terms/privacy") {
                val vm: SignUpViewModel = hiltViewModel(
                    navController.getBackStackEntry("social_auth_graph")
                )

                val onBack = {
                    navController.popBackStack()
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("show_terms_sheet", true)
                    Unit
                }

                PrivacyTermsScreenFixed(
                    onBackClicked = onBack,
                    onAgreeClicked = {
                        vm.setAgreePrivacy(true)
                        onBack()
                    }
                )
            }

            composable("social_terms/marketing") {
                val vm: SignUpViewModel = hiltViewModel(
                    navController.getBackStackEntry("social_auth_graph")
                )

                val onBack = {
                    navController.popBackStack()
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("show_terms_sheet", true)
                    Unit
                }

                MarketingTermsScreenComposable(
                    onBackClicked = onBack,
                    onAgreeClicked = {
                        vm.setAgreeMarketing(true)
                        onBack()
                    }
                )
            }

            /**
             *  소셜 회원가입 입력 플로우
             *  여기부터는 전부 SocialAuthViewModel
             */
            composable("social_nickname") {
                val vm: SocialAuthViewModel = hiltViewModel(
                    navController.getBackStackEntry("social_auth_graph")
                )
                SocialNicknameScreen(navController, vm)
            }

            composable("social_gender") {
                val vm: SocialAuthViewModel = hiltViewModel(
                    navController.getBackStackEntry("social_auth_graph")
                )
                SocialGenderScreen(navController, vm)
            }

            composable("social_job") {
                val vm: SocialAuthViewModel = hiltViewModel(
                    navController.getBackStackEntry("social_auth_graph")
                )
                SocialJobScreen(navController, vm)
            }

            composable("social_purpose") {
                val vm: SocialAuthViewModel = hiltViewModel(
                    navController.getBackStackEntry("social_auth_graph")
                )
                SocialPurposeScreen(navController, vm)
            }

            /**
             *  마지막: 관심사 → completeSocialProfile 단 1회 호출
             */
            composable("social_interest") {

                val parentEntry = navController.getBackStackEntry("social_auth_graph")
                val vm: SocialAuthViewModel = hiltViewModel(parentEntry)

                val socialToken =
                    parentEntry.savedStateHandle.get<String>("socialToken") ?: ""

                SocialInterestScreen(
                    navigator = navController,
                    viewModel = vm,
                    onComplete = {
                        vm.completeSocialProfile(
                            socialToken = socialToken,
                            onSuccess = {
                                navController.navigate("social_welcome") {
                                    popUpTo("social_auth_graph") { inclusive = true }
                                }
                            }
                        )
                    }
                )
            }

            /**
             *  소셜 회원가입 완료
             */
            composable("social_welcome") {
                WelcomeSocialScreen(
                    navigator = navController
                )
            }
        }


    }


}