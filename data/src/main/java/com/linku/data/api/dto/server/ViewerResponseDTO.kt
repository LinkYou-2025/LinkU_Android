package com.linku.data.api.dto.server

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ViewerResponseDTO(

    @field:Json(name = "userId")
    val userId: Long,

    @field:Json(name = "userName")
    val userName: String,

    @field:Json(name = "permission")
    val permission: String

)