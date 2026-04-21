package com.linku.data.api.dto.server

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AiArticleResultDTO(

    @field:Json(name = "id")
    val id: Long,

    @field:Json(name = "linkuId")
    val linkuId: Long,

    @field:Json(name = "situationId")
    val situationId: Long,

    @field:Json(name = "situationName")
    val situationName: String,

    @field:Json(name = "emotionId")
    val emotionId: Long,

    @field:Json(name = "emotionName")
    val emotionName: String,

    @field:Json(name = "title")
    val title: String,

    @field:Json(name = "aiFeelingName")
    val aiFeelingName: String,

    @field:Json(name = "aiFeelingId")
    val aiFeelingId: Long,

    @field:Json(name = "aiCategoryId")
    val aiCategoryId: Long,

    @field:Json(name = "categoryName")
    val categoryName: String,

    @field:Json(name = "summary")
    val summary: String?,

    @field:Json(name = "imgUrl")
    val imgUrl: String?,

    @field:Json(name = "memo")
    val memo: String?,

    @field:Json(name = "keyword")
    val keyword: String?

)