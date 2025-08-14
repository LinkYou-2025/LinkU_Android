package com.example.data.api.dto.server


import com.squareup.moshi.Json

data class RecommendLinksResponse(
    @Json(name = "isSuccess") val isSuccess: Boolean,
    @Json(name = "code") val code: String,
    @Json(name = "message") val message: String,
    @Json(name = "result") val result: List<RecommendLinkItemDto>
)

data class RecommendLinkItemDto(
    @Json(name = "userLinkuId") val userLinkuId: Long?, // 내부 추천이면 값 존재, 외부는 null
    @Json(name = "title") val title: String?,
    @Json(name = "url") val url: String?,
    @Json(name = "imageUrl") val imageUrl: String?,
    @Json(name = "domain") val domain: String?,
    @Json(name = "domainImageUrl") val domainImageUrl: String?,
    @Json(name = "categories") val categories: List<String>?
)

data class RecommendLink(
    val userLinkuId: Long?,          // null 허용
    val title: String,
    val url: String,
    val imageUrl: String?,           // null 허용
    val domain: String?,             // "invalid"/"unknown" 대비
    val domainImageUrl: String?,     // null 허용
    val categories: List<String>?    // null 허용
)
