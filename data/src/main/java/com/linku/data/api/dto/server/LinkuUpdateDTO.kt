package com.linku.data.api.dto.server

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LinkuUpdateDTO(

    @field:Json(name = "folderId")
    val folderId: Long? = null,

    @field:Json(name = "categoryId")
    val categoryId: Long,

    @field:Json(name = "linku")
    val linku: String,

    @field:Json(name = "memo")
    val memo: String,

    @field:Json(name = "emotionId")
    val emotionId: Long,

    @field:Json(name = "situationId")
    val situationId: Long,

    @field:Json(name = "domainId")
    val domainId: Long,

    @field:Json(name = "title")
    val title: String

)