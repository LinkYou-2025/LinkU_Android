package com.linku.login.viewmodel.state

import com.linku.core.model.auth.LoginState
import com.linku.login.mvi.UiState

/**
 * 로그인 화면에서 쓸 상태
 */
internal data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loginState: LoginState = LoginState.Idle,
    // 탈퇴 유예기간(INACTIVE) 계정으로 로그인 시도 시 계정 복구 여부를 묻는 모달 표시 여부
    val showRecoverModal: Boolean = false
) : UiState