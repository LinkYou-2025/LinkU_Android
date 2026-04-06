package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class SocialProfileRequestDTO(
    @Json(name = "nickName") val nickName: String,
    @Json(name = "gender") val gender: Int,
    @Json(name = "jobId") val jobId: Int,
    @Json(name = "purposeList") val purposeList: List<String>,
    @Json(name = "interestList") val interestList: List<String>
)

data class SocialCompleteResultDTO(
    @Json(name = "userId") val userId: Long,
    @Json(name = "createdAt") val createdAt: String
)