package com.linku.core.model

// com.linku.data.api.dto.server.ShareFolderResponseDTO -> 구조를 공유
data class SharedFolderSimpleInfo(
    val folderId: Long,
    val userId: Long,
    val permission: FolderPermission,
    val sharedAt: String
)
