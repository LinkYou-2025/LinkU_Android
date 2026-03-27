package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class ApiResponseListLinkuSimpleDTO(

    @Json(name = "isSuccess")
    val isSuccess: Boolean,

    @Json(name = "code")
    val code: String,

    @Json(name = "message")
    val message: String,

    @Json(name = "result")
    val result: List<LinkuSimpleDTO>

)