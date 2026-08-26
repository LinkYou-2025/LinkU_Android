package com.linku.data.api.dto.server

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecommendLinkPageDTO(
    val items: List<LinkuSimpleDTO>,
    val nextCursor: String?,
    val hasNext: Boolean,
)
