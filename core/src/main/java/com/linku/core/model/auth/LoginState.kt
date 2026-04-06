package com.linku.core.model.auth

import com.linku.core.model.LoginResult

sealed  class LoginState {
    object Idle : LoginState()           // 초기 상태
    object Loading : LoginState()        // 로그인 진행 중
    data class Success(val result: LoginResult) : LoginState()  // 성공
    data class Error(val errorType: LoginErrorType) : LoginState()  // 실패
}

enum class LoginErrorType(val message: String) {
    INVALID_CREDENTIALS("이메일 또는 비밀번호가 올바르지 않습니다."),
    SERVER_ERROR("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    NETWORK_ERROR("네트워크 연결을 확인해주세요."),
    UNKNOWN_ERROR("알 수 없는 오류가 발생했습니다.")
}

sealed class AutoLoginState {
    object Idle : AutoLoginState()
    object Checking : AutoLoginState()
    object Success : AutoLoginState()
    object Failed : AutoLoginState()
}