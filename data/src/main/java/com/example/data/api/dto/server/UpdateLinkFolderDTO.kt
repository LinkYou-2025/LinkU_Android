package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class UpdateLinkFolderDTO(

    @Json(name = "folderId")
    val folderId: Long,
)
