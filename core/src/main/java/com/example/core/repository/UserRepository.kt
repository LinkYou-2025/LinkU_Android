package com.example.core.repository

import com.example.core.model.LoginResult


interface UserRepository {
    suspend fun checkNickname(nickname: String): Boolean
    suspend fun login(email: String, password: String): LoginResult
    suspend fun signUp(
        nickname: String,
        email: String,
        password: String,
        gender: Int,
        jobId: Int,
        purposeList: List<String>,
        interestList: List<String>
    ): Boolean //api 명세서 : 회원가입 맞춰서 수정!

    suspend fun sendEmailCode(email: String, code: String): Boolean
    suspend fun verifyEmailCode(email: String, code: String): Boolean
    suspend fun deleteUser(reason: String): Boolean

}