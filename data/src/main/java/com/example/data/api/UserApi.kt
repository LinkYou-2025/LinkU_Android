package com.example.data.api

import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.login.kakao.KakaoLoginRequestDTO
import com.example.data.api.dto.login.kakao.KakaoLoginResponseDTO
import com.example.data.api.dto.server.ApiResponseString
import com.example.data.api.dto.server.DeleteReasonDTO
import com.example.data.api.dto.server.EmailVerificationResponse
import com.example.data.api.dto.server.JoinDTO
import com.example.data.api.dto.server.JoinResultDTO
import com.example.data.api.dto.server.LoginRequestDTO
import com.example.data.api.dto.server.LoginResultDTO
import com.example.data.api.dto.server.SocialProfileRequestDTO
import com.example.data.api.dto.server.TokenPair
import com.example.data.api.dto.server.UpdateProfileDTO
import com.example.data.api.dto.server.UserInfoDTO
import com.example.data.api.dto.server.withDrawalResultDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Header

interface UserApi {

    //자동 로그인을 위한 토큰 재발급 API
    @POST("users/reissue")
    suspend fun reissue(
        @Header("Refresh-Token") refreshToken: String
    ): BaseResponse<TokenPair>

    @POST("users/join")
    suspend fun signUpWithEmail(
        @Body joinDTO: JoinDTO
    ): BaseResponse<JoinResultDTO>


    @GET("users/check-nickname")
    suspend fun checkNickname(
        @Query("nickname") nickname: String
    ): ApiResponseString


    // 회원 탈퇴
    @POST("users/inactive")
    suspend fun deleteUser(
        @Body body: DeleteReasonDTO
    ): BaseResponse<withDrawalResultDTO>

    // 로그인
    @POST("users/login")
    suspend fun signIn(
        @Body body: LoginRequestDTO
    ): BaseResponse<LoginResultDTO>


    // 임시 비밀번호 받기 (RequestBody 사용)
    @POST("users/password/temp")
    suspend fun requestTempPassword(
        @Query("email") email: String
    ): ApiResponseString


    // 마이페이지 조회
    @GET("users/me")
    suspend fun getUserInfo(
//        @Path("userId") userId: Long
    ): BaseResponse<UserInfoDTO>

    // 마이페이지 수정
    @PATCH("users/profile")
    suspend fun updateUserInfo(
        @Body body: UpdateProfileDTO
    ) : ApiResponseString


    // 이메일 인증 코드 전송
    @POST("users/emails/code")
    suspend fun sendVerificationEmail(
        @Query("email") email: String,
        @Query("code") code: String
    ): ApiResponseString


    // 이메일 인증 코드 검증
    @GET("users/emails/verify")
    suspend fun checkVerificationEmail(
        @Query("email") email: String,
        @Query("code") code: String
    ): BaseResponse<EmailVerificationResponse>


    //소셜 로그인 이후 닉네임, 성별, 직업, 목적, 관심 콘텐츠만 담는 api
    @PATCH("users/social/complete")
    suspend fun completeSocialProfile(
        @Header("Authorization") authorization: String,
        @Body body: SocialProfileRequestDTO
    ): ApiResponseString

    //카카오톡 로그인 api
    @POST("auth/mobile/kakao")
    suspend fun kakaoLogin(
        @Body request: KakaoLoginRequestDTO
    ): BaseResponse<KakaoLoginResponseDTO>
}