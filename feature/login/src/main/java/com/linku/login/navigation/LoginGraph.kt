package com.linku.login.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.linku.design.theme.LocalColorTheme
import com.linku.login.AuthActivityContract
import kotlinx.coroutines.CoroutineScope

/**
 * 로그인 플로우를 상위 내비게이션 그래프에 등록합니다.
 *
 * 로그인 화면 자체는 별도 Activity([com.linku.login.AuthActivity])가 그리므로, 이 목적지는 진입하는 즉시
 * 그 Activity를 열고 결과를 기다리는 자리표시자입니다. 덕분에 상위 그래프의 로그인 이동 로직
 * (`navigate(route)`, `popUpTo(route)`)은 그대로 유지됩니다. 로그인 성공 이후의 화면 전환처럼 다른
 * 모듈과 얽히는 흐름은 호출한 쪽이 [onLoginSuccess]로 이어받습니다.
 *
 * @param route 로그인 플로우를 등록할 경로
 * @param onEnter 로그인 플로우에 진입할 때 한 번 호출하는 함수
 * @param onLoginSuccess 로그인 성공 시 호출하는 함수. 이 목적지의 생명주기에 묶인 [CoroutineScope]와
 * 로그인 화면을 다시 여는 함수를 함께 전달합니다.
 */
fun NavGraphBuilder.loginGraph(
    route: String,
    onEnter: () -> Unit,
    onLoginSuccess: (scope: CoroutineScope, reopenLogin: () -> Unit) -> Unit,
) {
    composable(route) {
        val context = LocalContext.current
        val loginScope = rememberCoroutineScope()

        // 로그인 화면을 열어 달라는 요청 횟수와 실제로 연 횟수입니다. 프로세스가 재생성되어도 이미 연 요청이면
        // 로그인 Activity를 중복해서 열지 않도록 저장 가능한 상태로 둡니다.
        var openRequest by rememberSaveable { mutableIntStateOf(0) }
        var openedRequest by rememberSaveable { mutableIntStateOf(-1) }

        val loginLauncher = rememberLauncherForActivityResult(AuthActivityContract()) { success ->
            if (success) {
                onLoginSuccess(loginScope) { openRequest++ }
            }
        }

        LaunchedEffect(openRequest) {
            if (openedRequest != openRequest) {
                openedRequest = openRequest
                onEnter()
                // 전환 애니메이션 없이 열어 뒤의 자리표시자가 비치지 않게 합니다.
                loginLauncher.launch(Unit, ActivityOptionsCompat.makeCustomAnimation(context, 0, 0))
            }
        }

        // 로그인 Activity가 위에 떠 있는 동안과 성공 후 전환 직전에 보이는 배경입니다. 로그인 화면과 같은
        // 테마 그라데이션(linearMainColor)을 써서 화면이 끊겨 보이지 않게 합니다.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColorTheme.current.linearMainColor)
        )
    }
}
