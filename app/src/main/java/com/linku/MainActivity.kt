package com.linku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.linku.core.model.SystemBarMode
import com.linku.core.system.SystemBarController
import com.linku.deeplink.extractSocialDeepLinkData
import dagger.hilt.android.AndroidEntryPoint
import com.linku.deeplink.SocialDeepLinkBus
@AndroidEntryPoint
class MainActivity : ComponentActivity(), SystemBarController {
    private var currentSystemBarMode: SystemBarMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.data?.let { Log.d("DEEPLINK", "onCreate uri = $it") }
        // 앱이 꺼진 상태에서 딥링크로 실행된 경우
        intent?.let { handleDeepLinkIntent(it) }
        //WindowCompat.setDecorFitsSystemWindows(window, false)
        //enableEdgeToEdge()
        // 최초 실행 딥링크
        setContent {
            MainApp(
                viewModel = hiltViewModel(),


            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        // 앱 실행 중 딥링크 들어온 경우
        handleDeepLinkIntent(intent)
    }

    private fun handleDeepLinkIntent(intent: Intent) {
        val uri = intent.data ?: return

        when (uri.host) {
            "auth" -> {
                val data = extractSocialDeepLinkData(intent) ?: return
                Log.d("DEEPLINK", "소셜 로그인 딥링크 수신: $data")
                SocialDeepLinkBus.emit(data)  // ← 다음 단계에서 만들 파일
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

