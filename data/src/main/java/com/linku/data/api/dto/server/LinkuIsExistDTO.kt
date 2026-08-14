package com.linku.data.api.dto.server

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class LinkuIsExistDTO(
    @field:Json(name = "isExist")
    val isExist: Boolean? = null,

    @field:Json(name = "userId")
    val userId: Long? = null,

    @field:Json(name = "title")
    val title: String? = null,

    @field:Json(name = "memo")
    val memo: String? = null,

    @field:Json(name = "emotionId")
    val emotionId: Long? = null,

    @field:Json(name = "createdAt")
    val createdAt: OffsetDateTime? = null,

    @field:Json(name = "updatedAt")
    val updatedAt: OffsetDateTime? = null,

)
