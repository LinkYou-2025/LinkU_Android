package com.linku.data.api.dto.server.curation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// 필요 없는 필드는 그냥 무시. 심플 이즈 베스트
@JsonClass(generateAdapter = true)
data class UnreadLinkDTO(
    @field:Json(name = "userLinkuId")
    val userLinkuId: Long?,

    @field:Json(name = "title")
    val title: String,

    @field:Json(name = "domain")
    val domain: String,

    @field:Json(name = "domainImageUrl")
    val domainImageUrl: String,

    @field:Json(name = "linkuImageUrl")
    val linkuImageUrl: String?
)
