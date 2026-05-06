package com.linku.data.api.dto.user

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateUserProfileRequestDTO(

    @field:Json(name = "nickname")
    val nickname: String,

    @field:Json(name = "jobId")
    val jobId: Long,

    @field:Json(name = "purposes")
    val purposes: List<String>,

    @field:Json(name = "interests")
    val interests: List<String>

)