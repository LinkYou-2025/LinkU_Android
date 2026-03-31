package com.linku.data.api.dto.server

data class SocialProfileRequestDTO(
    val nickName: String,
    val gender: Int,
    val jobId: Int,
    val purposeList: List<String>,
    val interestList: List<String>
)