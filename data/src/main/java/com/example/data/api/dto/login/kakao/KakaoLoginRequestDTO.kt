package com.example.data.api.dto.login.kakao

import com.squareup.moshi.Json

data class KakaoLoginResponseDTO(
    @Json(name = "userId") val userId: Long,
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String, // sns 회원가입 중간에는 null
    @Json(name = "status") val status: String
)

data class KakaoLoginRequestDTO(
    val token : String
)
