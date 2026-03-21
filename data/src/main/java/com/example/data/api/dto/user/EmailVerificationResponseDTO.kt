package com.example.data.api.dto.user

import com.squareup.moshi.Json

data class EmailVerificationResponseDTO(

    @Json(name = "success")
    val success: Boolean

)