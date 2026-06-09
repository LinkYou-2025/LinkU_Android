package com.linku.login.mvi

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class MviContainerImpl<S : UiState, E : UiSideEffect>(
    initialState: S,
) : MviContainer<S, E> {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<S> = _state.asStateFlow()

    private val _sideEffect = Channel<E>(capacity = Channel.BUFFERED)
    override val sideEffect: Flow<E> = _sideEffect.receiveAsFlow()

    override fun updateState(reducer: S.() -> S) {
        _state.update { it.reducer() }
    }

    override fun postSideEffect(effect: E) {
        _sideEffect.trySend(effect)
    }
}