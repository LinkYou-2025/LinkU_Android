package com.example.design.util

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Design module – Android System Bars controller
 *
 * - StatusBar / NavigationBar 색상 제어
 * - 아이콘 밝기 제어
 * - edge-to-edge 환경 공통 처리
 */
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

    SideEffect {
        val window = (view.context as Activity).window
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

        window.statusBarColor = statusBarColor.toArgb()
        window.navigationBarColor = navigationBarColor.toArgb()

        controller.isAppearanceLightStatusBars = darkIcons
        controller.isAppearanceLightNavigationBars = darkIcons
    }
}
