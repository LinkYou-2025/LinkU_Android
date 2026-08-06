package com.linku.data.api.dto.server

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AiArticleResultDTO(

    @field:Json(name = "id")
    val id: Long,

    @field:Json(name = "linkuId")
    val linkuId: Long,

    @field:Json(name = "emotionId")
    val emotionId: Long,

    @field:Json(name = "emotionName")
    val emotionName: String,

    @field:Json(name = "categoryName")
    val categoryName: String,

    @field:Json(name = "summary")
    val summary: String?,

    @field:Json(name = "imgUrl")
    val imgUrl: String?,

    @field:Json(name = "memo")
    val memo: String?,

    @field:Json(name = "tags")
    val tags: String?,

    @field:Json(name = "title")
    val title: String

)