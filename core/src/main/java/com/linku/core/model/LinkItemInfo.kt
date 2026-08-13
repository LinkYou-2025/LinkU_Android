package com.linku.core.model

import java.time.OffsetDateTime

/**
 * 폴더 목록에 표시하는 사용자 저장 링크입니다.
 *
 * [userLinkuId]는 상세 조회, 폴더 이동, 삭제에 공통으로 사용하는 저장 링크 식별자입니다.
 */
data class LinkItemInfo(
    val userLinkuId: Long,
    val parentFolderId: Long,
    val title: String,
    val url: String,
    val tags: List<String> = emptyList(),
    val linkuImageUrl: String?,
    val createdAt: OffsetDateTime?,
)
