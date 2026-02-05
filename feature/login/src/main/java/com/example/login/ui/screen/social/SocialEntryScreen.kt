package com.example.login.ui.screen.social

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController

/**
 * SocialEntryScreen
 *
 *  소셜 로그인 딥링크 진입 지점
 *
 * - UI 없음
 * - 딥링크로 전달받은 값(socialToken, status)을 해석
 * - ACTIVE / TEMP 상태에 따라 분기만 수행
 */
@Composable
fun SocialEntryScreen(
    navController: NavHostController,
    socialToken: String,
    status: String,
    onLoginSuccess: () -> Unit
) {
    LaunchedEffect(Unit) {

        // social_auth_graph 스코프에 socialToken 저장
        // 이후 모든 소셜 플로우 화면에서 안전하게 사용 가능
        navController
            .getBackStackEntry("social_auth_graph")
            .savedStateHandle["socialToken"] = socialToken

        when (status) {

            //  이미 프로필이 완성된 유저
            "ACTIVE" -> {
                // 바로 홈으로 이동
                onLoginSuccess()
            }

            //  추가 정보 입력이 필요한 유저
            "TEMP" -> {
                // 소셜 회원가입 플로우 시작
                navController.navigate("social_login_gate") {
                    // 이 entry는 다시 돌아올 일 없으므로 제거
                    popUpTo("social_entry") {
                        inclusive = true
                    }
                }
            }

            // 알 수 없는 상태
            else -> {
                // 로그인 화면으로 되돌리거나
                navController.popBackStack()
            }
        }
    }
}
