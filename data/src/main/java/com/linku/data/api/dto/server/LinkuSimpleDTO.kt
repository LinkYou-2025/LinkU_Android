package com.linku.data.api.dto.server

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LinkuSimpleDTO(

    @field:Json(name = "linkuId")
    val linkuId: Long,

    @field:Json(name = "categoryId")
    val categoryId: Long,

    @field:Json(name = "memo")
    val memo: String?,

    @field:Json(name = "emotionId")
    val emotionId: Long,

    @field:Json(name = "title")
    val title: String,

    @field:Json(name = "domain")
    val domain: String?,

    @field:Json(name = "domainImageUrl")
    val domainImageUrl: String?,

    @field:Json(name = "linkuImageUrl")
    val linkuImageUrl: String?,

    @field:Json(name = "aiArticleExists")
    val aiArticleExists: Boolean
)