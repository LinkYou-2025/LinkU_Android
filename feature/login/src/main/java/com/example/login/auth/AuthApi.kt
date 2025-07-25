package com.example.login.auth

import retrofit2.http.POST
import retrofit2.http.Query



interface AuthApi {

    // 이메일 인증 코드 요청 API
    @POST("/api/users/emails/code")
    suspend fun sendEmailCode(
        @Query("email") email: String
    ): BaseResponse<String>

    // 이메일 인증 코드 검증 API
    @POST("/api/users/emails/verify")
    suspend fun verifyEmailCode(
        @Query("email") email: String,
        @Query("code") code: String
    ): BaseResponse<String>
}
