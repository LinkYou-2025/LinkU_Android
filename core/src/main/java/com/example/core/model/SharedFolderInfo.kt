package com.example.core.model

data class FolderOwner(
    val userId: Long,
    val nickname: String
)

// com.example.data.api.dto.server.GetSharedFoldersDTO -> 구조를 공유
data class SharedFolderInfo(
    val folderId: Long,
    val folderName: String,
    val categoryId: Long,
    val owner: FolderOwner,
    val permission: FolderPermission
)
