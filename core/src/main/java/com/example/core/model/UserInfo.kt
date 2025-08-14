package com.example.core.model

data class UserInfo(
    val nickname: String,
    val email: String,
    val gender: String,  // "MALE" | "FEMALE"
    val jobId: Long,
    val jobName: String,
    val myLinku: Long,
    val myFolder: Long,
    val myAiLinku: Long
)
