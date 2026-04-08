package com.linku.data.api.dto.user

import com.squareup.moshi.Json

data class DeleteUserRequestDTO(

    @Json(name = "reason")
    val reason: String

)