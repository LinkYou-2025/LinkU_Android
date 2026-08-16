package com.linku.design.util

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
 *
 * @param statusBarDarkIcons 상태바 아이콘을 어둡게 표시할지 여부
 * @param navigationBarDarkIcons 내비게이션 바 아이콘을 어둡게 표시할지 여부
 * @param hidden true면 상태바를 완전히 숨김(스플래시, 로그인 그라데이션 화면 등 몰입형 화면
 * 전용). 예전엔 별도의 [com.linku.core.system.SystemBarController]로 숨김/복원을 처리했는데,
 * 같은 Window를 두 체계가 각자 다른 타이밍에 건드리면서 경합이 생겨(로그아웃/탈퇴 직후 Toast로
 * 윈도우 포커스가 흔들리는 시점 등) 시스템 바가 다시 보이는 채로 남는 문제가 있었음. 이
 * 파라미터로 흡수해서 시스템 바 제어를 이 한 곳으로 통일함.
 * @param hideNavigationBar true면 내비게이션 바를 완전히 숨김. 기본값은 [hidden]과 동일해서
 * 대부분의 화면(스플래시 등)은 상태바/내비게이션 바를 함께 숨기지만, 약관 동의 바텀시트처럼
 * 상태바는 숨긴 배경을 유지하면서도 내비게이션 바는 항상 보여야 하는 화면에서 따로 지정할 수
 * 있게 분리함.
 */
@Composable
fun EdgeToEdgeSystemBars(
    statusBarDarkIcons: Boolean = true,
    navigationBarDarkIcons: Boolean = true,
    hidden: Boolean = false,
    hideNavigationBar: Boolean = hidden,
) {
    val view = LocalView.current
    val isPreview = LocalInspectionMode.current
    if (isPreview) return

    // SideEffect는 리컴포지션마다 실행되는데, Window/시스템 서버와 통신하는 호출들이라
    // 아이콘 밝기/hidden이 실제로 바뀔 때(+최초 진입)만 실행되도록 key로 제한함.
    DisposableEffect(statusBarDarkIcons, navigationBarDarkIcons, hidden, hideNavigationBar) {
        val activity = view.context.findActivityOrNull()
        if (activity == null) {
            return@DisposableEffect onDispose {}
        }

        val window = activity.window

        // window.statusBarColor/navigationBarColor, isStatusBarContrastEnforced는 API 35(edge-to-edge
        // 강제화)부터 deprecated됨. 대체 API인 enableEdgeToEdge()는 Activity.onCreate에서 한 번만
        // 호출하는 걸 전제로 하고 화면별 darkIcons/hidden 동적 전환을 지원하지 않아서, 이 프로젝트의
        // "화면마다 런타임에 바뀌는 시스템 바 제어" 구조엔 그대로 못 씀. API 35 미만 기기에서는 여전히
        // 유효하게 동작하는 API라 기능상 문제 없이 경고만 억제함.
        @Suppress("DEPRECATION")
        fun applySystemBars() {
            val controller = WindowInsetsControllerCompat(window, view)

            // 항상 edge-to-edge: 화면 콘텐츠 색이 상태바/내비게이션 바 배경으로 그대로 확장됨.
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // setDecorFitsSystemWindows(false)만으로는 3버튼 내비게이션 바가 기기 기본 회색
            // 배경을 유지하는 경우가 있어(특히 삼성 등 일부 OEM), 명시적으로 투명 처리함.
            // 이게 없으면 화면 위 딤 오버레이가 하단 바에는 비치지 않아 어색하게 끊겨 보임.
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT

            if (hidden) {
                controller.hide(WindowInsetsCompat.Type.statusBars())
            } else {
                controller.show(WindowInsetsCompat.Type.statusBars())
            }

            if (hideNavigationBar) {
                controller.hide(WindowInsetsCompat.Type.navigationBars())
            } else {
                controller.show(WindowInsetsCompat.Type.navigationBars())
            }

            controller.systemBarsBehavior = if (hidden || hideNavigationBar) {
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }

            controller.isAppearanceLightStatusBars = statusBarDarkIcons
            controller.isAppearanceLightNavigationBars = navigationBarDarkIcons

            // OS가 자동으로 그려주는 반투명 명암 보정 스크림을 꺼서, 화면 색이 흐려지지 않고
            // 그대로 비치게 함. (API 29+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isStatusBarContrastEnforced = false
                window.isNavigationBarContrastEnforced = false
            }
        }

        applySystemBars()

        // hidden=true인 상태에서 Toast(탈퇴 완료 안내 등)가 뜨면 별도 Window가 잠깐
        // 포커스를 가져가면서 OS가 숨겼던 시스템 바를 다시 보여줌. 아이콘 밝기/hidden 값 자체는
        // 안 바뀌므로 위 DisposableEffect는 재실행되지 않아 숨김이 복구되지 않았음(탈퇴 →
        // 로그인 화면 진입 시 내비게이션 바가 계속 떠 있던 버그의 원인). 윈도우 포커스를 다시
        // 얻는 시점마다 재적용해서 복구함.
        val focusListener = android.view.ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
            if (hasFocus) applySystemBars()
        }
        view.viewTreeObserver.addOnWindowFocusChangeListener(focusListener)

        onDispose {
            view.viewTreeObserver.removeOnWindowFocusChangeListener(focusListener)
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
