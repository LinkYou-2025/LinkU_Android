package com.linku.core.model

import java.time.OffsetDateTime

data class LinkItemInfo(
    val linkuId: Long,
    val parentFolderId: Long,
    val title: String,
    val url: String,
    val tags: List<String> = emptyList(),
    val linkuImageUrl: String?,
    val createdAt: OffsetDateTime?,
)
