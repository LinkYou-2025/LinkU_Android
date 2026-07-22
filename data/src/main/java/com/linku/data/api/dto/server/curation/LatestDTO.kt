package com.linku.data.api.dto.server.curation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LatestDTO(
    @field:Json(name = "curationId") val curationId: Long,
    @field:Json(name = "month") val month: String,
    @field:Json(name = "thumbnailUrl") val thumbnailUrl: String
)
