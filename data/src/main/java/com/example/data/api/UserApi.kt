package com.example.data.api

import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.ApiResponseString
import com.example.data.api.dto.server.DeleteReasonDTO
import com.example.data.api.dto.server.EmailVerificationResponse
import com.example.data.api.dto.server.JoinDTO
import com.example.data.api.dto.server.JoinResultDTO
import com.example.data.api.dto.server.LoginRequestDTO
import com.example.data.api.dto.server.LoginResultDTO
import com.example.data.api.dto.server.TempPasswordRequestDTO
import com.example.data.api.dto.server.TokenPair
import com.example.data.api.dto.server.UpdateProfileDTO
import com.example.data.api.dto.server.UserInfoDTO
import com.example.data.api.dto.server.withDrawalResultDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Header

interface UserApi {

    //자동 로그인을 위한 토큰 재발급 API
    @POST("/api/users/reissue")
    suspend fun reissue(
        @Header("Refresh-Token") refreshToken: String
    ): BaseResponse<TokenPair>

    @POST("/api/users/join")
    suspend fun signUp(
        @Body joinDTO: JoinDTO
    ): BaseResponse<JoinResultDTO>


    @GET("/api/users/check-nickname")
    suspend fun checkNickname(
        @Query("nickname") nickname: String
    ): ApiResponseString


    // 회원 탈퇴
    @POST("/api/users/inactive")
    suspend fun deleteUser(
        @Body body: DeleteReasonDTO
    ): BaseResponse<withDrawalResultDTO>

    // 로그인
    @POST("/api/users/login")
    suspend fun signIn(
        @Body body: LoginRequestDTO
    ): BaseResponse<LoginResultDTO>


    // 임시 비밀번호 받기 (RequestBody 사용)
    @POST("/api/users/password/temp")
    suspend fun requestTempPassword(
        @Query("email") email: String
    ): ApiResponseString


    // 마이페이지 조회
    @GET("/api/users/{userId}")
    suspend fun getUserInfo(
        @Path("userId") userId: Long
    ): BaseResponse<UserInfoDTO>

    // 마이페이지 수정
    @PATCH("/api/users/profile")
    suspend fun updateUserInfo(
        @Body body: UpdateProfileDTO
    ) : ApiResponseString


    // 이메일 인증 코드 전송
    @POST("/api/users/emails/code")
    suspend fun sendVerificationEmail(
        @Query("email") email: String,
        @Query("code") code: String
    ): ApiResponseString


    // 이메일 인증 코드 검증
    @GET("/api/users/emails/verify")
    suspend fun checkVerificationEmail(
        @Query("email") email: String,
        @Query("code") code: String
    ): BaseResponse<EmailVerificationResponse>
}