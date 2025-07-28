package com.example.core.api


data class SignUpResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: SignUpResult?
)

data class SignUpResult(
    val userId: Int,
    val createdAt: String
)
