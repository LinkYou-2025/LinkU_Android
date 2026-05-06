package com.linku.data.api.dto.user

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeleteUserRequestDTO(

    @field:Json(name = "reason")
    val reason: String

)