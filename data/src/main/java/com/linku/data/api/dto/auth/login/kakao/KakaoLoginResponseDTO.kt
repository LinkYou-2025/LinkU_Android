package com.linku.data.api.dto.auth.login.kakao

import com.squareup.moshi.Json

data class KakaoLoginResponseDTO(
    @Json(name = "userId") val userId: Long,
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String,
    @Json(name = "status") val status: String
)