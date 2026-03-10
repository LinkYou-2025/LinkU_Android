package com.example.data.api.dto.login.kakao

import com.squareup.moshi.Json

data class KakaoLoginResponseDTO(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
    val status: String
)

data class KakaoLoginRequestDTO(
    val token : String
)
