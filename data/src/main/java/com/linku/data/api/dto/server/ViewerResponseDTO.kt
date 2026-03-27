package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class ViewerResponseDTO(

    @Json(name = "userId")
    val userId: Long,

    @Json(name = "userName")
    val userName: String,

    @Json(name = "permission")
    val permission: String

)