package com.linku.data.api

import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.auth.login.email.LoginRequestDTO
import com.linku.data.api.dto.auth.login.email.LoginResponseDTO
import com.linku.data.api.dto.auth.login.kakao.SocialLoginRequestDTO
import com.linku.data.api.dto.auth.login.kakao.SocialLoginResponseDTO
import com.linku.data.api.dto.auth.refreshToken.RefreshTokenResponseDTO
import com.linku.data.api.dto.auth.signup.email.EmailVerificationResponseDTO
import com.linku.data.api.dto.auth.signup.email.SignUpEmailRequestDTO
import com.linku.data.api.dto.auth.signup.email.SignUpEmailResponseDTO
import com.linku.data.api.dto.auth.signup.social.SocialProfileRequestDTO
import com.linku.data.api.dto.auth.signup.social.SocialProfileResponseDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

// 회원가입, 로그인 관련 api
interface AuthApi {

    //자동 로그인을 위한 토큰 재발급 API
    @POST("auth/token/reissue")
    suspend fun reissue(
        @Header("Refresh-Token") refreshToken: String //헤더로 받음. DTO 불필요
    ): BaseResponse<RefreshTokenResponseDTO>

    @POST("auth/signup/email")
    suspend fun signUpWithEmail(
        @Body signUpEmailRequestDTO: SignUpEmailRequestDTO
    ): BaseResponse<SignUpEmailResponseDTO>


    @GET("auth/check-nickname")
    suspend fun checkNickname(
        @Query("nickname") nickname: String //@ Query로 닉네임을 전달. DTO 불필요
    ): BaseResponse<String> //BaseResponse 형태임. 별도 클래스 생성 없음.

    // 로그인
    @POST("auth/login")
    suspend fun signIn(
        @Body body: LoginRequestDTO
    ): BaseResponse<LoginResponseDTO>

    // 이메일 인증 코드 전송
    @POST("auth/email/code")
    suspend fun sendVerificationEmail(
        @Query("email") email: String,
        @Query("code") code: String
    ): BaseResponse<String> // BaseResponse 형태임. 별도 클래스 생성 없음.

    // 이메일 인증 코드 검증
    @GET("auth/email/verify")
    suspend fun checkVerificationEmail(
        @Query("email") email: String,
        @Query("code") code: String
    ): BaseResponse<EmailVerificationResponseDTO>

    //소셜 로그인 이후 닉네임, 성별, 직업, 목적, 관심 콘텐츠만 담는 api
    @PATCH("auth/signup/social/profile")
    suspend fun completeSocialProfile(
        @Header("Authorization") authorization: String,
        @Body body: SocialProfileRequestDTO
    ): BaseResponse<SocialProfileResponseDTO>

    //카카오톡 로그인 api
    @POST("auth/mobile/kakao")
    suspend fun kakaoLogin(
        @Body request: SocialLoginRequestDTO
    ): BaseResponse<SocialLoginResponseDTO>

    //구글 로그인 api
    @POST("auth/mobile/google")
    suspend fun googleLogin(
        @Body request: SocialLoginRequestDTO
    ): BaseResponse<SocialLoginResponseDTO>

}