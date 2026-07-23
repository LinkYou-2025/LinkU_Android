package com.linku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.linku.core.model.SystemBarMode
import com.linku.core.model.alarm.AlarmType
import com.linku.core.system.SystemBarController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), SystemBarController {
    private var currentSystemBarMode: SystemBarMode? = null

    // 푸시 알림 수신은 Activity 또는 FCM 서비스에서 호출되므로
    // Composable 범위가 아닌 Activity 생명주기에 묶여야 한다.
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("_MainActivity", "onCreate")
        handleIntent(intent)

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

        Log.d("_MainActivity", "onNewIntent")
        handleIntent(intent)
    }

    /**
     * 전달된 [Intent]를 분석하여 알림 처리를 수행합니다.
     *
     * Intent의 extra 데이터에서 "type"과 "targetId"를 추출하며,
     * 유효한 데이터가 존재할 경우 [MainViewModel]을 통해 알림 처리 로직을 실행합니다.
     *
     * @param intent 처리를 수행할 인텐트 객체
     */
    private fun handleIntent(intent: Intent) {

        val type = intent.getStringExtra("type")
        val targetId = intent.getStringExtra("targetId")?.toLongOrNull()

        if (type != null && targetId != null) {
            viewModel.handleNotification(
                AlarmType.valueOf(type),
                targetId
            )
        }
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

