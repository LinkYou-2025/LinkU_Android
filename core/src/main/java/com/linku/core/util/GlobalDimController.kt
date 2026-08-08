package com.linku.core.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 화면 전체(하단 탭바 포함) 딤 처리 상태를 앱 전역에서 공유하는 싱글턴 컨트롤러.
 *
 * MainViewModel과, 딤을 켜야 하는 각 feature ViewModel(예: MyPageViewModel)이
 * 이 컨트롤러를 동일 인스턴스로 생성자 주입받아 상태를 공유한다. Composable 트리를
 * 거쳐 onDimmedChange 콜백을 relay할 필요 없이 각 ViewModel에서 직접 show()/hide()를 호출한다.
 */
@Singleton
class GlobalDimController @Inject constructor() {
    private val _isDimmed = MutableStateFlow(false)
    val isDimmed: StateFlow<Boolean> = _isDimmed.asStateFlow()

    fun show() {
        _isDimmed.value = true
    }

    fun hide() {
        _isDimmed.value = false
    }
}
