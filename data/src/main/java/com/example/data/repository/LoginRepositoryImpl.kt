package com.example.data.repository


import com.example.core.api.LoginRequest
import android.util.Log
import com.example.core.api.EmailCodeResponse
import com.example.core.api.EmailVerifyResponse
import com.example.core.api.LoginResponse
import com.example.core.api.LoginApi
import com.example.core.api.NicknameResponse
import com.example.core.api.SignUpRequest
import com.example.core.api.SignUpResponse
import com.example.core.domain.LoginRepository
import jakarta.inject.Inject

/**
 * 로그인/회원가입 관련 Repository 구현체
 * core의 LoginApi를 사용하여 서버와 통신
 */
class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi
) : LoginRepository {

    // 닉네임을 파라미터로 받아 API에 전달
    override suspend fun checkNickname(nickname: String): NicknameResponse {
        Log.d("LoginRepositoryImpl", "API 요청 시작 → $nickname")
        val response = loginApi.checkNickname(nickname)
        Log.d("LoginRepositoryImpl", "API 응답 → isSuccess=${response.isSuccess}, code=${response.code}, message=${response.message}, result=${response.result}")
        return response
    }

    // 로그인 API 호출 구현
    override suspend fun login(email: String, password: String): LoginResponse {
        return loginApi.login(LoginRequest(email, password))
    }

    override suspend fun sendEmailCode(email: String): EmailCodeResponse {
        Log.d("LoginRepositoryImpl", "이메일 코드 전송 요청 → $email")
        return loginApi.sendEmailCode(email)
    }

    //회원가입
    override suspend fun signUp(request: SignUpRequest): SignUpResponse {
        return loginApi.signUp(request)
    }

    override suspend fun verifyEmailCode(email: String, code: String): EmailVerifyResponse {
        Log.d("LoginRepositoryImpl", "이메일 코드 검증 요청 → email=$email, code=$code")
        return loginApi.verifyEmailCode(email, code)
    }
}