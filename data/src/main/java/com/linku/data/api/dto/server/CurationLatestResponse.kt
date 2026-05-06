package com.linku.data.api.dto.server


import com.squareup.moshi.Json


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurationLatestResponse(
    @field:Json(name = "curationId") val curationId: Long,
    @field:Json(name = "month") val month: String,
    @field:Json(name = "thumbnailUrl") val thumbnailUrl: String
)