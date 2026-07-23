package com.linku.login.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.linku.login.LoginScreen
import com.linku.login.R
import com.linku.login.ui.animation.AnimatedLoginScreen
import com.linku.login.ui.bottom_sheet.TermsAgreementSheet
import com.linku.login.ui.model.TermsAgreementEvent
import com.linku.login.ui.model.TermsAgreementState
import com.linku.login.ui.screen.email.EmailLoginScreen
import com.linku.login.ui.screen.email.EmailVerificationScreen
import com.linku.login.ui.screen.email.InterestContentScreen
import com.linku.login.ui.screen.email.InterestPurposeScreen
import com.linku.login.ui.screen.email.ResetPasswordScreen
import com.linku.login.ui.screen.email.SignUpGenderScreen
import com.linku.login.ui.screen.email.SignUpJobScreen
import com.linku.login.ui.screen.email.SignUpNicknameScreen
import com.linku.login.ui.screen.email.SignUpPasswordScreen
import com.linku.login.ui.screen.email.WelcomeScreen
import com.linku.login.ui.screen.social.SocialGenderScreen
import com.linku.login.ui.screen.social.SocialInterestScreen
import com.linku.login.ui.screen.social.SocialJobScreen
import com.linku.login.ui.screen.social.SocialNicknameScreen
import com.linku.login.ui.screen.social.SocialPurposeScreen
import com.linku.login.ui.screen.social.WelcomeSocialScreen
import com.linku.login.ui.terms.MarketingTermsScreenComposable
import com.linku.login.ui.terms.PrivacyTermsScreenFixed
import com.linku.login.ui.terms.ServiceTermsScreen
import com.linku.login.viewmodel.EmailAuthViewModel
import com.linku.login.viewmodel.LoginViewModel
import com.linku.login.viewmodel.SignUpViewModel
import com.linku.login.viewmodel.SocialAuthViewModel
import com.linku.login.viewmodel.state.LoginUiEffect

