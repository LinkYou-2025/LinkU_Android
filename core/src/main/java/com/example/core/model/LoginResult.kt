package com.example.core.model

data class LoginResult(
    val userId: Long,
    val accessToken: String, // 널 비허용으로 수정.
    val refreshToken: String,
    val status: String,
    val inactiveDate: String?
)
