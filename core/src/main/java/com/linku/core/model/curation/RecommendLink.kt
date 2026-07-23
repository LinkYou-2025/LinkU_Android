package com.linku.core.model.curation

data class RecommendLink(
    val userLinkuId: Long,
    val isExternal: Boolean = false, // 외부 링크인지 여부. DTO에는 아직 추가 전
    val title: String,
    val url: String, // 외부 리다이렉트 시 사용
    val imageUrl: String,
    val domain: String,
    val domainImageUrl: String,
    val categories: List<String>
)
