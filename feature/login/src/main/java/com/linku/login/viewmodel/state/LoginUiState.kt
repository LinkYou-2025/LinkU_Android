package com.linku.login.viewmodel.state

import com.linku.core.model.auth.LoginState
import com.linku.login.mvi.UiState

/**
 * 로그인 화면에서 쓸 상태
 */
internal data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loginState: LoginState = LoginState.Idle
) : UiState