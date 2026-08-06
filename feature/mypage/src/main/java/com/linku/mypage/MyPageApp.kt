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
import androidx.navigation.compose.rememberNavController
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.LoginType
import com.linku.core.model.auth.Purpose
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
    onNavigateToAlarm: () -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val session by viewModel.sessionState.collectAsStateWithLifecycle()

    // 알림 설정 창에서 사용할 뷰모델
    // 마이페이지에 귀속되는 UI이므로, MainApp에서부터 주입하지 않고
    // MyPageApp에서 만들어 주입한다.
    val notificationViewModel: NotificationViewModel = hiltViewModel()

    // 상태바/내비게이션 바는 MainScreen(app 모듈)에서 공통으로 흰색 처리함.

    // 로그인 시 발급받은 userId 를 보관하고 있다면 그 값을 사용
    // 화면 진입 시 최신 데이터 한 번 긁어오기
    LaunchedEffect(Unit) {
        viewModel.loadUserInfo()
        viewModel.checkUnreadAlarm()
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
                    viewModel.logout(
                        onSuccess = {
                            Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                            onLogoutToLogin()
                        },
                        onError = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
        }
//        composable("mypage") {
//            ui.userInfo?.let { user ->
//                MyPageScreen(
//                    navController = navController,
//                    nickname = user.nickname,
//                    email = user.email,
//                    gender = user.gender,
//                    jobName = user.jobName,
//                    myLinku = user.myLinku,
//                    myFolder = user.myFolder,
//                    myAiLinku = user.myAiLinku,
//                    onNavigateAccount = { navController.navigate("account") },
//                    onNavigateAlarm = { navController.navigate("alarm") },
//                    onNavigateQuit = { navController.navigate("quit") },
//                    onRequestLogout = {
//                        viewModel.logout(
//                            onSuccess = {
//                                android.widget.Toast
//                                    .makeText(context, "로그아웃 되었습니다.", android.widget.Toast.LENGTH_SHORT)
//                                    .show()
//
//                                // 1) 내부 MyPageApp 스택 정리(선택)
//                                navController.popBackStack(route = "mypage", inclusive = true)
//                                // 2) 상위 네비게이터에 로그인 화면으로 이동 요청
//                                onLogoutToLogin()
//                            },
//                            onError = { msg ->
//                                android.widget.Toast
//                                    .makeText(context, msg, android.widget.Toast.LENGTH_SHORT)
//                                    .show()
//                            }
//                        )
//                    }
//                )
//            }
//        }
        composable("account") {
            if (uiState.userInfo != null) {
                AccountSettingScreen(
                    navController = navController,
                    isSocialLogin = session.loginType == LoginType.KAKAO || session.loginType == LoginType.GOOGLE,
                    onEditProfileClick = { navController.navigate("editProfile") },
                    onChangePasswordClick = { navController.navigate("changePassword") },
                    onCustomInfoSettingClick = { navController.navigate("customInfoSetting") }
                )
            } else {
                // 로딩 중일 때 보여줄 화면 (잠시 빈 화면 혹은 프로그레스바)
                // 아무것도 안 써두면 데이터가 올 때까지 잠깐 멈춰있다가 나타납니다.
            }

//            nicknamePlaceholder = session.nickname ?: "",
//            jobPlaceholder = session.jobName ?: "",
//            initialPurposeTags = session.purposes.toSet(),
//            initialContentTags = session.interests.toSet(),
//            onSubmit = { nickname, jobId, jobName, purposes, interests ->
//                viewModel.updateUserInfo(
//                    nickname = nickname,
//                    jobId = jobId,
//                    jobName = jobName,
//                    purposes = purposes,
//                    interests = interests,
//                    onSuccess = {
//                        Toast.makeText(context, "변경되었습니다.", Toast.LENGTH_SHORT).show()
//                        navController.popBackStack("mypage", inclusive = false)
//                    },
//                    onError = { msg ->
//                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//                    }
//                )
//            }
//
//            AccountSettingScreen(
//                navController = navController,
//                nicknamePlaceholder = session.nickname ?: "",
//                jobPlaceholder = session.jobName ?: "",
//                // 세션에서 바로 가져올 수 있음!  api 호출 줄임.
//                initialPurposeTags = session.purposes.toSet(),    // 세션에서 가져옴
//                initialContentTags = session.interests.toSet(),   // 세션에서 가져옴
//                onSubmit = { nickname, jobId, jobName, purposes, interests ->
//                    /**
//                     * TODO: 지현이에게 전달
//                     *
//                     * [사용자 정보 수정 방법]
//                     * 아래 함수 호출하면 자동으로:
//                     * 1. 서버 API 호출 (DB 수정)
//                     * 2. 로컬 세션 업데이트 (UI 즉시 반영)
//                     *
//                     *
//                     * - nickname: 새 닉네임
//                     * - jobId: 직업 ID (Long)
//                     * - jobName: 직업 이름 (UI 표시용)
//                     * - purposes: 사용 목적 리스트 (한글 그대로 전달)
//                     *   ex) listOf("취업·커리어 준비", "학업/리포트 정리")
//                     * - interests: 관심 콘텐츠 리스트 (한글 그대로 전달)
//                     *   ex) listOf("IT/개발", "비즈니스/마케팅")
//                     *
//                     * jobName은 선택한 직업의 이름을 넘겨야 UI에 바로 반영
//                     */
//                    viewModel.updateUserInfo(
//                        nickname = nickname,
//                        jobId = jobId,
//                        jobName = jobName,// 직업 이름.
//                        purposes = purposes,
//                        interests = interests,
//                        onSuccess = {
//                            Toast.makeText(context, "변경되었습니다.", Toast.LENGTH_SHORT).show()
//                            navController.popBackStack("mypage", inclusive = false)
//                        },
//                        onError = { msg ->
//                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//                        }
//                    )
//                }
//            )
        }
//        composable("account") {
//            ui.userInfo?.let { user ->
//                AccountSettingScreen(
//                    navController = navController,
//                    nicknamePlaceholder = user.nickname,
//                    jobPlaceholder = user.jobName,
//                    initialPurposeTags = user.purposes.toSet(),
//                    initialContentTags = user.interests.toSet(),
//                    onSubmit = { nickname, jobId, purposes, interests ->
//                        viewModel.updateUserInfo(
//                            nickname = nickname,
//                            jobId = jobId,
//                            purposes = purposes,
//                            interests = interests,
//                            onSuccess = {
//                                android.widget.Toast
//                                    .makeText(context, "변경되었습니다.", android.widget.Toast.LENGTH_SHORT)
//                                    .show()
//                                // 최신 데이터는 loadUserInfo()에서 이미 갱신됨
//                                // MyPageScreen 으로 복귀
//                                navController.popBackStack("mypage", inclusive = false)
//                            },
//                            onError = { msg ->
//                                android.widget.Toast
//                                    .makeText(context, msg, android.widget.Toast.LENGTH_SHORT)
//                                    .show()
//                            }
//                        )
//                    }
//                )
//            }
//        }

        composable("editProfile") {
            LaunchedEffect(Unit) {
                viewModel.loadUserInfo()
            }

            val user = uiState.userInfo
            if (user != null) {
                EditProfileScreen(
                    navController = navController,
                    onPickProfileImage = {
                        // TODO: 이미지 picker 연결
                    },
                    onChangeProfileImage = {
                        // TODO: 프로필 이미지 변경 API 연결
                    },
                    onNicknameInputChanged = { input ->
                        viewModel.onNicknameChanged(input, originalNickname = user.nickname)
                    },
                    nicknameCheckState = uiState.nicknameCheckState,
                    onChangeNickname = { newNickname ->
                        viewModel.updateUserInfo(
                            nickname = newNickname,
                            jobId = user.jobId,
                            jobName = user.jobName,
                            purposes = user.purposes,
                            interests = user.interests,
                            onSuccess = {
                                Toast.makeText(context, "변경되었습니다.", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onChangeJob = { newJobName ->
                        // TODO: jobName -> jobId 매핑 수단이 없어 기존 jobId를 그대로 유지함
                        viewModel.updateUserInfo(
                            nickname = user.nickname,
                            jobId = user.jobId,
                            jobName = newJobName,
                            purposes = user.purposes,
                            interests = user.interests,
                            onSuccess = {
                                Toast.makeText(context, "변경되었습니다.", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    userNickname = user.nickname,
                    userJob = user.jobName,
                    userEmail = user.email,
                    userGender = user.gender,
                    userSocialLoginType = session.loginType.name
                )
            }
        }

        composable("changePassword") {
            val user = uiState.userInfo
            if (user != null) {
                ChangePasswordScreen(
                    navController = navController,
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
            LaunchedEffect(Unit) {
                viewModel.loadUserInfo()
            }

            val user = uiState.userInfo
            if (user != null) {
                PurposeSelectionScreen(
                    navController = navController,
                    initialSelected = user.purposes.mapNotNull { Purpose.fromServerKey(it) }
                        .toSet(),
                    onNextClick = { selected ->
                        viewModel.updateUserInfo(
                            nickname = user.nickname,
                            jobId = user.jobId,
                            jobName = user.jobName,
                            purposes = selected.map { it.serverKey },
                            interests = user.interests,
                            onSuccess = {
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
            LaunchedEffect(Unit) {
                viewModel.loadUserInfo()
            }

            val user = uiState.userInfo
            if (user != null) {
                InterestSelectionScreen(
                    navController = navController,
                    initialSelected = user.interests.mapNotNull { Interest.fromServerKey(it) }
                        .toSet(),
                    onFinishClick = { selected ->
                        viewModel.updateUserInfo(
                            nickname = user.nickname,
                            jobId = user.jobId,
                            jobName = user.jobName,
                            purposes = user.purposes,
                            interests = selected.map { it.serverKey },
                            onSuccess = {
                                Toast.makeText(context, "맞춤정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                                navController.popBackStack("customInfoSetting", inclusive = true)
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
                navController = navController,
                viewModel = notificationViewModel
            )
        }

        composable("quit") {
            ServiceQuitScreen(
                navController = navController,
                onRequestQuit = { reason ->
                    if (reason.isBlank()) {
                        android.widget.Toast
                            .makeText(context, "탈퇴 사유를 입력해주세요.", android.widget.Toast.LENGTH_SHORT)
                            .show()
                        return@ServiceQuitScreen
                    }
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
                            android.widget.Toast
                                .makeText(context, msg, android.widget.Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                }
            )
        }

        composable("faq") {
            FaqScreen(navController = navController)
        }

        composable("notice") {
            NoticeScreen(navController = navController)
        }

        composable("terms") {
            ServiceAgreeScreen(
                navController = navController,
                onMarketingAgreeClick = {
                    navController.navigate("marketingAgree")
                }
            )
        }

        composable("marketingAgree") {
            MarketingAgreeScreen(
                navController = navController
            )
        }

        // AI 링크 요약 화면은 세션 정보에 링크 리스트가 나오기 전까지 보류
//        composable ("aisummary") {
//            AILinkuListScreen(
//                navController = navController,
//                initialLinks =
//            )
//        }
    }
}