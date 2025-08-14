package com.example.core.model

import java.time.OffsetDateTime

// com.example.data.api.dto.server.FolderListResponseDTO -> 구조를 공유
data class FolderSimpleInfo(
    val folderId: Long,
    val folderName: String,
    val parentFolderId: Long,
    val isBookmarked: Boolean,
    val isSharing: String? = null,
)