package com.linku.data.api.dto.server.curation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UnreadLinkDTO(
    @field:Json(name = "title")
    val title: String,

    @field:Json(name = "domain")
    val domain: String,

    @field:Json(name = "domainImageUrl")
    val domainImageUrl: String,

    @field:Json(name = "linkuImageUrl")
    val linkuImageUrl: String?
)
