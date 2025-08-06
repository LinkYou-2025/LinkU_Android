package com.example.core.model

data class SharedFolderSimpleInfo(
    val folderId: Long,
    val userId: Long,
    val permission: FolderPermission,
    val sharedAt: String
)
