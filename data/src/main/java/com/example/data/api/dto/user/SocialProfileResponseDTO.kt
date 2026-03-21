package com.example.data.api.dto.user

import com.squareup.moshi.Json

data class SocialProfileResponseDTO(
    @Json(name = "userId") val userId: Long,
    @Json(name = "createdAt") val createdAt: String
)