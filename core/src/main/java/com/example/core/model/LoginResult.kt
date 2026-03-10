package com.example.core.model

data class LoginResult(
    val userId: Long,
    val accessToken: String,   // token에서 수정
    val refreshToken: String,
    val status: String,
    val inactiveDate: String? = null
)
