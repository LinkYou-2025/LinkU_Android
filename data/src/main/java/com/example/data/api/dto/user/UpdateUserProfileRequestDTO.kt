package com.example.data.api.dto.user

import com.squareup.moshi.Json

data class UpdateUserProfileRequestDTO(

    @Json(name = "nickname")
    val nickname: String,

    @Json(name = "jobId")
    val jobId: Long,

    @Json(name = "purposes")
    val purposes: List<String>,

    @Json(name = "interests")
    val interests: List<String>

)