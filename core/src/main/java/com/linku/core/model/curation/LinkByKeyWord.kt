package com.linku.core.model.curation

data class LinkByKeyWord(
    val userLinkuId: Long?,
    val title: String,
    val url: String,
    val imageUrl: String?,
    val domain: String,
    val domainImageUrl: String?,
    val categories: List<String>,
    val aiArticleExists: Boolean
)
