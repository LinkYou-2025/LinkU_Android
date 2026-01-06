package com.example.design.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * 앱에서 자주 쓰는 SystemBar 프리셋들
 */

@Composable
fun WhiteSystemBars() { //안드로이드 자체 바텀바 흰색 적용
    DesignSystemBars(
        statusBarColor = Color.White,
        navigationBarColor = Color.White,
        darkIcons = true
    )
}

@Composable
fun TransparentSystemBars(darkIcons: Boolean = false) {
    DesignSystemBars(
        statusBarColor = Color.Transparent,
        navigationBarColor = Color.Transparent,
        darkIcons = darkIcons
    )
}

@Composable
fun DarkSystemBars() {
    DesignSystemBars(
        statusBarColor = Color.Black,
        navigationBarColor = Color.Black,
        darkIcons = false
    )
}