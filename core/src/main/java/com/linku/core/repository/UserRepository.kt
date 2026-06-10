package com.linku.core.repository

import com.linku.core.model.UserInfo


interface UserRepository {

    suspend fun deleteUser(reason: String): Boolean

    // 닉네임 전용 메서드 추가
    suspend fun getNickname(userId: Long): String?

    // 마이페이지 조회
    suspend fun getUserInfo(userId: Long): Result<UserInfo>

    // 마이페이지 계정 수정
    suspend fun updateUserInfo(
        nickname: String,
        jobId: Long,
        purposes: List<String>,
        interests: List<String>
    ): Result<Unit>

    // 로그아웃
    suspend fun logout()

}