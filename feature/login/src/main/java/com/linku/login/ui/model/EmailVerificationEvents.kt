package com.linku.login.ui.model

data class EmailVerificationEvents(
    val onEmailChange: (String) -> Unit,
    val onCodeChange: (String) -> Unit,
    val onSendCode: () -> Unit,
    val onVerifyCode: () -> Unit,
)