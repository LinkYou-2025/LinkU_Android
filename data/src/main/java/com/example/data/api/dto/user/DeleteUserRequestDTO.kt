package com.example.data.api.dto.user

import com.squareup.moshi.Json

data class DeleteUserRequestDTO(

    @Json(name = "reason")
    val reason: String

)