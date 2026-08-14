package com.linku.data.api.dto.search

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LinkuSearchResponseDTO(
    @field:Json(name = "items")
    val items: List<LinkuSearchItemResponseDTO>? = null,
    @field:Json(name = "nextCursor")
    val nextCursor: Long? = null,
    @field:Json(name = "hasNext")
    val hasNext: Boolean? = null,
)

@JsonClass(generateAdapter = true)
data class LinkuSearchItemResponseDTO(
    @field:Json(name = "userLinkuId")
    val userLinkuId: Long? = null,
    @field:Json(name = "title")
    val title: String? = null,
    @field:Json(name = "linkuImageUrl")
    val linkuImageUrl: String? = null,
    @field:Json(name = "tags")
    val tags: List<String>? = null,
    @field:Json(name = "domainImageUrl")
    val domainImageUrl: String? = null,
    @field:Json(name = "domainName")
    val domainName: String? = null,
)
