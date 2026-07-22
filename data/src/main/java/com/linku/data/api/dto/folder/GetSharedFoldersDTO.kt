package com.linku.data.api.dto.folder

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetSharedFoldersDTO(
    @field:Json(name = "userId")
    val userId: Long,

    @field:Json(name = "nickname")
    val nickname: String,

    @field:Json(name = "folders")
    val folders: List<FolderTreeResponseDTO>
)
