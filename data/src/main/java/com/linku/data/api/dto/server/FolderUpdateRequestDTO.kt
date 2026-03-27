package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class FolderUpdateRequestDTO(

    @Json(name = "folderName")
    val folderName: String

)