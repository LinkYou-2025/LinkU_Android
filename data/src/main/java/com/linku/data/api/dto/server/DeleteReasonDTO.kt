package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class DeleteReasonDTO(

    @Json(name = "reason")
    val reason: String

)