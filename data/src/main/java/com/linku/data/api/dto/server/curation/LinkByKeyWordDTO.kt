package com.linku.data.api.dto.server.curation

import com.squareup.moshi.Json

data class LinkByKeyWordDTO(
    @field:Json(name = "userLinkuId") val userLinkuId: Long?,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "url") val url: String,
    @field:Json(name = "imageUrl") val imageUrl: String?,
    @field:Json(name = "domain") val domain: String,
    @field:Json(name = "domainImageUrl") val domainImageUrl: String?,
    @field:Json(name = "categories") val categories: List<String>,
    @field:Json(name = "aiArticleExists") val aiArticleExists: Boolean
)
