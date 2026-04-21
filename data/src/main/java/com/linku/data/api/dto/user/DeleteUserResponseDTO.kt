package com.linku.data.api.dto.user

import com.squareup.moshi.Json
import java.time.OffsetDateTime

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeleteUserResponseDTO(

    @field:Json(name = "userId")
    val userId: Long,

    @field:Json(name = "nickname")
    val nickname: String,

    @field:Json(name = "createdAt")
    val createdAt: OffsetDateTime,

    @field:Json(name = "status")
    val status: String,

    @field:Json(name = "inactiveDate")
    val inactiveDate: OffsetDateTime

)