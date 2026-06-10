package com.linku.login.viewmodel.state

import com.linku.login.mvi.UiSideEffect

internal sealed interface LoginUiEffect : UiSideEffect {
    object NavigateToSignUp : LoginUiEffect
    object NavigateToResetPassword : LoginUiEffect
    object LoginSuccess : LoginUiEffect
}