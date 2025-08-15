package com.example.core.model

// com.example.data.api.dto.server.ShareFolderResponseDTO -> 구조를 공유
data class SharedFolderSimpleInfo(
    val folderId: Long,
    val userId: Long,
    val permission: FolderPermission,
    val sharedAt: String
)
