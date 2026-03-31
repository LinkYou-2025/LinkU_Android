package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class GetDetailLinkDTO(
    @Json(name = "userLinkuId")
    val userLinkuId: Long,
)
