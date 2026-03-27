package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class FolderCreateRequestDTO(

    @Json(name = "folderName")
    val folderName: String

)