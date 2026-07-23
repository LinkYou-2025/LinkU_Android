package com.linku.data.api.dto.server.curation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JobKeywordDTO(
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "count")
    val count: Int,
)