package com.example.core.model

data class LoginResult(
    val userId: Int,
    val token: String,
    val status: String,
    val inactiveDate: String? = null
)
/*
도메인 모델링. 뷰 모델에서 사용하기 쉬움!*/