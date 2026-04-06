package com.linku.core.model

// com.linku.data.api.dto.server.GetSharedFoldersDTO -> 구조를 공유
data class SharedFolderInfo(
    val userId: Long,
    val nickname: String,
    val folders: List<FolderSimpleInfo>
)