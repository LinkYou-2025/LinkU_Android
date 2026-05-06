package com.linku.data.api.dto.server

import com.squareup.moshi.Json
import java.time.OffsetDateTime

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LinkuResultDTO(

    @field:Json(name = "userId")
    val userId: Long,

    @field:Json(name = "linkuId")
    val linkuId: Long,

    @field:Json(name = "linkuFolderId")
    val linkuFolderId: Long?,

    @field:Json(name = "categoryId")
    val categoryId: Long,

    @field:Json(name = "linku")
    val linku: String,

    @field:Json(name = "memo")
    val memo: String?,

    @field:Json(name = "emotionId")
    val emotionId: Long?,

    @field:Json(name = "domain")
    val domain: String?,

    @field:Json(name = "title")
    val title: String,

    @field:Json(name = "domainImageUrl")
    val domainImageUrl: String?,

    @field:Json(name = "linkuImageUrl")
    val linkuImageUrl: String?,

    @field:Json(name = "aiArticleExists")
    val aiArticleExists: Boolean,

    @field:Json(name = "keyword")
    val keyword: String?,

    @field:Json(name = "summary")
    val summary: String?,

    @field:Json(name = "createdAt")
    val createdAt: OffsetDateTime,

    @field:Json(name = "updatedAt")
    val updatedAt: OffsetDateTime

)
