package com.linku.data.api.dto.auth.signup.email

import com.squareup.moshi.Json

data class EmailVerificationResponseDTO(

    @Json(name = "success")
    val success: Boolean

)