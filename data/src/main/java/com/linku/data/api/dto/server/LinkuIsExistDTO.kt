package com.linku.data.api.dto.server

import com.squareup.moshi.Json
import java.time.OffsetDateTime

data class LinkuIsExistDTO(

    @Json(name = "userId")
    val userId: Long,

    @Json(name = "linkuId")
    val linkuId: Long,

    @Json(name = "title")
    val title: String,

    @Json(name = "memo")
    val memo: String,

    @Json(name = "emotionId")
    val emotionId: Long,

    @Json(name = "createdAt")
    val createdAt: OffsetDateTime,

    @Json(name = "updatedAt")
    val updatedAt: OffsetDateTime,

    @Json(name = "exist")
    val exist: Boolean

)