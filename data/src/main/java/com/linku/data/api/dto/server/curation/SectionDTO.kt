package com.linku.data.api.dto.server.curation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SectionDTO(
    @field:Json(name = "section") val section: Int,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "imageUrl") val imageUrl: String
)
