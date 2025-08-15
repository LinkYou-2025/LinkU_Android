package com.example.data.api.dto.server

import com.squareup.moshi.Json

// com.example.core.model.SharedFolderSimpleInfo -> 구조를 공유
data class ShareFolderResponseDTO(

    @Json(name = "folderId")
    val folderId: Long,

    @Json(name = "userId")
    val userId: Long,

    @Json(name = "permission")
    val permission: String,

    @Json(name = "sharedAt")
    val sharedAt: String

)