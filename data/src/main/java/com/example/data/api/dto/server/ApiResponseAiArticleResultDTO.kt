package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class ApiResponseAiArticleResultDTO(

    @Json(name = "isSuccess")
    val isSuccess: Boolean? = null,

    @Json(name = "code")
    val code: String? = null,

    @Json(name = "message")
    val message: String? = null,

    @Json(name = "result")
    val result: AiArticleResultDTO? = null

)