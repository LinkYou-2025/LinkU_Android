package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class ApiResponseJoinResultDTO (

    @Json(name = "isSuccess")
    val isSuccess: Boolean? = null,

    @Json(name = "code")
    val code: String? = null,

    @Json(name = "message")
    val message: String? = null,

    @Json(name = "result")
    val result: JoinResultDTO? = null

)