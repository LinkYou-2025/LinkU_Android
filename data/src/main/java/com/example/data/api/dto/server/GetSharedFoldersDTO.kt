package com.example.data.api.dto.server

import com.squareup.moshi.Json

// 폴더 정보 DTO
data class SharedFolderDTO(
    @Json(name = "folderId")
    val folderId: Long,

    @Json(name = "folderName")
    val folderName: String,

    @Json(name = "categoryId")
    val categoryId: Long
)

// 사용자 + 해당 사용자가 공유한 폴더 목록 DTO
data class GetSharedFoldersDTO(
    @Json(name = "userId")
    val userId: Long,

    @Json(name = "nickname")
    val nickname: String,

    @Json(name = "folders")
    val folders: List<SharedFolderDTO>
)
