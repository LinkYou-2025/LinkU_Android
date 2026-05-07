package com.linku.data.api.dto.server

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetDetailLinkDTO(
    @field:Json(name = "userLinkuId")
    val userLinkuId: Long,
)
