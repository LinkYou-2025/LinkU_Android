package com.linku.login.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.linku.login.viewmodel.LoginViewModel
import kotlinx.coroutines.CoroutineScope

/**
 * 로그인 플로우를 상위 내비게이션 그래프에 등록합니다.
 *
 * 로그인 화면 내부 이동은 [LoginApp]이 자체 내비게이션으로 처리하고, 로그인 성공 이후의 화면 전환처럼
 * 다른 모듈과 얽히는 흐름은 호출한 쪽이 [onLoginSuccess]로 이어받습니다.
 *
 * @param route 로그인 플로우를 등록할 경로
 * @param loginViewModel 로그인 상태와 이벤트를 제공하는 [LoginViewModel]
 * @param onEnter 로그인 플로우에 진입할 때 한 번 호출하는 함수
 * @param onEdgeToEdgeChange 상태바와 내비게이션 바 숨김 여부를 상위에 전달하는 함수
 * @param onLoginSuccess 로그인 성공 시 호출하는 함수. 이 목적지의 생명주기에 묶인 [CoroutineScope]를
 * 함께 전달합니다.
 */
fun NavGraphBuilder.loginGraph(
    route: String,
    loginViewModel: LoginViewModel,
    onEnter: () -> Unit,
    onEdgeToEdgeChange: (hideStatusBar: Boolean, hideNavigationBar: Boolean) -> Unit,
    onLoginSuccess: (scope: CoroutineScope) -> Unit,
) {
    composable(route) {
        LaunchedEffect(Unit) { onEnter() }
        val loginScope = rememberCoroutineScope()

        LoginApp(
            loginViewModel = loginViewModel,
            onEdgeToEdgeChange = onEdgeToEdgeChange,
            onLoginSuccess = { onLoginSuccess(loginScope) },
        )
    }
}
