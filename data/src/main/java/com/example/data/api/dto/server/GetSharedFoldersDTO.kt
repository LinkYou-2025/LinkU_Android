package com.example.data.api.dto.server
import com.squareup.moshi.Json

data class FolderOwnerDTO(
    @Json(name = "userId")
    val userId: Long,

    @Json(name = "nickname")
    val nickname: String
)

// com.example.core.model.SharedFolderInfo -> 구조를 공유
data class GetSharedFoldersDTO(

    @Json(name = "folderId")
    val folderId: Long,

    @Json(name = "folderName")
    val folderName: String,

    @Json(name = "categoryId")
    val categoryId: Long,

    @Json(name = "owner")
    val owner: FolderOwnerDTO,

    @Json(name = "permission")
    val permission: String
)
