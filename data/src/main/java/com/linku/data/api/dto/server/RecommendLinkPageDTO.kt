package com.linku.data.api.dto.server

data class RecommendLinkPageDTO(
    val items: List<LinkuSimpleDTO>,
    val nextCursor: String?,
    val hasNext: Boolean,
)