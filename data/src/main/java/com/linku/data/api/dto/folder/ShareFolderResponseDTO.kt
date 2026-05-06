package com.linku.data.api.dto.folder

import com.squareup.moshi.Json

// com.linku.core.model.SharedFolderSimpleInfo -> 구조를 공유
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ShareFolderResponseDTO(

    @field:Json(name = "folderId")
    val folderId: Long,

    @field:Json(name = "userId")
    val userId: Long,

    @field:Json(name = "permission")
    val permission: String,

    @field:Json(name = "sharedAt")
    val sharedAt: String

)