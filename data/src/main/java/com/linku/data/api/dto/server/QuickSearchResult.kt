package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class QuickSearchResult(

    @Json(name = "linkuId")
    val linkuId: Long,

    @Json(name = "title")
    val title: String,

    @Json(name = "domainImageUrl")
    val domainImageUrl: String,

    @Json(name = "linkUrl")
    val linkUrl: String

)
