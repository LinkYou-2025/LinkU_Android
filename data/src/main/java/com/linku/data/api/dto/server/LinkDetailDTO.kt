package com.linku.data.api.dto.server

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LinkDetailDTO(

    @field:Json(name = "title")
    val title: String,

    @field:Json(name = "summary")
    val summary: String,

    @field:Json(name = "source")
    val source: String,

    @field:Json(name = "url")
    val url: String,

    @field:Json(name = "favicon")
    val favicon: String

)
