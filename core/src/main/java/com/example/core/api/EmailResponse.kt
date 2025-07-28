package com.example.core.api

data class EmailCodeResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: String?
)

data class EmailVerifyResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: VerifyResult?
)

data class VerifyResult(
    val success: Boolean
)