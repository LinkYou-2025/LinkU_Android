package com.linku.design.util

import android.app.Activity
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * 앱에서 자주 쓰는 SystemBar 프리셋들
 */

// 현재 호출부 없음(EdgeToEdgeSystemBars로 대체됨). 다크모드 지원 추가할 때 프리셋별로
// 색상을 다시 다뤄야 할 수 있어 삭제하지 않고 주석으로 남겨둠.
//@Composable
//fun WhiteSystemBars() { //안드로이드 자체 바텀바 흰색 적용
//    DesignSystemBars(
//        statusBarColor = Color.White,
//        navigationBarColor = Color.White,
//        darkIcons = true
//    )
//}

/**
 * 시스템 바 배경에 별도 색을 칠하지 않고 각 화면의 콘텐츠가 상태바/내비게이션 바 뒤까지
 * 자연스럽게 이어져 보이게(edge-to-edge) 두는 기본 프리셋. 아이콘 밝기와 표시 여부만 맞춤.
 * 이 프로젝트의 "일반 화면(스플래시/로그인 그라데이션 제외)" 기본값으로 사용됨.
 */
@Composable
fun EdgeToEdgeSystemBars(darkIcons: Boolean = true) {
    val view = LocalView.current
    val isPreview = LocalInspectionMode.current
    if (isPreview) return

    SideEffect {
        val window = (view.context as Activity).window
        val controller = WindowInsetsControllerCompat(window, view)

        // 항상 edge-to-edge: 화면 콘텐츠 색이 상태바/내비게이션 바 배경으로 그대로 확장됨.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        controller.show(
            WindowInsetsCompat.Type.statusBars() or
                    WindowInsetsCompat.Type.navigationBars()
        )
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT

        controller.isAppearanceLightStatusBars = darkIcons
        controller.isAppearanceLightNavigationBars = darkIcons

        // OS가 자동으로 그려주는 반투명 명암 보정 스크림을 꺼서, 화면 색이 흐려지지 않고
        // 그대로 비치게 함. (API 29+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
    }
}

// 현재 호출부 없음. 다크모드 지원 시 필요할 수 있어 삭제하지 않고 주석으로 남겨둠.
//@Composable
//fun TransparentSystemBars(darkIcons: Boolean = false) {
//    DesignSystemBars(
//        statusBarColor = Color.Transparent,
//        navigationBarColor = Color.Transparent,
//        darkIcons = darkIcons
//    )
//}
//
//@Composable
//fun DarkSystemBars() {
//    DesignSystemBars(
//        statusBarColor = Color.Black,
//        navigationBarColor = Color.Black,
//        darkIcons = false
//    )
//}