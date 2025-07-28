package com.example.core.domain


import com.example.core.api.EmailCodeResponse
import com.example.core.api.EmailVerifyResponse
import com.example.core.api.LoginResponse
import com.example.core.api.NicknameResponse
import com.example.core.api.SignUpRequest
import com.example.core.api.SignUpResponse

interface LoginRepository {
    
    //닉네임 중복 확인 api 연동
    suspend fun checkNickname(nickname: String): NicknameResponse

    //로그인 추가
    suspend fun login(email: String, password: String): LoginResponse

    //회원가입 api
    suspend fun signUp(request: SignUpRequest): SignUpResponse
    
    //이메일 인증, 확인
    suspend fun sendEmailCode(email: String): EmailCodeResponse
    suspend fun verifyEmailCode(email: String, code: String): EmailVerifyResponse

}