package com.linku.core.model

data class RecommendationPage(
    val items: List<LinkSimpleInfo>,
    val nextCursor: String?,
    val hasNext: Boolean,
)