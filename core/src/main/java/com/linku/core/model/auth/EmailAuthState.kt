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
    const val INVALID_EMAIL_FORMAT = "이메일 양식이 올바르지 않습니다!"
    const val EMAIL_ALREADY_EXISTS = "이미 가입된 이메일입니다."
    const val SERVER_ERROR = "서버 오류"
    const val VERIFY_FAILED = "인증번호가 올바르지 않습니다! 다시 시도해주세요."
    const val NETWORK_ERROR = "네트워크 오류"
}