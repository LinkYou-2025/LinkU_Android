package com.example.data.api.dto.server

import com.squareup.moshi.Json

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class CurationLikeStatusResponseDTO(@Json(name = "liked") val liked: Boolean)