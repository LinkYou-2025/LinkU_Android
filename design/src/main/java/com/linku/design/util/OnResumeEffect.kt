package com.linku.design.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Composable이 속한 LifecycleOwner의 ON_RESUME 이벤트를 감지하여
 * 지정된 콜백을 실행하는 Effect입니다.
 *
 * 화면이 처음 표시되거나, 외부 화면(브라우저, 시스템 설정 등)으로
 * 이동한 뒤 다시 앱으로 복귀했을 때 실행됩니다.
 *
 * 내부적으로 LifecycleEventObserver를 등록하며,
 * Composable이 Composition에서 제거될 때 Observer를 해제하여
 * 메모리 누수를 방지합니다.
 *
 * 사용 예시:
 *
 * OnResume {
 *     viewModel.sendIntent(NotificationIntent.RefreshSystemAlarm)
 * }
 *
 *
 * @param onResume ON_RESUME 이벤트 발생 시 실행할 콜백
 */
@Composable
fun OnResumeEffect(
    onResume: () -> Unit // onResume시 호출할 콜백
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onResume()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
