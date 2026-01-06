package com.example.design.util

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
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
    statusBarColor: Color = Color.White,
    navigationBarColor: Color = Color.White,
    darkIcons: Boolean = true
) {
    val view = LocalView.current
    val isPreview = LocalInspectionMode.current

    if (isPreview) return

    SideEffect {
        val window = (view.context as Activity).window

        // edge-to-edge 유지
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 시스템 바 색상
        window.statusBarColor = statusBarColor.toArgb()
        window.navigationBarColor = navigationBarColor.toArgb()

        // 아이콘 색상
        WindowInsetsControllerCompat(window, view).apply {
            isAppearanceLightStatusBars = darkIcons
            isAppearanceLightNavigationBars = darkIcons
        }
    }
}