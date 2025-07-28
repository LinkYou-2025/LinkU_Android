package com.example.core.api


data class SignUpRequest(
    val nickName: String,
    val email: String,
    val password: String,
    val gender: Int,
    val jobId: Int,
    val purposeList: List<String>,
    val interestList: List<String>
)