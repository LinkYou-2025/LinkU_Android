package com.linku.core.model

data class FolderPermissionInfo(
    val userId: Long,
    val userName: String,
    val permission: FolderPermission
)
