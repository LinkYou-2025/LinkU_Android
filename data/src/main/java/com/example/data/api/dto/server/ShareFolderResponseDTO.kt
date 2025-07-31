package com.example.data.api.dto.server

import com.example.core.model.FolderPermission
import com.squareup.moshi.Json

data class ShareFolderResponseDTO(

    @Json(name = "folderId")
    val folderId: Long,

    @Json(name = "userId")
    val userId: Long,

    @Json(name = "permission")
    val permission: FolderPermission,

    @Json(name = "sharedAt")
    val sharedAt: String

)