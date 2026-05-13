package com.linku.login.ui.model

data class EmailVerificationUiState(
    val email: String = "",
    val code: String = "",
    val isSending: Boolean = false,
    val isVerifying: Boolean = false,
    val sendResult: String? = null,
    val verifyResult: String? = null,
    val isCodeSent: Boolean = false,
    val isCodeValid: Boolean = false,
    val emailValid: Boolean = false,
    val timer: Int = 0,
)