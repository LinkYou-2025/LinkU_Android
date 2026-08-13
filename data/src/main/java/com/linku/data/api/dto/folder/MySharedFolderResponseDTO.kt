package com.linku.data.api.dto.folder

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** 내가 소유하면서 다른 멤버와 실제로 공유 중인 폴더 응답입니다. */
@JsonClass(generateAdapter = true)
data class MySharedFolderResponseDTO(
    @field:Json(name = "folderId")
    val folderId: Long,

    @field:Json(name = "folderName")
    val folderName: String,

    @field:Json(name = "memberCount")
    val memberCount: Int,
)
