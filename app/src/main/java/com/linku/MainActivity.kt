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

/**
 * 앱의 Compose UI를 시작하고 Activity 수준의 Intent 및 시스템 바 상태를 관리합니다.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity(), SystemBarController {
    private var currentSystemBarMode: SystemBarMode? = null

    // 푸시 알림 수신은 Activity 또는 FCM 서비스에서 호출되므로
    // Composable 범위가 아닌 Activity 생명주기에 묶여야 한다.
    private val viewModel: MainViewModel by viewModels()

    /**
     * 앱 전역 Compose UI를 구성하고 콜드 스타트 딥링크를 내비게이션 그래프에 전달합니다.
     *
     * @param savedInstanceState 이전 Activity 인스턴스에서 복원할 상태
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("_MainActivity", "onCreate")
        handleIntent(intent)

        //WindowCompat.setDecorFitsSystemWindows(window, false)
        //enableEdgeToEdge()
        // 콜드 스타트의 초기 Intent는 MainApp의 NavHost 딥링크 그래프가 처리합니다.
        setContent {
            MainApp(
                viewModel = hiltViewModel(),
            )
        }
    }

    /**
     * 실행 중 새 Intent를 받으면 등록된 리스너에 전달하고 Activity의 최신 Intent를 갱신합니다.
     *
     * @param intent 새로 전달된 Intent
     */
    override fun onNewIntent(intent: Intent) {
        // ComponentActivity에 등록된 새 Intent 리스너가 웜 스타트 딥링크를 처리합니다.
        super.onNewIntent(intent)
        // 이후 Activity.intent 조회에서도 가장 최근에 받은 Intent를 반환하도록 교체합니다.
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
            ?.let { runCatching { AlarmType.valueOf(it) }.getOrNull() }
            ?: return

        val targetId = intent.getStringExtra("targetId")?.toLongOrNull() ?: return
        val alarmId = intent.getStringExtra("alarmId")?.toLongOrNull()

        viewModel.handleNotification(type, targetId, alarmId)

        // 처리 완료한 알림 Intent는 소비 처리.
        // Activity 재생성(예: 화면 회전) 시 동일 Intent가 다시 전달되어
        // 중복 읽음 처리 및 중복 네비게이션이 발생하는 것을 방지
        intent.removeExtra("type")
        intent.removeExtra("targetId")
        intent.removeExtra("alarmId")
    }

    override fun onResume() {
        super.onResume()
    }


    /**
     * 앱 전역 시스템 바 표시 상태와 콘텐츠의 시스템 윈도우 맞춤 여부를 함께 적용합니다.
     *
     * Compose의 시스템 바 효과와 상태가 어긋나지 않도록 동일한 모드도 매 호출마다 다시 적용합니다.
     *
     * @param mode 적용할 시스템 바 표시 모드
     */
    override fun setSystemBarMode(mode: SystemBarMode) {
        // DesignSystemBars(Compose SideEffect)가 같은 Window를 별도로 직접 제어하기 때문에
        // 이전 호출 결과를 캐시해서 조기 반환하면 실제 상태와 어긋나 복구 호출이 무시될 수 있음.
        // 그래서 매번 무조건 반영함(hide/show는 반복 호출해도 안전함).

        // 시스템 바 표시 여부에 맞춰 앱 콘텐츠의 시스템 윈도우 inset 처리도 전환합니다.
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

