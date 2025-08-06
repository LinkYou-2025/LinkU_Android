package com.example.core.model

import java.time.OffsetDateTime

// com.example.data.api.dto.server.FolderListResponseDTO -> FolderSimpleInfo
data class FolderSimpleInfo(
    val folderId: Long,
    var folderName: String,
    val parentFolderId: Long
)

// 예상 폴더 최종 구조
data class _FolderSimpleInfo(
    val folderId: Long,
    var folderName: String,
    val parentFolderId: Long,
    var colorCode: String,
    var isBookmarked: Boolean = false,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)