package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class AiArticleResultDTO(

    @Json(name = "id")
    val id: Long,

    @Json(name = "linkuId")
    val linkuId: Long,

    @Json(name = "situationId")
    val situationId: Long,

    @Json(name = "situationName")
    val situationName: String,

    @Json(name = "emotionId")
    val emotionId: Long,

    @Json(name = "emotionName")
    val emotionName: String,

    @Json(name = "title")
    val title: String,

    @Json(name = "aiFeelingName")
    val aiFeelingName: String,

    @Json(name = "aiFeelingId")
    val aiFeelingId: Long,

    @Json(name = "aiCategoryId")
    val aiCategoryId: Long,

    @Json(name = "categoryName")
    val categoryName: String,

    @Json(name = "summary")
    val summary: String,

    @Json(name = "imgUrl")
    val imgUrl: String,

    @Json(name = "memo")
    val memo: String,

    @Json(name = "keyword")
    val keyword: String

)