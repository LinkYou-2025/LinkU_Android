package com.example.mypage

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mypage.screen.AccountSettingScreen
import com.example.mypage.screen.AlarmSettingScreen
import com.example.mypage.screen.MyPageScreen
import com.example.mypage.screen.ServiceQuitScreen

@Composable
fun MyPageApp(
    viewModel: MyPageViewModel,
    onLogoutToLogin: () -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // 로그인 시 발급받은 userId 를 보관하고 있다면 그 값을 사용
    LaunchedEffect(Unit) {
        viewModel.loadUserInfo()
    }

    val ui by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "mypage"
    ) {
        composable("mypage") {
            ui.userInfo?.let { user ->
                MyPageScreen(
                    navController = navController,
                    nickname = user.nickname,
                    email = user.email,
                    gender = user.gender,
                    jobName = user.jobName,
                    myLinku = user.myLinku,
                    myFolder = user.myFolder,
                    myAiLinku = user.myAiLinku,
                    onNavigateAccount = { navController.navigate("account") },
                    onNavigateAlarm = { navController.navigate("alarm") },
                    onNavigateQuit = { navController.navigate("quit") },
                    onRequestLogout = {
                        viewModel.logout(
                            onSuccess = {
                                android.widget.Toast
                                    .makeText(context, "로그아웃 되었습니다.", android.widget.Toast.LENGTH_SHORT)
                                    .show()

                                // 1) 내부 MyPageApp 스택 정리(선택)
                                navController.popBackStack(route = "mypage", inclusive = true)
                                // 2) 상위 네비게이터에 로그인 화면으로 이동 요청
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
        }
        composable("account") {
            ui.userInfo?.let { user ->
                AccountSettingScreen(
                    navController = navController,
                    nicknamePlaceholder = user.nickname,
                    jobPlaceholder = user.jobName,
                    initialPurposeTags = user.purposes.toSet(),
                    initialContentTags = user.interests.toSet(),
                    onSubmit = { nickname, jobId, purposes, interests ->
                        viewModel.updateUserInfo(
                            nickname = nickname,
                            jobId = jobId,
                            purposes = purposes,
                            interests = interests,
                            onSuccess = {
                                android.widget.Toast
                                    .makeText(context, "변경되었습니다.", android.widget.Toast.LENGTH_SHORT)
                                    .show()
                                // 최신 데이터는 loadUserInfo()에서 이미 갱신됨
                                // MyPageScreen 으로 복귀
                                navController.popBackStack("mypage", inclusive = false)
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
        }
        composable("alarm") { AlarmSettingScreen(navController = navController) }
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
    }
}