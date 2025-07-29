package com.example.data.api.dto.server

import com.example.core.model.FolderPermission
import com.squareup.moshi.Json

data class FolderPermissionRequestDTO(

    @Json(name = "permission")
    val permission: FolderPermission

)