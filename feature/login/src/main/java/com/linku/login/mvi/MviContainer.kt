package com.linku.login.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel이 위임(by)받아 사용할 피처 모듈 공통 MVI 컨테이너 인터페이스
 */
interface MviContainer<S : UiState, E : UiSideEffect> {
    val state: StateFlow<S>
    val sideEffect: Flow<E>

    fun updateState(reducer: S.() -> S)
    fun postSideEffect(effect: E)
}

/**
 * ViewModel 내부에서 초기 상태를 가지고 컨테이너를 간편하게 조립하기 위한 팩토리 함수
 */
fun <S : UiState, E : UiSideEffect> mviContainer(initialState: S): MviContainer<S, E> =
    MviContainerImpl(initialState)