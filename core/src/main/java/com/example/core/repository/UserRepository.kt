package com.example.core.repository

import com.example.core.model.LoginResult
import com.example.core.model.UserInfo


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

    // 유저 id 가져오기
    suspend fun getUserInfo(userId: Long): String?

    //유저 비밀번호 재설정
    suspend fun requestTempPassword(email: String): Boolean

    // 마이페이지 조회
    suspend fun getUserInfo(userId: Long): UserInfo

    // 로그아웃
    suspend fun logout()
}