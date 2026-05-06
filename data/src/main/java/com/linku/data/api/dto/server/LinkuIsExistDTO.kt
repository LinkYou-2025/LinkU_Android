package com.linku.data.api.dto.server

import com.squareup.moshi.Json
import java.time.OffsetDateTime

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LinkuIsExistDTO(

    @field:Json(name = "userId")
    val userId: Long,

    @field:Json(name = "linkuId")
    val linkuId: Long,

    @field:Json(name = "title")
    val title: String,

    @field:Json(name = "memo")
    val memo: String,

    @field:Json(name = "emotionId")
    val emotionId: Long,

    @field:Json(name = "createdAt")
    val createdAt: OffsetDateTime,

    @field:Json(name = "updatedAt")
    val updatedAt: OffsetDateTime,

    @field:Json(name = "exist")
    val exist: Boolean

)