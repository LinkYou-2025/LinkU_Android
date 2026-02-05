package com.example.data.api.dto.server

data class SocialProfileRequestDTO(
    val nickName: String,
    val gender: Int,
    val jobId: Int,
    val purposeList: List<String>,
    val interestList: List<String>
)