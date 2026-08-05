package com.linku.core.model.search

data class LinkuSearchInfo(
    val userLinkuId: Long?,
    val linkuId: Long?,
    val title: String?,
    val linkuImageUrl: String?,
    val tags: List<String>,
    val domainImageUrl: String?,
    val domainName: String?,
)
