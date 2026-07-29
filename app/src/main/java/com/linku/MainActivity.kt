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
    /** 중복된 시스템 바 갱신을 방지하기 위해 마지막으로 적용한 모드입니다. */
    private var currentSystemBarMode: SystemBarMode? = null

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
     * 이미 적용된 모드와 같으면 중복된 Window 작업을 수행하지 않습니다.
     *
     * @param mode 적용할 시스템 바 표시 모드
     */
    override fun setSystemBarMode(mode: SystemBarMode) {
        if (currentSystemBarMode == mode) return
        currentSystemBarMode = mode

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

