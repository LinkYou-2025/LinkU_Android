package com.example.data.api.dto.server

import com.squareup.moshi.Json
import java.time.OffsetDateTime

data class JoinResultDTO(

    @Json(name = "userId")
    val userId: Long,

    @Json(name = "createdAt")
    val createdAt: OffsetDateTime

)