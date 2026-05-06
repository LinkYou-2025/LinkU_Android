package com.linku.data.api.dto.server

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuickSearchResult(

    @field:Json(name = "linkuId")
    val linkuId: Long,

    @field:Json(name = "title")
    val title: String,

    @field:Json(name = "domainImageUrl")
    val domainImageUrl: String,

    @field:Json(name = "linkUrl")
    val linkUrl: String

)
