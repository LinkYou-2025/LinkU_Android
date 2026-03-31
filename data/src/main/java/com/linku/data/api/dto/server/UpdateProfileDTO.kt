package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class UpdateProfileDTO(

    @Json(name = "nickname")
    val nickname: String,

    @Json(name = "jobId")
    val jobId: Long,

    @Json(name = "purposes")
    val purposes: List<String>,

    @Json(name = "interests")
    val interests: List<String>

)