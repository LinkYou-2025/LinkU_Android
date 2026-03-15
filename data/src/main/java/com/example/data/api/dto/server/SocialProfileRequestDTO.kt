package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class SocialProfileRequestDTO(
    val nickName: String,
    val gender: Int,
    val jobId: Int,
    val purposeList: List<String>,
    val interestList: List<String>
)

data class SocialCompleteResultDTO(
    @Json(name = "userId") val userId: Long,
    @Json(name = "createdAt") val createdAt: String
)