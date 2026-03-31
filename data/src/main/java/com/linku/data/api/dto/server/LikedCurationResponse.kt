package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class LikedCurationResponse(

    @Json(name = "curationId")
    val curationId: Long,

    @Json(name = "month")
    val month: String,

    @Json(name = "thumbnailUrl")
    val thumbnailUrl: String

)