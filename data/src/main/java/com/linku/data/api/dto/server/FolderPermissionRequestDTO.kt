package com.linku.data.api.dto.server

import com.linku.core.model.FolderPermission
import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FolderPermissionRequestDTO(

    @field:Json(name = "permission")
    val permission: FolderPermission

)