package com.linku.core.model.search

data class RecentQuery(
    val text: String,        // 검색어
    val timestamp: Long      // 저장 시각 (epoch millis)
)
