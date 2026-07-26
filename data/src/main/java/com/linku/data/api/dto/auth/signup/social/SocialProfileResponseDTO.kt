package com.linku.data.api.dto.auth.signup.social

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SocialProfileResponseDTO(
    @field:Json(name = "userId") val userId: Long,
    @field:Json(name = "createdAt") val createdAt: String,
    @field:Json(name = "tokenResponse") val tokenResponse: SocialProfileTokenResponseDTO
)

@JsonClass(generateAdapter = true)
data class SocialProfileTokenResponseDTO(
    @field:Json(name = "accessToken") val accessToken: String,
    @field:Json(name = "refreshToken") val refreshToken: String
)