package com.example.linku_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.model.SystemBarMode
import com.example.core.system.SystemBarController
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import com.example.login.viewmodel.LoginViewModel

// 딥링크 데이터 클래스
data class SocialLoginData(
    val provider: String,  // 카카오, 구글
    val token: String
)


@AndroidEntryPoint
class MainActivity : ComponentActivity(), SystemBarController {
    private var currentSystemBarMode: SystemBarMode? = null

    // 소셜 로그인 딥링크 상태
    private val socialLoginState =
        androidx.compose.runtime.mutableStateOf<SocialLoginData?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.data?.let { Log.d("DEEPLINK", "onCreate uri = $it") }

        //WindowCompat.setDecorFitsSystemWindows(window, false)
        //enableEdgeToEdge()
        // 최초 실행 딥링크
        socialLoginState.value = extractDeepLinkData(intent)
        setContent {
            MainApp(
                viewModel = hiltViewModel(),
                socialLoginData = socialLoginState.value // 소셜 로그인 추가.

            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        // 딥링크만 업데이트
        socialLoginState.value = extractDeepLinkData(intent)
    }

    // 소셜 로그인 딥링크에서 데이터 추출
    private fun extractDeepLinkData(intent: Intent): SocialLoginData? {
        Log.d("SOCIAL_LOGIN", "=== 딥링크 수신 시작 ===")
        Log.d("SOCIAL_LOGIN", "intent.data = ${intent.data}")
        Log.d("SOCIAL_LOGIN", "intent.action = ${intent.action}")

        return intent.data?.let { uri ->
            Log.d("SOCIAL_LOGIN", "URI 전체: $uri")
            Log.d("SOCIAL_LOGIN", "host: ${uri.host}")
            Log.d("SOCIAL_LOGIN", "path: ${uri.path}")
            Log.d("SOCIAL_LOGIN", "query: ${uri.query}")

            if (uri.host == "linkuserver.store" && uri.path == "/auth") {
                val provider = uri.getQueryParameter("path")
                val token = uri.getQueryParameter("token")

                Log.d("SOCIAL_LOGIN", "provider: $provider")
                Log.d("SOCIAL_LOGIN", "token: ${token?.take(20)}...")  // 토큰 앞 20자만

                if (token != null && provider != null) {
                    Log.d("SOCIAL_LOGIN", " SocialLoginData 생성 성공!")
                    SocialLoginData(provider = provider, token = token)
                } else {
                    Log.e("SOCIAL_LOGIN", " token 또는 provider가 null")
                    null
                }
            } else {
                Log.d("SOCIAL_LOGIN", " 소셜 로그인 딥링크 아님 (host/path 불일치)")
                null
            }
        }
    }



        /**
     *  SystemBarController 구현
     * 앱 전역 시스템 바 단일 제어 지점
     */
    override fun setSystemBarMode(mode: SystemBarMode) {
        if (currentSystemBarMode == mode) return
        currentSystemBarMode = mode

        WindowCompat.setDecorFitsSystemWindows(
            window,
            mode == SystemBarMode.VISIBLE
        )

        val controller = WindowInsetsControllerCompat(
            window,
            window.decorView
        )

        if (mode == SystemBarMode.VISIBLE) {
            controller.show(
                WindowInsetsCompat.Type.statusBars() or
                        WindowInsetsCompat.Type.navigationBars()
            )
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        } else {
            controller.hide(
                WindowInsetsCompat.Type.statusBars() or
                        WindowInsetsCompat.Type.navigationBars()
            )
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

    }
}

