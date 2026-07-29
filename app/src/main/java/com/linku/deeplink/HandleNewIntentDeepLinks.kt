package com.linku.deeplink

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.util.Consumer
import androidx.navigation.NavHostController

/**
 * 실행 중인 Activity가 새로 받은 딥링크 Intent를 [NavHostController]에 전달합니다.
 *
 * 컴포지션에 진입하면 새 Intent 리스너를 등록하고, 컴포지션에서 제거될 때 리스너도 해제합니다.
 *
 * @param navigator 새 Intent의 딥링크를 처리할 내비게이션 컨트롤러
 */
@Composable
internal fun HandleNewIntentDeepLinks(
    navigator: NavHostController,
) {
    val activity = LocalActivity.current as? ComponentActivity ?: return

    DisposableEffect(activity, navigator) {
        val listener = Consumer<Intent> { intent ->
            navigator.handleDeepLink(intent)
        }

        activity.addOnNewIntentListener(listener)

        onDispose {
            activity.removeOnNewIntentListener(listener)
        }
    }
}
