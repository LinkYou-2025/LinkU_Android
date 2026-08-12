package com.linku.design.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * 현재 화면의 배경색을 상위 Scaffold에 보고하기 위한 채널입니다.
 *
 * 앱 최상단(MainApp 등)에서 실제 Scaffold containerColor 상태와 연결해 제공하며,
 * 화면들은 [ReportScaffoldBackground]를 통해서만 이 채널에 접근합니다.
 */
val LocalScaffoldBackgroundReporter = staticCompositionLocalOf<(Color) -> Unit> { {} }

/**
 * 화면 배경이 상위 Scaffold의 기본값(흰색)과 다를 때, 진입 시 그 배경색을 보고하고
 * 화면을 벗어나면 흰색으로 되돌립니다.
 *
 * 시스템 내비게이션 바 뒤로 비치는 상위 Scaffold의 containerColor를 화면의 실제
 * 배경색과 맞춰서, 화면 전환 시 배경색이 안 맞아 보이는 깜빡임을 없애기 위한 용도입니다.
 *
 * @param color 이 화면이 컴포지션에 있는 동안 상위 Scaffold에 반영할 배경색
 */
@Composable
fun ReportScaffoldBackground(color: Color) {
    val reportBackground = LocalScaffoldBackgroundReporter.current

    DisposableEffect(color) {
        reportBackground(color)
        onDispose { reportBackground(Color.White) }
    }
}
