package com.example.data.api.dto.user

import com.squareup.moshi.Json

data class SocialProfileRequestDTO(
    @Json(name = "nickName") val nickName: String,
    @Json(name = "gender") val gender: Int,
    @Json(name = "jobId") val jobId: Int,
    @Json(name = "purposeList") val purposeList: List<String>,
    @Json(name = "interestList") val interestList: List<String>
)

