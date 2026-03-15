package com.example.data.api.dto.login.kakao

import com.squareup.moshi.Json

data class KakaoLoginResponseDTO(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String?, //sns 회원가입 중간에눈 null로 옮
    val status: String
)

data class KakaoLoginRequestDTO(
    val token : String
)
