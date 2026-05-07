package com.linku.data.api.dto.server

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurationDetailResponse(

    @field:Json(name = "curationId")
    val curationId: Long,

    @field:Json(name = "month")
    val month: String,

    @field:Json(name = "topTags")
    val topTags: List<String>,

    @field:Json(name = "headerMent")
    val headerMent: String,

    @field:Json(name = "footerMent")
    val footerMent: String

)