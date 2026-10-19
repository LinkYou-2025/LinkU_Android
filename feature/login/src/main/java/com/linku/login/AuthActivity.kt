package com.linku.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.linku.design.theme.ThemeProvider
import com.linku.design.util.EdgeToEdgeSystemBars
import com.linku.design.util.LocalScaffoldBackgroundReporter
import com.linku.design.util.LocalStatusBarDarkIcons
import com.linku.login.navigation.LoginApp
import com.linku.login.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * 로그인과 회원가입 플로우만 담당하는 Activity입니다.
 *
 * 앱의 메인 화면([com.linku.login.AuthActivityContract]를 호출한 쪽)이 뒤에 살아 있는 상태로 이 화면을
 * 결과 대기 방식으로 엽니다. 로그인에 성공하면 [android.app.Activity.RESULT_OK]를 돌려주고 종료하며,
 * 이후의 화면 전환과 보류된 딥링크 처리는 호출한 쪽이 이어받습니다.
 *
 * 로그인 루트 화면에서 뒤로가기를 누르면 앱 전체 작업을 종료합니다. 로그인 플로우 내부의 이전 화면 이동은
 * [LoginApp]의 내비게이션이 먼저 처리합니다.
 */
@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    /**
     * 로그인 플로우 Compose UI를 구성하고 로그인 루트에서의 뒤로가기 동작을 등록합니다.
     *
     * @param savedInstanceState 이전 Activity 인스턴스에서 복원할 상태
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Compose의 BackHandler보다 먼저 등록해 우선순위를 가장 낮게 둡니다. 로그인 플로우 내부에서
        // 처리할 이전 화면이 없을 때만 앱 작업 전체를 종료합니다.
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishAffinity()
                }
            },
        )

        setContent {
            ThemeProvider {
                AuthScreenShell { onEdgeToEdgeChange ->
                    val loginViewModel: LoginViewModel = hiltViewModel()

                    LoginApp(
                        loginViewModel = loginViewModel,
                        onEdgeToEdgeChange = onEdgeToEdgeChange,
                        onLoginSuccess = {
                            setResult(RESULT_OK)
                            finish()
                        },
                    )
                }
            }
        }
    }
}

/**
 * 로그인 플로우의 시스템 바 정책과 배경색 보고 채널을 제공하는 공통 틀입니다.
 *
 * 시스템 바 숨김과 아이콘 밝기는 이 한 곳의 [EdgeToEdgeSystemBars]에서만 처리합니다. 스플래시에서 이어지는
 * 몰입형 화면이 깜빡이지 않도록 시스템 바는 숨긴 상태에서 시작합니다.
 *
 * @param content 시스템 바 변경 요청 함수를 받아 로그인 UI를 그리는 콘텐츠
 */
@Composable
private fun AuthScreenShell(
    content: @Composable (
        onEdgeToEdgeChange: (hideStatusBar: Boolean, hideNavigationBar: Boolean) -> Unit,
    ) -> Unit,
) {
    var hideStatusBar by rememberSaveable { mutableStateOf(true) }
    var hideNavigationBar by rememberSaveable { mutableStateOf(true) }
    val statusBarDarkIcons = remember { mutableStateOf(true) }
    var containerColor by remember { mutableStateOf(Color.White) }

    EdgeToEdgeSystemBars(
        statusBarDarkIcons = statusBarDarkIcons.value,
        navigationBarDarkIcons = statusBarDarkIcons.value,
        hidden = hideStatusBar,
        hideNavigationBar = hideNavigationBar,
    )

    CompositionLocalProvider(
        LocalStatusBarDarkIcons provides statusBarDarkIcons,
        LocalScaffoldBackgroundReporter provides { containerColor = it },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
                ),
                modifier = Modifier.fillMaxSize(),
                containerColor = containerColor,
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    content { hideStatus, hideNavigation ->
                        hideStatusBar = hideStatus
                        hideNavigationBar = hideNavigation
                    }
                }
            }
        }
    }
}
