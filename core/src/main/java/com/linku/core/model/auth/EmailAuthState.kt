package com.linku.core.model.auth

sealed class EmailAuthState {
    object Idle : EmailAuthState()
    object Sending : EmailAuthState()
    data class SendSuccess(val message: String) : EmailAuthState()
    data class SendError(val message: String) : EmailAuthState()
    object Verifying : EmailAuthState()
    object VerifySuccess : EmailAuthState()
    data class VerifyError(val message: String) : EmailAuthState()
}

object AuthErrorMessages {
    const val INVALID_EMAIL_FORMAT = "잘못된 이메일 형식"
    const val EMAIL_ALREADY_EXISTS = "이미 가입된 이메일입니다."
    const val SERVER_ERROR = "서버 오류"
    const val VERIFY_FAILED = "인증 실패"
    const val NETWORK_ERROR = "네트워크 오류"
    const val INVALID_CODE = "이메일 인증 코드가 잘못 입력 되었습니다."
}