package com.example.core.model

data class LoginResult(
    val userId: Long,
    val accessToken: String,   // token에서 수정
    val refreshToken: String?, //카카오톡 회원가입 중에는 null
    val status: String,
    val inactiveDate: String? = null
)
