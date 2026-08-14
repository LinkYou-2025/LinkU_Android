package com.linku.design.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 상태바 아이콘을 어둡게(검정) 보여줄지 여부를 하위 컴포저블이 일시적으로 바꿀 수 있게 하는
 * CompositionLocal입니다.
 *
 * 활성 route의 기본 아이콘 정책은 MainApp/MainScreen이 소유합니다. 하위 화면은 로딩이나
 * 다이얼로그처럼 route 안에서 잠깐 달라지는 상태만 `LocalStatusBarDarkIcons.current.value`로
 * override해야 합니다. route 기본값을 화면의 진입/해제 효과에서 변경하면 이전 화면의
 * `onDispose`가 새 화면 정책을 덮어쓸 수 있으므로 사용하지 않습니다.
 */
val LocalStatusBarDarkIcons = staticCompositionLocalOf<MutableState<Boolean>> {
    error("LocalStatusBarDarkIcons is not provided. MainScreen에서 CompositionLocalProvider로 감싸야 함.")
}
