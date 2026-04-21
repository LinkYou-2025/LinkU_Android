package com.linku.data.api.dto.auth.login.kakao

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KakaoLoginResponseDTO(
    @field:Json(name = "userId") val userId: Long,
    @field:Json(name = "accessToken") val accessToken: String,
    @field:Json(name = "refreshToken") val refreshToken: String,
    @field:Json(name = "status") val status: String
)