package com.linku

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.linku.core.model.SystemBarMode
import com.linku.core.system.SystemBarController
import dagger.hilt.android.AndroidEntryPoint

/**
 * 앱의 Compose UI를 시작하고 Activity 수준의 Intent 및 시스템 바 상태를 관리합니다.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity(), SystemBarController {
    /**
     * 앱 전역 Compose UI를 구성하고 콜드 스타트 딥링크를 내비게이션 그래프에 전달합니다.
     *
     * @param savedInstanceState 이전 Activity 인스턴스에서 복원할 상태
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

