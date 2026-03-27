package com.linku.core.model.auth

import com.linku.core.model.LoginResult

sealed  class LoginState {
    object Idle : LoginState()           // 초기 상태
    object Loading : LoginState()        // 로그인 진행 중
    data class Success(val result: LoginResult) : LoginState()  // 성공
    data class Error(val errorType: LoginErrorType) : LoginState()  // 실패
}

enum class LoginErrorType(val message: String) {
    INVALID_CREDENTIALS("이메일 주소 또는 비밀번호를 다시 확인하세요."),
    SERVER_ERROR("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),

    //TODO : 다인 언니로부터 어떻게 처리할 지 물어보기 밑에는 예시. 2주 이내 로그인시 부활하는가??? -> 이건 백엔드에게 상의 필요함.(프론트 해줄 수 있는게 없음...)
    INACTIVE_User_Error("계정이 비활성화되었습니다. 고객센터에 문의해주세요."),
    NETWORK_ERROR("네트워크 연결을 확인해주세요."),
    UNKNOWN_ERROR("알 수 없는 오류가 발생했습니다.")
}

sealed class AutoLoginState {
    object Idle : AutoLoginState()
    object Checking : AutoLoginState()
    object Success : AutoLoginState()
    object Failed : AutoLoginState()
}