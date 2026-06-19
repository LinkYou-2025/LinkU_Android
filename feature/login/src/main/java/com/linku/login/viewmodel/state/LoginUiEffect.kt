package com.linku.login.viewmodel.state

import com.linku.login.mvi.UiSideEffect

internal sealed interface LoginUiEffect : UiSideEffect {
    object LoginSuccess : LoginUiEffect
    object AutoLoginSuccess : LoginUiEffect
    object AutoLoginFail : LoginUiEffect
}