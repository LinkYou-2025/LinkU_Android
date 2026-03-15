package com.example.core.model

import com.example.core.model.auth.Gender


data class UserInfo(
    val nickname: String,
    val email: String,
    val gender: String,  // "MALE" | "FEMALE"
    val jobId: Long,
    val jobName: String,
    val myLinku: Long,
    val myFolder: Long,
    val myAiLinku: Long,
    val purposes: List<String> = emptyList(),
    val interests: List<String> = emptyList()
)
