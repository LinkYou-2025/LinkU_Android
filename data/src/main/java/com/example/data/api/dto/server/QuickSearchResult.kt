package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class QuickSearchResult(

    @Json(name = "searchKeyword")
    val searchKeyword: String,

    @Json(name = "recentKeywords")
    val recentKeywords: List<String>,

    @Json(name = "links")
    val links: List<LinkDetailDTO>

)