@Composable
fun LoginApp(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel,
    // 그라데이션 배경(AnimatedLoginScreen/LoginScreen)이 보이는 동안에만 true를 전달해서
    // 상위(MainScreen)의 흰색 상태바 스크림을 끔. 그 외 로그인 하위 화면은 흰 상태바가 기본.
    onEdgeToEdgeChange: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()

    // 채널 effect는 오직 LoginApp에서만.
    // 자동 로그인 성공/실패는 Splash가 loginViewModel.autoLoginState(StateFlow)를 직접 관찰해 라우팅하므로
    // 여기서는 다루지 않음 - LoginApp은 자동 로그인 시점엔 아직 컴포즈되지 않아 이 채널로 보내면
    // 이벤트가 버퍼에 쌓였다가, 로그아웃/탈퇴 후 LoginApp이 뒤늦게 재구성될 때 그 오래된 이벤트를
    // 받아 엉뚱하게 홈으로 되돌아가는 문제가 있었음.
    LaunchedEffect(Unit) {
        loginViewModel.sideEffect.collect { effect ->
            when (effect) {
                is LoginUiEffect.LoginSuccess -> onLoginSuccess()
            }
        }
    }


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

                // AnimatedLoginScreen/LoginScreen은 그라데이션 배경이 상태바까지 비치는
                // edge-to-edge 화면. 이 화면을 벗어나면(email_login 등으로 이동) 자동으로 꺼짐.
                DisposableEffect(Unit) {
                    onEdgeToEdgeChange(true)
                    onDispose { onEdgeToEdgeChange(false) }
                }

                AnimatedLoginScreen(
                    skipAnimation = skipAnimation,
                    viewModel = socialAuthVm,
                    onLoginSuccess = onLoginSuccess,
                    onNavigateToEmailLogin = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = false
                        navController.navigate("email_login")
                    },
                    onNavigateToSocialOnboarding = { token ->
                        // TEMP 신규 사용자 감지 시 소셜 온보딩 하위 그래프 세션 인입
                        navController.navigate("social_auth_graph") {
                            launchSingleTop = true
                        }
                        navController.getBackStackEntry("social_auth_graph")
                            .savedStateHandle["socialToken"] = token
                    },
                    onAnimationSkipHandled = {
                        parentEntry.savedStateHandle.remove<Boolean>("skip_login_animation")
                    }
                )
            }

            // 2. 이메일 로그인 + 약관 바텀시트
            authComposable("email_login") { parentEntry ->
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)
                val signUpUiState by signUpVm.state.collectAsStateWithLifecycle()
                val showTermsSheet by parentEntry.savedStateHandle
                    .getStateFlow("show_terms_sheet", false)
                    .collectAsStateWithLifecycle()

                BackHandler(enabled = showTermsSheet) {
                    parentEntry.savedStateHandle["show_terms_sheet"] = false
                }

                EmailLoginScreen(
                    loginViewModel = loginViewModel,
                    onSignUpClick = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = true
                    },
                    onResetPasswordClick = {
                        navController.navigate("reset_password")
                    },
                    onLoginSuccess = onLoginSuccess
                )

                TermsAgreementSheet(
                    visible = showTermsSheet,
                    state = TermsAgreementState(
                        agreeTerms = signUpUiState.signUpForm.agreeTerms,
                        agreePrivacy = signUpUiState.signUpForm.agreePrivacy,
                        agreeMarketing = signUpUiState.signUpForm.agreeMarketing,
                    ),
                    event = TermsAgreementEvent(
                        onAgreeTermsChange = { signUpVm.setAgreeTerms(it) },
                        onAgreePrivacyChange = { signUpVm.setAgreePrivacy(it) },
                        onAgreeMarketingChange = { signUpVm.setAgreeMarketing(it) },
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
                        },
                        onNext = {
                            navController.navigate("email_verification") {
                                launchSingleTop = true
                            }
                        },
                    )
                )
            }

            // 3. 약관 상세 - 반복되는 부분은 helper로 뺌.
            val termsSteps = listOf(
                "terms/service" to { vm: SignUpViewModel -> vm.setAgreeTerms(true) },
                "terms/privacy" to { vm: SignUpViewModel -> vm.setAgreePrivacy(true) },
                "terms/marketing" to { vm: SignUpViewModel -> vm.setAgreeMarketing(true) }
            )

            termsSteps.forEach { (route, agreeAction) ->
                authComposable(route) { parentEntry ->
                    val vm: SignUpViewModel = hiltViewModel(parentEntry)

                    val signUpUiState by vm.state.collectAsStateWithLifecycle()

                    val onBack: () -> Unit = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = true
                        navController.popBackStack()
                    }
                    BackHandler { onBack() }

                    when (route) {
                        "terms/service" -> ServiceTermsScreen(
                            alreadyAgreed = signUpUiState.signUpForm.agreeTerms,
                            onBackClicked = onBack,
                            // agreeAction(vm) 뒤에 세미콜론 제거 → 람다 마지막 식이 Unit이어야 함
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )

                        "terms/privacy" -> PrivacyTermsScreenFixed(
                            alreadyAgreed = signUpUiState.signUpForm.agreePrivacy,
                            onBackClicked = onBack,
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )

                        "terms/marketing" -> MarketingTermsScreenComposable(
                            alreadyAgreed = signUpUiState.signUpForm.agreeMarketing,
                            onBackClicked = onBack,
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )
                    }
                }
            }

            // 4. 이메일 인증
            authComposable("email_verification") { parentEntry ->
                val emailVm: EmailAuthViewModel = hiltViewModel(parentEntry)
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)

                EmailVerificationScreen(
                    onBackClick = {
                        parentEntry.savedStateHandle["skip_login_animation"] = true
                        navController.popBackStack()
                    },
                    onNavigateToPassword = {
                        navController.navigate("sign_up_password") {
                            launchSingleTop = true
                        }
                    },
                    viewModel = emailVm,
                    signUpViewModel = signUpVm
                )
            }

            // 5. 회원가입 단계
            authComposable("sign_up_password") { parentEntry ->
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)

                SignUpPasswordScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToNickname = {
                        navController.navigate("sign_up_nickname") { launchSingleTop = true }
                    },
                    signUpViewModel = signUpVm
                )
            }

            authComposable("sign_up_nickname") { parentEntry ->
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)

                SignUpNicknameScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToGender = {
                        navController.navigate("sign_up_gender") { launchSingleTop = true }
                    },
                    signUpViewModel = signUpVm
                )
            }

            authComposable("sign_up_gender") { parentEntry ->
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)
                SignUpGenderScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToJob = {
                        navController.navigate("sign_up_job") { launchSingleTop = true }
                    },
                    signUpViewModel = signUpVm
                )
            }

            authComposable("sign_up_job") { parentEntry ->
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)

                SignUpJobScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToPurpose = {
                        navController.navigate("sign_up_purpose") { launchSingleTop = true }
                    },
                    signUpViewModel = signUpVm
                )
            }

            authComposable("sign_up_purpose") { parentEntry ->
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)

                InterestPurposeScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToInterest = {
                        navController.navigate("sign_up_interest") { launchSingleTop = true }
                    },
                    signUpViewModel = signUpVm
                )
            }

            authComposable("sign_up_interest") { parentEntry ->
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)

                InterestContentScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToWelcome = {
                        navController.navigate("welcome") { launchSingleTop = true }
                    },
                    signUpViewModel = signUpVm
                )
            }

            authComposable("welcome") { parentEntry ->
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)

                WelcomeScreen(
                    onNavigateToLogin = {
                        navController.navigate("email_login") {
                            popUpTo("auth_graph") { inclusive = true }
                        }
                    },
                    onNavigateBackOnError = {
                        navController.popBackStack()
                    },
                    signUpViewModel = signUpVm
                )
            }

            composable("reset_password") {
                ResetPasswordScreen(
                    onNavigateToEmailLogin = {
                        navController.navigate("email_login") {
                            popUpTo("reset_password") { inclusive = true }
                        }
                    }
                )
            }
        }

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
                val signUpUiState by signUpVm.state.collectAsStateWithLifecycle()
                val showTermsSheet by entry.savedStateHandle
                    .getStateFlow("show_terms_sheet", true)
                    .collectAsStateWithLifecycle()

                BackHandler(enabled = showTermsSheet) {
                    entry.savedStateHandle["show_terms_sheet"] = false
                }

                // 이 화면도 그라데이션 배경이 상태바까지 비치는 edge-to-edge 화면.
                DisposableEffect(Unit) {
                    onEdgeToEdgeChange(true)
                    onDispose { onEdgeToEdgeChange(false) }
                }

                LoginScreen(
                    viewModel = socialAuthVm,
                    onLoginSuccess = onLoginSuccess,
                    buttonsEnabled = false,  // 버튼 비활성화
                    onNavigateToEmailLogin = {
                        navController.navigate("email_login")
                    },
                    onNavigateToSocialOnboarding = { token ->
                        // TEMP 유저 효과 발생 시 내비게이션 흐름 및 세션 전달을 그래프가 제어합니다.
                        // TODO : 수정하기
                        navController.navigate("social_auth_graph") {
                            launchSingleTop = true
                        }
                        navController.getBackStackEntry("social_auth_graph")
                            .savedStateHandle["socialToken"] = token
                    },
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
                    visible = showTermsSheet,
                    state = TermsAgreementState(
                        agreeTerms = signUpUiState.signUpForm.agreeTerms,
                        agreePrivacy = signUpUiState.signUpForm.agreePrivacy,
                        agreeMarketing = signUpUiState.signUpForm.agreeMarketing,
                    ),
                    event = TermsAgreementEvent(
                        onAgreeTermsChange = { signUpVm.setAgreeTerms(it) },
                        onAgreePrivacyChange = { signUpVm.setAgreePrivacy(it) },
                        onAgreeMarketingChange = { signUpVm.setAgreeMarketing(it) },
                        onClose = { entry.savedStateHandle["show_terms_sheet"] = false },
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
                        },
                        onNext = {
                            navController.navigate("social_nickname") {
                                launchSingleTop = true
                            }
                        },
                    )
                )
            }

            // 소셜 약관 상세 (auth_graph의 termsSteps와 동일한 패턴으로 반복 제거)
            val socialTermsSteps = listOf(
                "social_terms/service" to { vm: SignUpViewModel -> vm.setAgreeTerms(true) },
                "social_terms/privacy" to { vm: SignUpViewModel -> vm.setAgreePrivacy(true) },
                "social_terms/marketing" to { vm: SignUpViewModel -> vm.setAgreeMarketing(true) }
            )

            socialTermsSteps.forEach { (route, agreeAction) ->
                socialComposable(route) { parentEntry, _ ->
                    val vm: SignUpViewModel = hiltViewModel(parentEntry)
                    val signUpUiState by vm.state.collectAsStateWithLifecycle()
                    val onBack: () -> Unit = {
                        navController.popBackStack()
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("show_terms_sheet", true)
                    }
                    BackHandler { onBack() }

                    when (route) {
                        "social_terms/service" -> ServiceTermsScreen(
                            alreadyAgreed = signUpUiState.signUpForm.agreeTerms,
                            onBackClicked = onBack,
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )

                        "social_terms/privacy" -> PrivacyTermsScreenFixed(
                            alreadyAgreed = signUpUiState.signUpForm.agreeTerms,
                            onBackClicked = onBack,
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )

                        "social_terms/marketing" -> MarketingTermsScreenComposable(
                            alreadyAgreed = signUpUiState.signUpForm.agreeTerms,
                            onBackClicked = onBack,
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )
                    }
                }
            }

            // 소셜 회원가입 입력 플로우
            socialComposable("social_nickname") { parentEntry, _ ->
                val vm: SocialAuthViewModel = hiltViewModel(parentEntry)
                SocialNicknameScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToGender = {
                        navController.navigate("social_gender") {
                            launchSingleTop = true
                        }
                    },
                    viewModel = vm
                )
            }

            socialComposable("social_gender") { parentEntry, _ ->
                val vm: SocialAuthViewModel = hiltViewModel(parentEntry)
                SocialGenderScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToJob = {
                        navController.navigate("social_job") {
                            launchSingleTop = true
                        }
                    },
                    viewModel = vm
                )
            }

            socialComposable("social_job") { parentEntry, _ ->
                val vm: SocialAuthViewModel = hiltViewModel(parentEntry)
                SocialJobScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToPurpose = {
                        navController.navigate("social_purpose") {
                            launchSingleTop = true
                        }
                    },
                    viewModel = vm
                )
            }

            socialComposable("social_purpose") { parentEntry, _ ->
                val vm: SocialAuthViewModel = hiltViewModel(parentEntry)
                SocialPurposeScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToInterest = {
                        navController.navigate("social_interest") {
                            launchSingleTop = true
                        }
                    },
                    viewModel = vm
                )
            }

            socialComposable("social_interest") { parentEntry, _ ->
                val vm: SocialAuthViewModel = hiltViewModel(parentEntry)
                val socialToken = parentEntry.savedStateHandle.get<String>("socialToken") ?: ""

                SocialInterestScreen(
                    onBackClick = { navController.popBackStack() },
                    viewModel = vm,
                    onComplete = {
                        navController.navigate("social_welcome") {
                            launchSingleTop = true
                        }
                    }
                )
            }

            socialComposable("social_welcome") { parentEntry, _ ->
                val vm: SocialAuthViewModel = hiltViewModel(parentEntry)
                val socialToken = parentEntry.savedStateHandle.get<String>("socialToken") ?: ""

                WelcomeSocialScreen(
                    socialToken = socialToken,
                    onNavigateToHome = onLoginSuccess,
                    onNavigateBackOnError = {
                        navController.popBackStack()
                    },
                    viewModel = vm
                )
            }
        }
    }
}



