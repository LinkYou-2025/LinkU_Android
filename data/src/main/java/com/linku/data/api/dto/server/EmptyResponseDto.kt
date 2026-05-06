package com.linku.data.api.dto.server

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmptyResponseDto(val dummy: String = "")