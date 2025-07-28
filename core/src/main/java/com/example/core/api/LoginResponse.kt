package com.example.core.api

// 로그인 결과 DTO
data class LoginResult(
    val userId: Int,
    val accessToken: String,
    val status: String,
    val inactiveDate: String?
)

// 로그인 응답 DTO
data class LoginResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: LoginResult?
)

//// 로그인 응답의 result에 포함되는 JWT 토큰
//data class LoginResult(
//    val accessToken: String
//)