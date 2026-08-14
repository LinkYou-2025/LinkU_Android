package com.linku.core.model.search

/** 검색 결과에 표시하는 저장 링크이며 [userLinkuId]가 상세 화면 이동의 식별자입니다. */
data class LinkuSearchInfo(
    val userLinkuId: Long,
    val title: String?,
    val linkuImageUrl: String?,
    val tags: List<String>,
    val domainImageUrl: String?,
    val domainName: String?,
)
