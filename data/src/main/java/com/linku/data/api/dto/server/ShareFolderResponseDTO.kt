package com.linku.data.api.dto.server

import com.squareup.moshi.Json

// com.linku.core.model.SharedFolderSimpleInfo -> 구조를 공유
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