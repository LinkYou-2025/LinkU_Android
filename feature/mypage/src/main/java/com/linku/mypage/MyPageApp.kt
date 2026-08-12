package com.linku.mypage

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.LoginType
import com.linku.core.model.auth.Purpose
import com.linku.mypage.intent.EditUserInfoIntent
import com.linku.mypage.intent.MarketingAgreeIntent
import com.linku.mypage.screen.AccountSettingScreen
import com.linku.mypage.screen.AlarmSettingScreen
import com.linku.mypage.screen.ChangePasswordScreen
import com.linku.mypage.screen.EditProfileScreen
import com.linku.mypage.screen.FaqScreen
import com.linku.mypage.screen.InterestSelectionScreen
import com.linku.mypage.screen.MarketingAgreeScreen
import com.linku.mypage.screen.MyPageScreen
import com.linku.mypage.screen.NoticeScreen
import com.linku.mypage.screen.PurposeSelectionScreen
import com.linku.mypage.screen.ServiceAgreeScreen
import com.linku.mypage.screen.ServiceQuitScreen

@Composable
fun MyPageApp(
    viewModel: MyPageViewModel,
    onLogoutToLogin: () -> Unit,
    onNavigateToAlarm: () -> Unit,
    // 로그아웃/탈퇴 API 응답을 기다리는 동안(성공 전) 로그인 화면 전환 애니메이션이 시작되기도
    // 전에 안드로이드 시스템 바가 잠깐 보였다 사라지는 깜빡임을 없애기 위해, 버튼을 누른 즉시
    // (API 호출 전에) 몰입 모드로 먼저 전환함. 실패해서 계속 MyPage에 남으면 다시 false로 되돌림.
    onImmersiveTransitionChange: (Boolean) -> Unit = {},
    // 내부 NavHost의 현재 화면이 MyPageScreen("mypage")일 때만 true를 전달해
    // 하단 네비게이션 바를 마이페이지 메인 화면에서만 보이게 함.
    onShowNavBarChange: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(currentBackStackEntry) {
        val route = currentBackStackEntry?.destination?.route
        onShowNavBarChange(route == "mypage")
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val session by viewModel.sessionState.collectAsStateWithLifecycle()

    // 알림 설정 창에서 사용할 뷰모델
    // 마이페이지에 귀속되는 UI이므로, MainApp에서부터 주입하지 않고
    // MyPageApp에서 만들어 주입한다.
    val notificationViewModel: NotificationViewModel = hiltViewModel()

    // 마케팅 수신 동의 화면 진입 시 체감 대기시간을 없애기 위해
    // 마이페이지 진입 시점부터 미리 상태를 조회해 두는 뷰모델.
    // marketingAgree 라우트가 아닌 이 레벨에서 hiltViewModel()을 호출해
    // 화면을 오가도 인스턴스와 조회 결과가 유지되게 한다.
    val marketingViewModel: MarketingAgreeViewModel = hiltViewModel()

    // 상태바/내비게이션 바는 MainScreen(app 모듈)에서 공통으로 흰색 처리함.

    // 로그인 시 발급받은 userId 를 보관하고 있다면 그 값을 사용
    // 화면 진입 시 최신 데이터 한 번 긁어오기
    LaunchedEffect(Unit) {
        viewModel.loadUserInfo()
        viewModel.checkUnreadAlarm()
        marketingViewModel.sendIntent(MarketingAgreeIntent.InitialLoad)
    }
    //기존
//    LaunchedEffect(Unit) {
//        viewModel.loadUserInfo()
//    }

    //val ui by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "mypage"
    ) {
        composable("mypage") {

            val user = uiState.userInfo
            val isUnreadAlarmExists = uiState.isUnreadAlarmExists

            MyPageScreen(
                nickname = user?.nickname ?: uiState.cachedNickname ?: "",
                email = user?.email ?: "",
                isUnreadAlarmExists = isUnreadAlarmExists,
                myLinku = user?.myLinku ?: 0L,
                myFolder = user?.myFolder ?: 0L,
                myAiLinku = user?.myAiLinku ?: 0L,
                loginType = session.loginType,
                onNavigateAccount = { navController.navigate("account") },
                onNavigateAlarm = { onNavigateToAlarm() },
                onNavigateAlarmSetting = { navController.navigate("alarmSetting") },
                onNavigateQuit = { navController.navigate("quit") },
                onNavigateFAQ = { navController.navigate("faq") },
                onNavigateNotice = { navController.navigate("notice") },
                onNavigateTerms = { navController.navigate("terms") },
                onNavigateAISummary = { navController.navigate("aisummary") },
                onRequestLogout = {
                    onImmersiveTransitionChange(true)
                    viewModel.logout(
                        onSuccess = {
                            Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                            onLogoutToLogin()
                        },
                        onError = { msg ->
                            // 실패 시 계속 MyPage에 남으므로 몰입 모드를 되돌림
                            onImmersiveTransitionChange(false)
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
        }

        composable("account") {
            if (uiState.userInfo != null) {
                AccountSettingScreen(
                    isSocialLogin = session.loginType == LoginType.KAKAO || session.loginType == LoginType.GOOGLE,
                    onBackClick = { navController.popBackStack() },
                    onEditProfileClick = { navController.navigate("editProfile") },
                    onChangePasswordClick = { navController.navigate("changePassword") },
                    onCustomInfoSettingClick = { navController.navigate("customInfoSetting") }
                )
            } else {
                // 로딩 중일 때 보여줄 화면 (잠시 빈 화면 혹은 프로그레스바)
                // 아무것도 안 써두면 데이터가 올 때까지 잠깐 멈춰있다가 나타납니다.
            }

        }

        composable("editProfile") {
            val editViewModel: MyPageEditViewModel = hiltViewModel()
            val editUiState by editViewModel.uiEditState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                editViewModel.loadUserInfo()
            }

            LaunchedEffect(editUiState.error) {
                editUiState.error?.let { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }

            val user = editUiState.userInfo
            if (user != null) {
                EditProfileScreen(
                    onBackClick = { navController.popBackStack() },
//                    onPickProfileImage = {
//                        // TODO: 이미지 picker 연결
//                    },
//                    onChangeProfileImage = {
//                        // TODO: 프로필 이미지 변경 API 연결
//                    },
                    onNicknameInputChanged = { input ->
                        editViewModel.onNicknameChanged(input, originalNickname = user.nickname)
                    },
                    nicknameCheckState = editUiState.nicknameCheckState,
                    onSubmit = { newNickname, newJobId ->
                        editViewModel.onEditUserInfoIntent(
                            EditUserInfoIntent.UpdateNickname(
                                newNickname
                            )
                        )
                        editViewModel.onEditUserInfoIntent(EditUserInfoIntent.UpdateJobId(newJobId))
                        editViewModel.editUserInfo(
                            onSuccess = {
                                viewModel.loadUserInfo()
                                Toast.makeText(context, "사용자 정보가 변경되었습니다.", Toast.LENGTH_SHORT)
                                    .show()
                                navController.popBackStack()
                            },
                            onError = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    userInfo = user,
                    userSocialLoginType = session.loginType
                )
            }
        }

        composable("changePassword") {
            val user = uiState.userInfo
            if (user != null) {
                ChangePasswordScreen(
                    onBackClick = { navController.popBackStack() },
                    userEmail = user.email,
                    onClickFinish = { newPassword ->
                        // TODO: 새로운 비밀번호 변경 API 연결
                        Toast.makeText(context, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                )
            }
        }

        composable("customInfoSetting") {
            val editViewModel: MyPageEditViewModel = hiltViewModel()
            val editUiState by editViewModel.uiEditState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                editViewModel.loadUserInfo()
            }

            LaunchedEffect(editUiState.error) {
                editUiState.error?.let { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }

            val user = editUiState.userInfo
            if (user != null) {
                PurposeSelectionScreen(
                    onBackClick = { navController.popBackStack() },
                    initialSelected = user.purposes.mapNotNull { Purpose.fromDisplayName(it) }
                        .toSet(),
                    onNextClick = { selected ->
                        val current =
                            user.purposes.mapNotNull { Purpose.fromDisplayName(it) }.toSet()
                        val diff = (current - selected) + (selected - current)
                        diff.forEach {
                            editViewModel.onEditUserInfoIntent(EditUserInfoIntent.UpdatePurpose(it))
                        }
                        editViewModel.editUserInfo(
                            onSuccess = {
                                viewModel.loadUserInfo()
                                navController.navigate("customInfoInterest")
                            },
                            onError = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            }
        }

        composable("customInfoInterest") {
            val editViewModel: MyPageEditViewModel = hiltViewModel()
            val editUiState by editViewModel.uiEditState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                editViewModel.loadUserInfo()
            }

            LaunchedEffect(editUiState.error) {
                editUiState.error?.let { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }

            val user = editUiState.userInfo
            if (user != null) {
                InterestSelectionScreen(
                    onBackClick = { navController.popBackStack() },
                    initialSelected = user.interests.mapNotNull { Interest.fromDisplayName(it) }
                        .toSet(),
                    onFinishClick = { selected ->
                        val current =
                            user.interests.mapNotNull { Interest.fromDisplayName(it) }.toSet()
                        val diff = (current - selected) + (selected - current)
                        diff.forEach {
                            editViewModel.onEditUserInfoIntent(EditUserInfoIntent.UpdateInterest(it))
                        }
                        editViewModel.editUserInfo(
                            onSuccess = {
                                viewModel.loadUserInfo()
                                Toast.makeText(context, "맞춤정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                                // 맞춤정보 설정(목적) -> 맞춤정보 설정(관심사)으로 이어지는 별도 흐름이므로,
                                // 시작 지점(account)까지 모두 정리하고 계정 설정 화면으로 복귀.
                                navController.popBackStack("account", inclusive = false)
                            },
                            onError = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            }
        }

//        composable("alarm") {
//            // TODO: 알림 화면 연결
//        }

        composable("alarmSetting") {
            AlarmSettingScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = notificationViewModel
            )
        }

        composable("quit") {
            ServiceQuitScreen(
                onBackClick = { navController.popBackStack() },
                onRequestQuit = { reason ->
                    if (reason.isBlank()) {
                        android.widget.Toast
                            .makeText(context, "탈퇴 사유를 입력해주세요.", android.widget.Toast.LENGTH_SHORT)
                            .show()
                        return@ServiceQuitScreen
                    }
                    onImmersiveTransitionChange(true)
                    viewModel.leaveUser(
                        reason = reason,
                        onSuccess = {
                            android.widget.Toast
                                .makeText(context, "탈퇴 처리가 완료되었습니다.", android.widget.Toast.LENGTH_SHORT)
                                .show()

                            // 상위 네비게이터로 로그인 이동 요청.
                            // onLogoutToLogin()이 외부 백스택 전체(MyPage 탭 포함)를 지우기 때문에
                            // 이 내부 NavHost도 함께 사라짐 - 별도로 popBackStack("mypage")를 호출하면
                            // 시작 목적지를 inclusive하게 지우려다 내부 백스택이 비어 예외가 나서
                            // 바로 아래의 onLogoutToLogin() 호출 자체가 실행되지 못하는 문제가 있었음.
                            onLogoutToLogin()
                        },
                        onError = { msg ->
                            // 실패 시 계속 MyPage에 남으므로 몰입 모드를 되돌림
                            onImmersiveTransitionChange(false)
                            android.widget.Toast
                                .makeText(context, msg, android.widget.Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                }
            )
        }

        composable("faq") {
            FaqScreen(onBackClick = { navController.popBackStack() })
        }

        composable("notice") {
            NoticeScreen(onBackClick = { navController.popBackStack() })
        }

        composable("terms") {
            ServiceAgreeScreen(
                onBackClick = { navController.popBackStack() },
                onMarketingAgreeClick = {
                    navController.navigate("marketingAgree")
                }
            )
        }

        composable("marketingAgree") {
            val marketingState by marketingViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                marketingViewModel.sideEffect.collect { effect ->
                    when (effect) {
                        is MarketingAgreeSideEffect.ShowToast ->
                            Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            MarketingAgreeScreen(
                onBackClick = { navController.popBackStack() },
                isChecked = marketingState.isAgreed,
                onToggleClick = {
                    marketingViewModel.sendIntent(MarketingAgreeIntent.ToggleMarketingAgree)
                },
            )
        }

        // AI 링크 요약 화면은 세션 정보에 링크 리스트가 나오기 전까지 보류
//        composable ("aisummary") {
//            AILinkuListScreen(
//                onBackClick = { navController.popBackStack() },
//                initialLinks =
//            )
//        }
    }
}