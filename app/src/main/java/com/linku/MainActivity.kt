package com.linku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.linku.core.model.SystemBarMode
import com.linku.core.system.SystemBarController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), SystemBarController {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.data?.let { Log.d("DEEPLINK", "onCreate uri = $it") }
        // 앱이 꺼진 상태에서 딥링크로 실행된 경우
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


    }

    override fun onResume() {
        super.onResume()
    }


    /**
     *  SystemBarController 구현
     * 앱 전역 시스템 바 단일 제어 지점
     */
    override fun setSystemBarMode(mode: SystemBarMode) {
        // DesignSystemBars(Compose SideEffect)가 같은 Window를 별도로 직접 제어하기 때문에
        // 이전 호출 결과를 캐시해서 조기 반환하면 실제 상태와 어긋나 복구 호출이 무시될 수 있음.
        // 그래서 매번 무조건 반영함(hide/show는 반복 호출해도 안전함).
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

