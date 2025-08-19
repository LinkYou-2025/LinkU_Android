package com.example.data.api.dto.server

import com.squareup.moshi.Json
import java.time.OffsetDateTime

data class LinkuResultDTO(

    @Json(name = "userId")
    val userId: Long,

    @Json(name = "linkuId")
    val linkuId: Long,

    @Json(name = "linkuFolderId")
    val linkuFolderId: Long,

    @Json(name = "categoryId")
    val categoryId: Long,

    @Json(name = "linku")
    val linku: String,

    @Json(name = "memo")
    val memo: String?,

    @Json(name = "emotionId")
    val emotionId: Long?,

    @Json(name = "domain")
    val domain: String?,

    @Json(name = "title")
    val title: String,

    @Json(name = "domainImageUrl")
    val domainImageUrl: String?,

    @Json(name = "linkuImageUrl")
    val linkuImageUrl: String?,

    @Json(name = "aiArticleExists")
    val aiArticleExists: Boolean,

    @Json(name = "keyword")
    val keyword: String?,

    @Json(name = "summary")
    val summary: String?,

    @Json(name = "createdAt")
    val createdAt: OffsetDateTime,

    @Json(name = "updatedAt")
    val updatedAt: OffsetDateTime

)
