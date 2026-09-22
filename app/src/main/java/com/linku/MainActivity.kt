package com.linku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.linku.core.model.alarm.AlarmType
import dagger.hilt.android.AndroidEntryPoint

/**
 * 앱의 Compose UI를 시작하고 Activity 수준의 Intent를 관리합니다.
 *
 * 시스템 바 표시/숨김은 MainScreen의 EdgeToEdgeSystemBars(hideSystemBars)에서 통일해서 처리하므로
 * 여기서는 별도로 다루지 않습니다.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
     * 전달된 [Intent]를 분석하여 외부 공유 링크와 알림을 각각 처리합니다.
     *
     * @param intent 처리를 수행할 인텐트 객체
     */
    private fun handleIntent(intent: Intent) {
        handleSharedTextIntent(intent)
        handleNotificationIntent(intent)
    }

    /**
     * 외부 앱의 `ACTION_SEND` 인텐트에서 공유 텍스트를 꺼내 ViewModel에 전달합니다.
     *
     * LinkU가 공유 대상으로 등록한 `text/plain`만 처리하며, 전달을 마친 extra는 Activity 재생성 시
     * 같은 링크 저장 화면이 다시 열리지 않도록 즉시 소비합니다.
     *
     * @param intent 외부 앱에서 전달된 공유 인텐트
     */
    private fun handleSharedTextIntent(intent: Intent) {
        if (intent.action != Intent.ACTION_SEND || intent.type != SHARED_TEXT_MIME_TYPE) {
            return
        }

        viewModel.handleSharedText(intent.getStringExtra(Intent.EXTRA_TEXT))
        intent.removeExtra(Intent.EXTRA_TEXT)
    }

    /**
     * 전달된 [Intent]를 분석하여 알림 처리를 수행합니다.
     *
     * Intent의 extra 데이터에서 "type"과 "targetId"를 추출하며,
     * 유효한 데이터가 존재할 경우 [MainViewModel]을 통해 알림 처리 로직을 실행합니다.
     *
     * @param intent 처리를 수행할 인텐트 객체
     */
    private fun handleNotificationIntent(intent: Intent) {
        val rawType = intent.getStringExtra("type") ?: return
        val type = AlarmType.fromOrNull(rawType) ?: return

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

    private companion object {
        /** 외부 앱의 URL 공유에 사용하는 Android 표준 텍스트 MIME 타입입니다. */
        const val SHARED_TEXT_MIME_TYPE = "text/plain"
    }
}
