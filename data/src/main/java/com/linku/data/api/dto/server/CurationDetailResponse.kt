package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class CurationDetailResponse(

    @Json(name = "curationId")
    val curationId: Long,

    @Json(name = "month")
    val month: String,

    @Json(name = "topTags")
    val topTags: List<String>,

    @Json(name = "headerMent")
    val headerMent: String,

    @Json(name = "footerMent")
    val footerMent: String

)