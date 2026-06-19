package com.linku.data.api.dto.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NicknameResponseDTO(
    @field:Json(name = "nickname")
    val nickname: String
)
