package com.linku.data.api.dto.auth.signup.social

import com.squareup.moshi.Json

data class SocialProfileResponseDTO(
    @Json(name = "userId") val userId: Long,
    @Json(name = "createdAt") val createdAt: String
)