package com.linku.mypage

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.linku.mypage.screen.AccountSettingScreen
import com.linku.mypage.screen.AlarmSettingScreen
import com.linku.mypage.screen.ChangePasswordScreen
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
    notificationViewModel: NotificationViewModel = hiltViewModel(),
    onLogoutToLogin: () -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val session by viewModel.sessionState.collectAsStateWithLifecycle()

    // 로그인 시 발급받은 userId 를 보관하고 있다면 그 값을 사용
    // 화면 진입 시 최신 데이터 한 번 긁어오기
    LaunchedEffect(Unit) {
        viewModel.loadUserInfo()
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

            MyPageScreen(
                nickname = user?.nickname ?: "",
                email = user?.email ?: "",
                myLinku = user?.myLinku ?: 0L,
                myFolder = user?.myFolder ?: 0L,
                myAiLinku = user?.myAiLinku ?: 0L,
                onNavigateAccount = { navController.navigate("account") },
                onNavigateAlarm = { navController.navigate("alarm") },
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
                    isSocialLogin = session.isLoggedIn, // TODO: 세션에서 소셜 로그인 여부 가져오기(일단 지금은 로그인 여부로 대체)
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

        // 소셜 로그인 파라미터 때문에 주석처리
//        composable("editProfile") {
//            if (session.nickname != null && session.email != null) {
//                EditProfileScreen(
//                    navController = navController,
//                    onPickProfileImage = {
//                        // TODO: 이미지 picker 연결
//                    },
//                    onChangeProfileImage = {
//                        // TODO: 프로필 이미지 변경 API 연결
//                    },
//                    onChangeNickname = { newNickname ->
//                        viewModel.updateUserInfo(
//                            nickname = newNickname,
//                            jobId = session.jobId ?: 0L,
//                            jobName = session.jobName ?: "",
//                            purposes = session.purposes ?: emptyList(),
//                            interests = session.interests ?: emptyList(),
//                            onSuccess = {
//                                Toast.makeText(context, "변경되었습니다.", Toast.LENGTH_SHORT).show()
//                                navController.popBackStack()
//                            },
//                            onError = { msg ->
//                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//                            }
//                        )
//                    },
//                    onChangeGender = { newGender ->
//                        // TODO: 성별 변경 API 또는 updateUserInfo에 gender 포함해서 연결
//                    },
//                    onChangeJob = { newJob ->
//                        viewModel.updateUserInfo(
//                            nickname = session.nickname ?: "",
//                            jobId = session.jobId ?: 0L,
//                            jobName = newJob,
//                            purposes = session.purposes ?: emptyList(),
//                            interests = session.interests ?: emptyList(),
//                            onSuccess = {
//                                Toast.makeText(context, "변경되었습니다.", Toast.LENGTH_SHORT).show()
//                                navController.popBackStack()
//                            },
//                            onError = { msg ->
//                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//                            }
//                        )
//                    },
//                    userNickname = session.nickname ?: "",
//                    userJob = session.jobName ?: "",
//                    userEmail = session.email ?: "",
//                    userGender = session.gender ?: "",
//                    userSocialLoginType = session.socialLoginType ?: ""
//                )
//            }

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
            PurposeSelectionScreen(
                navController = navController,
                onNextClick = {
                    // TODO: 목적 저장 API 연결
                    navController.navigate("customInfoInterest")
                }
            )
        }

        composable("customInfoInterest") {
            InterestSelectionScreen(
                navController = navController,
                onFinishClick = {
                    // TODO: 목적/관심사 저장 API 연결
                    Toast.makeText(context, "맞춤정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    navController.popBackStack("customInfoSetting", inclusive = true)
                }
            )
        }

        composable("alarm") {
            // TODO: 알림 화면 연결
        }

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

                            // 1) 내부(MyPageApp) 스택 정리
                            navController.popBackStack(route = "mypage", inclusive = true)

                            // 2) 상위 네비게이터로 로그인 이동 요청
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