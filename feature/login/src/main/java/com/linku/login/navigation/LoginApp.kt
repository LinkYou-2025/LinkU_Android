package com.linku.login.navigation

import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.linku.core.model.auth.LoginType
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
import com.linku.login.ui.screen.social.SocialGenderScreen
import com.linku.login.ui.screen.social.SocialInterestScreen
import com.linku.login.ui.screen.social.SocialJobScreen
import com.linku.login.ui.screen.social.SocialNicknameScreen
import com.linku.login.ui.screen.social.SocialPurposeScreen
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
    // (hideStatusBar, hideNavigationBar) 쌍으로 전달함 — 약관 동의 바텀시트처럼 상태바는
    // 숨긴 배경을 유지하면서도 내비게이션 바는 항상 보여야 하는 화면이 있어서 분리함.
    onEdgeToEdgeChange: (hideStatusBar: Boolean, hideNavigationBar: Boolean) -> Unit = { _, _ -> }
) {
    val navController = rememberNavController()

    // 탈퇴 유예기간(INACTIVE) 계정 복구 모달 표시 여부. 단순 1회성 이벤트라 uiState가 아닌
    // sideEffect(ShowRecoverModal)로 받아서 여기서 remember 상태로만 관리함.
    var showRecoverModal by remember { mutableStateOf(false) }

    // 채널 effect는 오직 LoginApp에서만.
    // 자동 로그인 성공/실패는 Splash가 loginViewModel.autoLoginState(StateFlow)를 직접 관찰해 라우팅하므로
    // 여기서는 다루지 않음 - LoginApp은 자동 로그인 시점엔 아직 컴포즈되지 않아 이 채널로 보내면
    // 이벤트가 버퍼에 쌓였다가, 로그아웃/탈퇴 후 LoginApp이 뒤늦게 재구성될 때 그 오래된 이벤트를
    // 받아 엉뚱하게 홈으로 되돌아가는 문제가 있었음.
    LaunchedEffect(Unit) {
        loginViewModel.sideEffect.collect { effect ->
            when (effect) {
                is LoginUiEffect.LoginSuccess -> onLoginSuccess()
                is LoginUiEffect.ShowRecoverModal -> showRecoverModal = true
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
                // edge-to-edge(몰입형) 화면. 이 화면을 벗어나면(email_login 등으로 이동) 자동으로 꺼짐.
                // 시스템 바 숨김/복원은 MainScreen의 EdgeToEdgeSystemBars(hideSystemBars) 한 곳에서만
                // 처리함 — 예전엔 SystemBarController를 여기서 따로 호출해 같은 Window를 두 체계가
                // 각자 다른 타이밍에 건드리면서 경합이 있었음(로그아웃/탈퇴 직후 시스템 바가 다시
                // 보이던 버그의 원인).
                DisposableEffect(Unit) {
                    onEdgeToEdgeChange(true, true) // 로그인 쪽에서 상태바, 하단바 별도 제어가 필요했음.
                    onDispose { onEdgeToEdgeChange(false, false) }
                }

                AnimatedLoginScreen(
                    skipAnimation = skipAnimation,
                    viewModel = socialAuthVm,
                    onLoginSuccess = onLoginSuccess,
                    onNavigateToEmailLogin = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = false
                        navController.navigate("email_login")
                    },
                    onNavigateToSocialOnboarding = { token, loginType ->
                        // TEMP 신규 사용자 감지 시 소셜 온보딩 하위 그래프 세션 인입
                        navController.navigate("social_auth_graph") {
                            launchSingleTop = true
                        }
                        navController.getBackStackEntry("social_auth_graph").savedStateHandle.apply {
                            set("socialToken", token)
                            // social_auth_graph는 auth_graph와 별도의 SocialAuthViewModel 인스턴스를 쓰므로
                            // 로그인 수단(카카오/구글)을 여기로 같이 넘겨야 completeSocialProfile()에서 유실 안 됨.
                            set("socialLoginType", loginType)
                        }
                    },
                    onAnimationSkipHandled = {
                        parentEntry.savedStateHandle.remove<Boolean>("skip_login_animation")
                    }
                )
            }

            // 2. 이메일 로그인 + 약관 바텀시트
            authComposable("email_login") { parentEntry ->
                Log.d("LoginApp", "email_login: $parentEntry")
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
                    showRecoverModal = showRecoverModal,
                    onDismissRecoverModal = { showRecoverModal = false },
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
                    // 회원가입 API 성공 시 토큰 저장(자동 로그인)까지 리포지토리에서 이미 끝난 상태.
                    // 소셜 회원가입(sign_up_interest 아래 social_interest)과 동일하게 상위의 로그인 성공 콜백을 그대로 재사용해서 홈으로 이동.
                    onSignUpSuccess = onLoginSuccess,
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
                val socialAuthVm: SocialAuthViewModel = hiltViewModel(parentEntry)
                val socialAuthUiState by socialAuthVm.state.collectAsStateWithLifecycle()
                val showTermsSheet by entry.savedStateHandle
                    .getStateFlow("show_terms_sheet", true)
                    .collectAsStateWithLifecycle()

                // 약관 동의를 거부(뒤로가기)한 것은 곧 이 소셜(카카오/구글) 가입 자체를 취소한다는
                // 뜻이므로, 시트만 닫으면 버튼이 모두 비활성화된 LoginScreen만 남아 먹통처럼 보임.
                // 최초 로그인 화면으로 완전히 되돌아가 다른 수단(구글/이메일)을 바로 쓸 수 있게 함.
                BackHandler(enabled = showTermsSheet) {
                    navController.popBackStack()
                }

                // 이 화면도 그라데이션 배경이 상태바까지 비치는 edge-to-edge 화면. 다만 약관 동의
                // 바텀시트가 떠 있는 화면이라 내비게이션 바는 항상 보여야 함(바텀시트 위에서
                // 시스템 바가 없으면 어색함) — 상태바만 숨기고 내비게이션 바는 false로 유지함.
                // onDispose에서 상태바를 false로 되돌리지 않음: 뒤로가기로 나가면 "login" 화면이
                // 다시 켜지며 자체적으로 true를 세팅하므로, 여기서 false를 세팅하면 그 사이 한
                // 프레임 동안 시스템 바가 깜빡이며 노출되는 문제가 있었음. 로그인 성공 후 Home으로
                // 갈 때는 MainApp.kt의 onLoginSuccess에서 별도로 false 처리함.
                DisposableEffect(Unit) {
                    onEdgeToEdgeChange(true, false)
                    onDispose { }
                }

                LoginScreen(
                    viewModel = socialAuthVm,
                    onLoginSuccess = onLoginSuccess,
                    buttonsEnabled = false,  // 버튼 비활성화
                    onNavigateToEmailLogin = {
                        navController.navigate("email_login")
                    },
                    onNavigateToSocialOnboarding = { token, loginType ->
                        // TEMP 유저 효과 발생 시 내비게이션 흐름 및 세션 전달을 그래프가 제어합니다.
                        navController.navigate("social_auth_graph") {
                            launchSingleTop = true
                        }
                        navController.getBackStackEntry("social_auth_graph").savedStateHandle.apply {
                            set("socialToken", token)
                            set("socialLoginType", loginType)
                        }
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
                        agreeTerms = socialAuthUiState.socialLoginForm.agreeTerms,
                        agreePrivacy = socialAuthUiState.socialLoginForm.agreePrivacy,
                        agreeMarketing = socialAuthUiState.socialLoginForm.agreeMarketing,
                    ),
                    event = TermsAgreementEvent(
                        onAgreeTermsChange = { socialAuthVm.setAgreeTerms(it) },
                        onAgreePrivacyChange = { socialAuthVm.setAgreePrivacy(it) },
                        onAgreeMarketingChange = { socialAuthVm.setAgreeMarketing(it) },
                        onClose = { navController.popBackStack() },
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
                "social_terms/service" to { vm: SocialAuthViewModel -> vm.setAgreeTerms(true) },
                "social_terms/privacy" to { vm: SocialAuthViewModel -> vm.setAgreePrivacy(true) },
                "social_terms/marketing" to { vm: SocialAuthViewModel -> vm.setAgreeMarketing(true) }
            )

            socialTermsSteps.forEach { (route, agreeAction) ->
                socialComposable(route) { parentEntry, _ ->
                    val vm: SocialAuthViewModel = hiltViewModel(parentEntry)
                    val socialAuthUiState by vm.state.collectAsStateWithLifecycle()
                    val onBack: () -> Unit = {
                        navController.popBackStack()
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("show_terms_sheet", true)
                    }
                    BackHandler { onBack() }

                    when (route) {
                        "social_terms/service" -> ServiceTermsScreen(
                            alreadyAgreed = socialAuthUiState.socialLoginForm.agreeTerms,
                            onBackClicked = onBack,
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )

                        "social_terms/privacy" -> PrivacyTermsScreenFixed(
                            alreadyAgreed = socialAuthUiState.socialLoginForm.agreePrivacy,
                            onBackClicked = onBack,
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )

                        "social_terms/marketing" -> MarketingTermsScreenComposable(
                            alreadyAgreed = socialAuthUiState.socialLoginForm.agreeMarketing,
                            onBackClicked = onBack,
                            onAgreeClicked = { agreeAction(vm); onBack() }
                        )
                    }
                }
            }

            // 소셜 회원가입 입력 플로우
            socialComposable("social_nickname") { parentEntry, _ ->
                val vm: SocialAuthViewModel = hiltViewModel(parentEntry)

                // social_login_gate(약관 바텀시트)는 뒤로가기로 login 화면에 돌아갈 때의
                // 깜빡임을 막기 위해 onDispose에서 상태바를 끄지 않음(위 주석 참고). 그 결과
                // 여기로 전진 네비게이션해도 상태바 숨김이 그대로 이어져 있던 문제가 있었음.
                // 정상 화면인 여기서 명시적으로 꺼줌 — 뒤로가기로 시트에 돌아가면 그 화면 자체의
                // mount effect가 상태바를 다시 켜주므로 문제 없음.
                DisposableEffect(Unit) {
                    onEdgeToEdgeChange(false, false)
                    onDispose { }
                }

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
                // 로그인 화면에서 SavedStateHandle로 넘겨받음 - social_auth_graph는 auth_graph와
                // 별도의 SocialAuthViewModel 인스턴스를 쓰기 때문에 이 값을 직접 전달해야 함.
                val socialLoginType = parentEntry.savedStateHandle.get<LoginType>("socialLoginType")
                    ?: LoginType.NONE

                // 완료 버튼 클릭 시 completeSocialProfile API를 직접 호출하고, 응답 토큰으로
                // 자동 로그인 세션 저장까지 끝난 뒤 onComplete(=onLoginSuccess)로 바로 홈 이동.
                SocialInterestScreen(
                    onBackClick = { navController.popBackStack() },
                    viewModel = vm,
                    socialToken = socialToken,
                    socialLoginType = socialLoginType,
                    onComplete = onLoginSuccess
                )
            }
        }
    }
}



