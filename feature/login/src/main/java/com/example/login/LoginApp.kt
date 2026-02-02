package com.example.login

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.curation.CurationViewModel
import com.example.file.FileViewModel
import com.example.file.viewmodel.folder.state.FolderStateViewModel
import com.example.login.ui.animation.AnimatedLoginScreen
import com.example.login.ui.bottom_sheet.TermsAgreementSheet
import com.example.login.ui.screen.EmailLoginScreen
import com.example.login.ui.screen.EmailVerificationScreen
import com.example.login.ui.screen.InterestContentScreen
import com.example.login.ui.screen.InterestPurposeScreen
import com.example.login.ui.screen.ResetPasswordScreen
import com.example.login.ui.screen.SignUpGenderScreen
import com.example.login.ui.screen.SignUpJobScreen
import com.example.login.ui.screen.SignUpNicknameScreen
import com.example.login.ui.screen.SignUpPasswordScreen
import com.example.login.ui.screen.WelcomeScreen
import com.example.login.ui.terms.MarketingTermsScreenComposable
import com.example.login.ui.terms.PrivacyTermsScreenFixed
import com.example.login.ui.terms.ServiceTermsScreen
import com.example.login.viewmodel.LoginViewModel
import com.example.login.viewmodel.SignUpViewModel
import com.example.home.HomeViewModel
//import com.example.linku_android.deeplink.DeepLinkHandlerViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation


@Composable
fun LoginApp(
    //navController: NavHostController,
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

            composable("login") { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry("auth_graph")
                }
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)

                val skipAnimation =
                    parentEntry.savedStateHandle
                        .get<Boolean>("skip_login_animation") == true

                LaunchedEffect(skipAnimation) {
                    if (skipAnimation) {
                        parentEntry.savedStateHandle["skip_login_animation"] = false
                    }
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


             //② Email Login + Terms Sheet
            composable("email_login") { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry("auth_graph")
                }
                val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)

                LaunchedEffect(Unit) { showNavBar(false) }

                val showTermsSheet by parentEntry.savedStateHandle
                    .getStateFlow("show_terms_sheet", false)
                    .collectAsStateWithLifecycle()

                // 바텀시트 열려있을 때 백버튼 → 시트 닫기
                BackHandler(enabled = showTermsSheet) {
                    parentEntry.savedStateHandle["show_terms_sheet"] = false
                }

                EmailLoginScreen(
                    loginViewModel = loginViewModel,
                    navigator = navController,
                    onSignUpClick = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = true
                    },
                    onLoginSuccess = {
                        onLoginSuccess()
                    }
                )

                TermsAgreementSheet(
                    navController = navController,
                    vm = signUpVm,
                    visible = showTermsSheet,
                    onClose = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = false
                    },
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

            composable("terms/service") { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry("auth_graph")
                }
                val vm: SignUpViewModel = hiltViewModel(parentEntry)

                BackHandler {
                    parentEntry.savedStateHandle["show_terms_sheet"] = true
                    navController.popBackStack()
                }

                ServiceTermsScreen(
                    onBackClicked = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = true
                        navController.popBackStack()
                    },
                    onAgreeClicked = {
                        vm.setAgreeTerms(true)
                        parentEntry.savedStateHandle["show_terms_sheet"] = true
                        navController.popBackStack()
                    }
                )
            }


            composable("terms/privacy") { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry("auth_graph")
                }
                val vm: SignUpViewModel = hiltViewModel(parentEntry)

                PrivacyTermsScreenFixed(
                    onBackClicked = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = true
                        navController.popBackStack()
                    },
                    onAgreeClicked = {
                        vm.setAgreePrivacy(true)
                        parentEntry.savedStateHandle["show_terms_sheet"] = true
                        navController.popBackStack()
                    }
                )
            }

            composable("terms/marketing") { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry("auth_graph")
                }
                val vm: SignUpViewModel = hiltViewModel(parentEntry)

                MarketingTermsScreenComposable(
                    onBackClicked = {
                        parentEntry.savedStateHandle["show_terms_sheet"] = true
                        navController.popBackStack()
                    },
                    onAgreeClicked = {
                        vm.setAgreeMarketing(true)
                        parentEntry.savedStateHandle["show_terms_sheet"] = true
                        navController.popBackStack()
                    }
                )
            }


            composable("email_verification") { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry("auth_graph")
                }
                val vm: SignUpViewModel = hiltViewModel(parentEntry)

                BackHandler {
                    parentEntry.savedStateHandle["skip_login_animation"] = true
                    navController.popBackStack()
                }

                EmailVerificationScreen(
                    navigator = navController,
                    parentEntry = parentEntry,
                    signUpViewModel = vm
                )
            }

            composable("sign_up_password") {
                SignUpPasswordScreen(
                    navigator = navController,
                    signUpViewModel = hiltViewModel(
                        navController.getBackStackEntry("auth_graph")
                    )
                )
            }

            composable("sign_up_nickname") {
                SignUpNicknameScreen(
                    navigator = navController,
                    signUpViewModel = hiltViewModel(
                        navController.getBackStackEntry("auth_graph")
                    )
                )
            }

            composable("sign_up_gender") {
                SignUpGenderScreen(
                    navigator = navController,
                    signUpViewModel = hiltViewModel(
                        navController.getBackStackEntry("auth_graph")
                    )
                )
            }

            composable("sign_up_job") {
                SignUpJobScreen(
                    navigator = navController,
                    signUpViewModel = hiltViewModel(
                        navController.getBackStackEntry("auth_graph")
                    )
                )
            }

            composable("sign_up_purpose") {
                InterestPurposeScreen(
                    navigator = navController,
                    signUpViewModel = hiltViewModel(
                        navController.getBackStackEntry("auth_graph")
                    )
                )
            }

            composable("sign_up_interest") {
                InterestContentScreen(
                    navigator = navController,
                    signUpViewModel = hiltViewModel(
                        navController.getBackStackEntry("auth_graph")
                    )
                )
            }

            composable("welcome") {
                WelcomeScreen(
                    navigator = navController,
                    signUpViewModel = hiltViewModel(
                        navController.getBackStackEntry("auth_graph")
                    )
                )
            }

            composable("reset_password") {
                ResetPasswordScreen(navigator = navController)
            }
        }
    }
}
