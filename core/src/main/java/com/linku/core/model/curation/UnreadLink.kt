package com.linku.core.model.curation

data class UnreadLink(
    val title: String,
    val domain: String,
    val categories: List<String> = emptyList(), // 아직 백엔드 DTO에는 추가 전
    val domainImageUrl: String,
    val linkuImageUrl: String?
)
