package com.linku.design.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 상태바/내비게이션 바 아이콘을 어둡게(검정) 보여줄지 여부를 화면(하위 컴포저블)에서
 * 직접 읽고 바꿀 수 있게 하는 CompositionLocal.
 *
 * 기본은 true(검정 아이콘, 밝은 배경 기준). File 탭처럼 상태바 뒤로 어두운 배경(그라데이션 등)이
 * 비치는 화면은 진입 시 false로 바꿨다가 [androidx.compose.runtime.DisposableEffect]의
 * onDispose에서 다시 true로 되돌리면 됨. MainApp/MainScreen을 거치는 콜백 없이
 * 화면이 직접 `LocalStatusBarDarkIcons.current.value`를 변경한다.
 */
val LocalStatusBarDarkIcons = staticCompositionLocalOf<MutableState<Boolean>> {
    error("LocalStatusBarDarkIcons is not provided. MainScreen에서 CompositionLocalProvider로 감싸야 함.")
}
