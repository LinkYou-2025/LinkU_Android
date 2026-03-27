package com.linku.core.repository

import com.linku.core.model.LoginResult
import com.linku.core.model.TokenReissueResult
import com.linku.core.model.UserInfo
import com.linku.core.model.auth.Gender
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Job
import com.linku.core.model.auth.Purpose
import com.linku.core.datastore.session.LoginSessionStore
import com.linku.core.model.auth.SignUpEmailResult
import kotlinx.coroutines.flow.Flow


interface UserRepository {

    suspend fun deleteUser(reason: String): Boolean

    // 닉네임 전용 메서드 추가
    suspend fun getNickname(userId: Long): String?


    // 마이페이지 조회
    suspend fun getUserInfo(userId: Long): UserInfo

    suspend fun refreshUserInfo(userId: Long)
    // 마이페이지 계정 수정
    suspend fun updateUserInfo(
        nickname: String,
        jobId: Long,
        purposes: List<String>,
        interests: List<String>
    ): Boolean

    // 로그아웃
    suspend fun logout()


    suspend fun updateUserProfile(
        nickname: String,
        jobId: Long,
        jobName: String,
        purposes: List<String>,
        interests: List<String>
    )


}