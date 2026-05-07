package com.linku.data.api.dto.server

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponseLinkuResultDTO (

    @field:Json(name = "isSuccess")
    val isSuccess: Boolean? = null,

    @field:Json(name = "code")
    val code: String? = null,

    @field:Json(name = "message")
    val message: String? = null,

    @field:Json(name = "result")
    val result: LinkuResultDTO? = null

)