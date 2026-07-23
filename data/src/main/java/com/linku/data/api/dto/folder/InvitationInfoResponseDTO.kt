package com.linku.data.api.dto.folder

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InvitationInfoResponseDTO(
    @field:Json(name = "folderName")
    val folderName: String,

    @field:Json(name = "ownerName")
    val ownerName: String
)
