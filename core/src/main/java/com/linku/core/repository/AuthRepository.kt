package com.linku.core.repository

import com.linku.core.datastore.session.LoginSessionStore
import com.linku.core.model.LoginResult
import com.linku.core.model.TokenReissueResult
import com.linku.core.model.auth.Gender
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Job
import com.linku.core.model.auth.Purpose
import com.linku.core.model.auth.SignUpEmailResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val sessionState: Flow<LoginSessionStore.SessionSnapshot>
    //레포지토리가 세션 상태 플로우 제공하도록 수정함.

    suspend fun checkNickname(nickname: String): Result<Unit>

    suspend fun login(
        email: String,
        password: String,
        deviceId: String,
        deviceType: String
    ): Result<LoginResult>
    suspend fun signUpWithEmail(
        nickname: String,
        email: String,
        password: String,
        gender: Int,
        jobId: Int,
        purposeList: List<Purpose>,
        interestList: List<Interest>,
        termsMap: Map<String, Boolean>
    ): Result<SignUpEmailResult>

    suspend fun sendEmailCode(email: String): Result<Unit>
    suspend fun verifyEmailCode(email: String, code: String): Result<Unit>

    suspend fun reissue(refreshToken: String): Result<TokenReissueResult>

    //소셜 로그인 이후 사용자 정보 받음
    suspend fun completeSocialProfile(
        socialToken: String,
        nickName: String,
        gender: Gender,
        job: Job,
        purposes: List<Purpose>,
        interests: List<Interest>
    ): Result<Boolean>

    // 카카오로 로그인 하기
    suspend fun loginWithKakao(
        token: String
    ): Result<LoginResult>


    //구글로 로그인 하기
    suspend fun loginWithGoogle(
        token: String
    ): Result<LoginResult>


}