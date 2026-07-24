package com.linku.core.model

data class LoginResult(
    val userId: Long,
    val accessToken: String, // 널 비허용으로 수정.
    val refreshToken: String?, // 탈퇴 유예기간(INACTIVE) 계정 로그인 시 서버가 null로 내려줌
    val status: String,
    val inactiveDate: String?
)
