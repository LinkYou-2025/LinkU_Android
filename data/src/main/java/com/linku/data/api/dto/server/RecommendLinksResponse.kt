package com.linku.data.api.dto.server


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecommendLinksResponse(
    @field:Json(name = "isSuccess") val isSuccess: Boolean,
    @field:Json(name = "code") val code: String,
    @field:Json(name = "message") val message: String,
    @field:Json(name = "result") val result: List<RecommendLinkItemDto>
)
@JsonClass(generateAdapter = true)
data class RecommendLinkItemDto(
    @field:Json(name = "userLinkuId") val userLinkuId: Long?, // 내부 추천이면 값 존재, 외부는 null
    @field:Json(name = "title") val title: String?,
    @field:Json(name = "url") val url: String?,
    @field:Json(name = "imageUrl") val imageUrl: String?,
    @field:Json(name = "domain") val domain: String?,
    @field:Json(name = "domainImageUrl") val domainImageUrl: String?,
    @field:Json(name = "categories") val categories: List<String>?
)

@JsonClass(generateAdapter = true)
data class RecommendLink(
    val userLinkuId: Long?,          // null 허용
    val title: String,
    val url: String,
    val imageUrl: String?,           // null 허용
    val domain: String?,             // "invalid"/"unknown" 대비
    val domainImageUrl: String?,     // null 허용
    val categories: List<String>?    // null 허용
)
