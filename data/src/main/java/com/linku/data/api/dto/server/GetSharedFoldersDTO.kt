package com.linku.data.api.dto.server

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// 폴더 정보 DTO
@JsonClass(generateAdapter = true)
data class SharedFolderDTO(
    @field:Json(name = "folderId")
    val folderId: Long,

    @field:Json(name = "folderName")
    val folderName: String,

    @field:Json(name = "categoryId")
    val categoryId: Long
)

@JsonClass(generateAdapter = true)
data class GetSharedFoldersDTO(
    @field:Json(name = "userId")
    val userId: Long,

    @field:Json(name = "nickname")
    val nickname: String,

    @field:Json(name = "folders")
    val folders: List<SharedFolderDTO>
)
