package com.example.core.repository

import com.example.core.model.LoginResult
import com.example.core.model.TokenReissueResult
import com.example.core.model.UserInfo
import com.example.core.model.auth.Gender
import com.example.core.model.auth.Interest
import com.example.core.model.auth.Job
import com.example.core.model.auth.Purpose
import com.example.core.datastore.session.LoginSessionStore
import kotlinx.coroutines.flow.Flow


interface UserRepository {

    val sessionState: Flow<LoginSessionStore.SessionSnapshot>
    //레포지토리가 세션 상태 플로우 제공하도록 수정함.

    suspend fun checkNickname(nickname: String): Boolean
    //suspend fun getNickname(userId: Long): String?
    suspend fun login(email: String, password: String): LoginResult
    suspend fun signUpWithEmail(
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


    suspend fun reissue(refreshToken: String): TokenReissueResult
    //유저 비밀번호 재설정
//    suspend fun requestTempPassword(email: String): Boolean

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

    //소셜 로그인 이후 사용자 정보 받음
    suspend fun completeSocialProfile(
        socialToken: String,
        nickName: String,
        gender: Gender,
        job: Job,
        purposes: List<Purpose>,
        interests: List<Interest>
    ): Boolean

    suspend fun updateUserProfile(
        nickname: String,
        jobId: Long,
        jobName: String,
        purposes: List<String>,
        interests: List<String>
    )

    suspend fun loginWithKakao(
        token : String): LoginResult


}