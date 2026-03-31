package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class LinkDetailDTO(

    @Json(name = "title")
    val title: String,

    @Json(name = "summary")
    val summary: String,

    @Json(name = "source")
    val source: String,

    @Json(name = "url")
    val url: String,

    @Json(name = "favicon")
    val favicon: String

)
