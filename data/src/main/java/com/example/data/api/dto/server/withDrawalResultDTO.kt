package com.example.data.api.dto.server

import com.squareup.moshi.Json
import java.time.OffsetDateTime

data class withDrawalResultDTO(

    @Json(name = "userId")
    val userId: Long,

    @Json(name = "nickname")
    val nickname: String,

    @Json(name = "createdAt")
    val createdAt: OffsetDateTime,

    @Json(name = "status")
    val status: String,

    @Json(name = "inactiveDate")
    val inactiveDate: OffsetDateTime

)
