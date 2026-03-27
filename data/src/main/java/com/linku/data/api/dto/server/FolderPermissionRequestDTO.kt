package com.linku.data.api.dto.server

import com.linku.core.model.FolderPermission
import com.squareup.moshi.Json

data class FolderPermissionRequestDTO(

    @Json(name = "permission")
    val permission: FolderPermission

)