package com.linku.data.api.dto.server

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponseListLinkuSimpleDTO(

    @field:Json(name = "isSuccess")
    val isSuccess: Boolean,

    @field:Json(name = "code")
    val code: String,

    @field:Json(name = "message")
    val message: String,

    @field:Json(name = "result")
    val result: List<LinkuSimpleDTO>

)