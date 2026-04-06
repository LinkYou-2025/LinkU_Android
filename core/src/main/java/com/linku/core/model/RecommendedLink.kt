package com.linku.core.model


data class RecommendedLink(
    val isInternal: Boolean,
    val userLinkuId: Long?,
    val title: String,
    val url: String,
    val imageUrl: String?,
    val domain: String?,
    val domainImageUrl: String?,
    val categories: List<String>?
)