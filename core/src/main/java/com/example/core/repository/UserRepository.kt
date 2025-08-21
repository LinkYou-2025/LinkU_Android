package com.example.core.repository

import com.example.core.model.LoginResult
import com.example.core.model.UserInfo


interface UserRepository {
    suspend fun checkNickname(nickname: String): Boolean
    //suspend fun getNickname(userId: Long): String?
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

    // 닉네임 전용 메서드 추가
    suspend fun getNickname(userId: Long): String?

    // 유저 id 가져오기
    //suspend fun getUserInfo(userId: Long): String?

    //유저 비밀번호 재설정
    suspend fun requestTempPassword(email: String): Boolean

    // 마이페이지 조회
    suspend fun getUserInfo(userId: Long): UserInfo

    // 마이페이지 계정 수정
    suspend fun updateUserInfo(
        nickname: String,
        jobId: Long,
        purposes: List<String>,
        interests: List<String>
    ): Boolean

    // 로그아웃
    suspend fun logout()
}