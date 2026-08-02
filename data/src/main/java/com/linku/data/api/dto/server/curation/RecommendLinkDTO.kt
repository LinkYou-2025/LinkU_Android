package com.linku.data.api.dto.server.curation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecommendLinkDTO(
    @field:Json(name = "userLinkuId")
    val userLinkuId: Long,

    @field:Json(name = "title")
    val title: String,

    @field:Json(name = "url")
    val url: String, // 외부 리다이렉트 시 사용

    @field:Json(name = "imageUrl")
    val imageUrl: String,

    @field:Json(name = "domain")
    val domain: String,

    @field:Json(name = "domainImageUrl")
    val domainImageUrl: String,

    @field:Json(name = "categories")
    val categories: List<String>,

    @field:Json(name = "type")
    val type: String
)
