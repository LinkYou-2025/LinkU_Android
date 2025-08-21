package com.example.data.api.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BaseEmptyResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String
)