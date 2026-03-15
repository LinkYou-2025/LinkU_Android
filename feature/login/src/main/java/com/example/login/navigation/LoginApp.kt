package com.example.login.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.login.LoginScreen
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
import com.example.login.ui.screen.social.SocialGenderScreen
import com.example.login.ui.screen.social.SocialInterestScreen
import com.example.login.ui.screen.social.SocialJobScreen
import com.example.login.ui.screen.social.SocialNicknameScreen
import com.example.login.ui.screen.social.SocialPurposeScreen
import com.example.login.ui.screen.social.WelcomeSocialScreen
import com.example.login.ui.terms.MarketingTermsScreenComposable
import com.example.login.ui.terms.PrivacyTermsScreenFixed
import com.example.login.ui.terms.ServiceTermsScreen
import com.example.login.viewmodel.EmailAuthViewModel
import com.example.login.viewmodel.LoginViewModel
import com.example.login.viewmodel.SignUpViewModel
import com.example.login.viewmodel.SocialAuthViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.login.R

@Composable
fun LoginApp(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel,
    showNavBar: (Boolean) -> Unit,
) {
    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = "auth_graph",
        // 기본 트랜지션 애니메이션 제거
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {

        // 이메일 인증으로 로그인하기.
        navigation(
            route = "auth_graph",
            startDestination = "login"
        ) {

            //authComposable: NavEntryHelper로 분리된 rememberAuthParentEntry 사용
            fun authComposable(
                route: String,
                content: @Composable (NavBackStackEntry) -> Unit
            ) {
                composable(route) { entry ->
                    val parentEntry = rememberAuthParentEntry(navController, entry)
                    if (parentEntry == null) {
                        NavigateToLoginOnError(navController) // NavEntryHelper에서 import
                    } else {
                        content(parentEntry)
                    }
                }
            }

            // 1. 로그인
            authComposable("login") { parentEntry ->
                // 추가함 :  parentEntry → auth_graph 전체를 범위로 하는 ViewModel 로그인 상태가 화면 전환 중에도 유지됨.
                val socialAuthVm: SocialAuthViewModel = hiltViewModel(parentEntry)

                val skipAnimation =
                    parentEntry.savedStateHandle.get<Boolean>("skip_login_animation") ?: false

                LaunchedEffect(skipAnimation) {
                    if (skipAnimation) parentEntry.savedStateHandle["skip_login_animation"] = false
                }

                AnimatedLoginScreen(
                    navigator = navController,
                    skipAnimation = skipAnimation,
                    viewModel = socialAuthVm, //추가함 : 받아서 -> 로그인 스크린으로 전달함.
                    onLoginSuccess = onLoginSuccess,
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
                    .getStateFlow("show_terms_sheet", false)
                    .collectAsStateWithLifecycle()

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

            // 3. 약관 상세 - 반복되는 부분은 helper로 뺌.
            val termsSteps = listOf(
                "terms/service"   to { vm: SignUpViewModel -> vm.setAgreeTerms(true) },
                "terms/privacy"   to { vm: SignUpViewModel -> vm.setAgreePrivacy(true) },
                "terms/marketing" to { vm: SignUpViewModel -> vm.setAgreeMarketing(true) }
            )

            termsSteps.forEach { (route, agreeAction) ->
                authComposable(route) { parentEntry ->
                    val vm: SignUpViewModel = hiltViewModel(parentEntry)

                    val onBack: () -> Unit = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = true
                        navController.popBackStack()
                    }
                    BackHandler { onBack() }

                    when (route) {
                        "terms/service"   -> ServiceTermsScreen(
                            onBackClicked = onBack,
                            // agreeAction(vm) 뒤에 세미콜론 제거 → 람다 마지막 식이 Unit이어야 함
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )
                        "terms/privacy"   -> PrivacyTermsScreenFixed(
                            onBackClicked = onBack,
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )
                        "terms/marketing" -> MarketingTermsScreenComposable(
                            onBackClicked = onBack,
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )
                    }
                }
            }

            // 4. 이메일 인증
            authComposable("email_verification") { parentEntry ->
                val emailVm: EmailAuthViewModel = hiltViewModel(parentEntry)
                val signUpVm: SignUpViewModel   = hiltViewModel(parentEntry)

                BackHandler {
                    parentEntry.savedStateHandle["skip_login_animation"] = true
                    navController.popBackStack()
                }

                EmailVerificationScreen(
                    navigator      = navController,
                    parentEntry    = parentEntry,
                    viewModel      = emailVm,
                    signUpViewModel = signUpVm
                )
            }

            // 5. 회원가입 단계
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
         * 소셜 로그인 회원가입 순서
         * 1. 스플래쉬 → 2. 로그인 → 3. 카카오 버튼 선택 → 4. 카카오 SDK 인증
         * 5. social_login_gate (약관 동의) → 6. SocialNicknameScreen → 7. Gender
         * 8. Job → 9. Purpose → 10. Interest → 11. WelcomeSocialScreen → 12. 홈
         *
         *   카카오 로그인: 딥링크 방식 → 카카오 SDK 방식으로 변경
         *   LoginScreen에서 KakaoLoginState.Success(TEMP) 감지 시 social_login_gate로 이동
         *   accessToken은 social_auth_graph의 savedStateHandle["socialToken"]에 저장
         *   social_interest에서 socialToken을 꺼내 completeSocialProfile API 호출
         *
         *   socialComposable: rememberSocialParentEntry로 social_auth_graph 스코프 ViewModel 공유
         *   null이면 NavigateToLoginOnError 호출 → 로그인 화면으로 안전하게 복귀
         *
         *   social_welcome: onLoginSuccess 콜백 전달 → 홈 이동
         */
        navigation(
            route = "social_auth_graph",
            startDestination = "social_login_gate"
        ) {

            fun socialComposable(
                route: String,
                content: @Composable (parentEntry: NavBackStackEntry, entry: NavBackStackEntry) -> Unit
            ) {
                composable(route) { entry ->
                    // rememberSocialParentEntry: try-catch로 안전 처리 (NavEntryHelper.kt)
                    // social_auth_graph가 백스택에 없으면 null → 로그인으로 복귀
                    val parentEntry = rememberSocialParentEntry(navController, entry)
                    if (parentEntry == null) {
                        NavigateToLoginOnError(navController)
                    } else {
                        content(parentEntry, entry)
                    }
                }
            }



            // 약관 게이트
            socialComposable("social_login_gate") { parentEntry, entry ->
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)
                val socialAuthVm: SocialAuthViewModel = hiltViewModel(parentEntry)

                val showTermsSheet by entry.savedStateHandle
                    .getStateFlow("show_terms_sheet", true)
                    .collectAsStateWithLifecycle()

                BackHandler(enabled = showTermsSheet) {
                    entry.savedStateHandle["show_terms_sheet"] = false
                }

                LoginScreen(
                    navigator = navController,
                    viewModel = socialAuthVm,
                    onLoginSuccess = onLoginSuccess,
                    buttonsEnabled = false,  // 버튼 비활성화
                    logoSlot = {
                        Image(
                            painter = painterResource(id = R.drawable.img_login_logo),
                            contentDescription = "LinkU Logo",
                            modifier = Modifier
                                .width(150.dp)
                                .height(106.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                )


                TermsAgreementSheet(
                    navController = navController,
                    vm            = signUpVm,
                    visible       = showTermsSheet,
                    onClose       = { entry.savedStateHandle["show_terms_sheet"] = false },
                    onNext = {
                        navController.navigate("social_nickname") {  // 소셜 로그인시에는 닉네임으로!
                            launchSingleTop = true
                        }
                    },
                    onClickTerms  = {
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

            // 소셜 약관 상세 (auth_graph의 termsSteps와 동일한 패턴으로 반복 제거)
            val socialTermsSteps = listOf(
                "social_terms/service"   to { vm: SignUpViewModel -> vm.setAgreeTerms(true) },
                "social_terms/privacy"   to { vm: SignUpViewModel -> vm.setAgreePrivacy(true) },
                "social_terms/marketing" to { vm: SignUpViewModel -> vm.setAgreeMarketing(true) }
            )

            socialTermsSteps.forEach { (route, agreeAction) ->
                socialComposable(route) { parentEntry, _ ->
                    val vm: SignUpViewModel = hiltViewModel(parentEntry)

                    val onBack: () -> Unit = {
                        navController.popBackStack()
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("show_terms_sheet", true)
                    }
                    BackHandler { onBack() }

                    when (route) {
                        "social_terms/service"   -> ServiceTermsScreen(
                            onBackClicked  = onBack,
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )
                        "social_terms/privacy"   -> PrivacyTermsScreenFixed(
                            onBackClicked  = onBack,
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )
                        "social_terms/marketing" -> MarketingTermsScreenComposable(
                            onBackClicked  = onBack,
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )
                    }
                }
            }

            // 소셜 회원가입 입력 플로우
            socialComposable("social_nickname") { parentEntry, _ ->
                val vm: SocialAuthViewModel = hiltViewModel(parentEntry)
                SocialNicknameScreen(navController, vm)
            }

            socialComposable("social_gender") { parentEntry, _ ->
                val vm: SocialAuthViewModel = hiltViewModel(parentEntry)
                SocialGenderScreen(navController, vm)
            }

            socialComposable("social_job") { parentEntry, _ ->
                val vm: SocialAuthViewModel = hiltViewModel(parentEntry)
                SocialJobScreen(navController, vm)
            }

            socialComposable("social_purpose") { parentEntry, _ ->
                val vm: SocialAuthViewModel = hiltViewModel(parentEntry)
                SocialPurposeScreen(navController, vm)
            }

            // [수정 3 적용] social_interest: entry -> 명시 + socialComposable 헬퍼 사용
            // [수정 4 적용] onComplete 콜백 안에서만 API 호출 → 리컴포지션 때 호출 안 됨
            socialComposable("social_interest") { parentEntry, _ ->
                val vm: SocialAuthViewModel = hiltViewModel(parentEntry)
                val socialToken = parentEntry.savedStateHandle.get<String>("socialToken") ?: ""

                SocialInterestScreen(
                    navigator  = navController,
                    viewModel  = vm,
                    // onComplete은 버튼 클릭 시 1회만 호출 → API 중복 호출 없음
                    onComplete = {
                        vm.completeSocialProfile(
                            socialToken = socialToken,
                            onSuccess   = {
                                navController.navigate("social_welcome") {
                                    popUpTo("social_auth_graph") { inclusive = true }
                                }
                            }
                        )
                    }
                )
            }

            composable("social_welcome") {
                WelcomeSocialScreen(
                    navigator      = navController,
                    onLoginSuccess = onLoginSuccess
                )
            }
        }
    }
}


