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

    suspend fun checkNickname(nickname: String): Unit

    suspend fun login(email: String, password: String): LoginResult
    suspend fun signUpWithEmail(
        nickname: String,
        email: String,
        password: String,
        gender: Int,
        jobId: Int,
        purposeList: List<Purpose>,
        interestList: List<Interest>
    ) : SignUpEmailResult

    suspend fun sendEmailCode(email: String) // 이메일 전송 요청이니 return이 필요 없음.
    suspend fun verifyEmailCode(email: String, code: String): Boolean

    suspend fun reissue(refreshToken: String): TokenReissueResult

    //소셜 로그인 이후 사용자 정보 받음
    suspend fun completeSocialProfile(
        socialToken: String,
        nickName: String,
        gender: Gender,
        job: Job,
        purposes: List<Purpose>,
        interests: List<Interest>
    ): Boolean

    // 카카오로 로그인 하기
    suspend fun loginWithKakao(
        token : String): LoginResult
}