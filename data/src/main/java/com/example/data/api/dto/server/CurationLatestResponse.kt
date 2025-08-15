package com.example.data.api.dto.server

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class CurationLatestResponse(
    @Json(name = "curationId") val curationId: Long,
    @Json(name = "month") val month: String,
    @Json(name = "thumbnailUrl") val thumbnailUrl: String
)