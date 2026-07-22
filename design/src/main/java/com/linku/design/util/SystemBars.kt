package com.linku.design.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * [Context]가 (테마가 적용된 [ContextWrapper] 등으로) 감싸져 있어도 그 안의 실제
 * [Activity]를 찾아냄. 바로 `as Activity`로 캐스팅하면 감싸진 Context일 때
 * [ClassCastException]으로 크래시할 수 있어 안전하게 언래핑함.
 */
internal fun Context.findActivityOrNull(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return context as? Activity
}

/**
 * Design module – Android System Bars controller
 *
 * - StatusBar / NavigationBar 색상 제어
 * - 아이콘 밝기 제어
 * - edge-to-edge 환경 공통 처리
 */
// statusBarColor/navigationBarColor/isStatusBarContrastEnforced/isNavigationBarContrastEnforced는
// Android 15(API 35)부터 deprecated(edge-to-edge 강제로 no-op)이지만, 이 화면들(Login 계열,
// immersive = true)은 스와이프로 바를 잠깐 꺼냈을 때(BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
// API 34 이하 기기에서는 여전히 실제로 그려지므로 의도적으로 유지함.
@Suppress("DEPRECATION")
@Composable
fun DesignSystemBars(
    statusBarColor: Color = Color.White, //상태바 배경 색상
    navigationBarColor: Color = Color.White, //하단 네비게이션 바 배경 색상
    darkIcons: Boolean = true, // true인 경우, 어두운 아이콘(밝은 배경)
    immersive: Boolean = false
    //스플래쉬, 앱 진입 애니메이션은 디자이너와 상의 끝에 바텀바 안보이도록 함.
    // 숨김 기능 추가(immersive: Boolean = false)
) {
    val view = LocalView.current
    val isPreview = LocalInspectionMode.current
    if (isPreview) return

    // 매 리컴포지션마다가 아니라 실제로 값이 바뀔 때만 Window/시스템 서버와 통신하도록 함.
    DisposableEffect(statusBarColor, navigationBarColor, darkIcons, immersive) {
        val activity = view.context.findActivityOrNull()
        if (activity == null) {
            return@DisposableEffect onDispose {}
        }

        val window = activity.window
        val controller = WindowInsetsControllerCompat(window, view)

        WindowCompat.setDecorFitsSystemWindows(window, !immersive)

        //immersive 여부에 따라 바텀바 레이아웃 처리 진행함.
        if (immersive) {
            //스플래쉬, 앱 진입 애니메이션인 경우
            controller.hide(
                WindowInsetsCompat.Type.statusBars() or
                        WindowInsetsCompat.Type.navigationBars()
            )
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            //그 외 화면들 원래대로 작동함.
            controller.show(
                WindowInsetsCompat.Type.statusBars() or
                        WindowInsetsCompat.Type.navigationBars()
            )
        }

        // Android 15(targetSdk 35)부터는 edge-to-edge가 강제되어
        // window.statusBarColor / navigationBarColor 설정이 no-op이 됨.
        // (구버전 호환을 위해 값 자체는 계속 지정함)
        window.statusBarColor = statusBarColor.toArgb()
        window.navigationBarColor = navigationBarColor.toArgb()

        controller.isAppearanceLightStatusBars = darkIcons
        controller.isAppearanceLightNavigationBars = darkIcons

        // 위 색상 지정이 no-op인 기기에서, OS가 자동으로 그려주는 반투명 명암 보정 스크림이
        // 이전 화면(Login 등)에서 남은 색을 반영해 잔상처럼 비치는 것을 막기 위해 비활성화함.
        // (isStatusBarContrastEnforced/isNavigationBarContrastEnforced는 API 29+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }

        onDispose {}
    }
}
