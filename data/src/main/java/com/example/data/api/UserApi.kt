package com.example.data.api

import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.ApiResponseString
import com.example.data.api.dto.server.DeleteReasonDTO
import com.example.data.api.dto.server.EmailVerificationResponse
import com.example.data.api.dto.server.JoinDTO
import com.example.data.api.dto.server.JoinResultDTO
import com.example.data.api.dto.server.LoginRequestDTO
import com.example.data.api.dto.server.LoginResultDTO
import com.example.data.api.dto.server.UpdateProfileDTO
import com.example.data.api.dto.server.UserInfoDTO
import com.example.data.api.dto.server.withDrawalResultDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    // 회원 가입
    @POST("/api/users/join")
    suspend fun signUp(
        @Body body: JoinDTO
    ): BaseResponse<JoinResultDTO>

    // 닉네임 중복 확인
    @GET("/api/users/check-nickname")
    suspend fun checkNickDuplication(
        @Query("username") username: String
    ): ApiResponseString

    // 위의 코드가 안된다면 아래 코드로 교체
//    @GET("/api/users/check-nickname")
//    suspend fun checkIdDuplication(
//        @Query("username") username: String
//    ): BaseResponse<String>

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

    // 임시 비밀번호 받기 -> 후순위 개발
    @POST("/api/users/password/temp")
    suspend fun getTempPw(
        @Query("email") email: String
    ) : ApiResponseString

    // 위의 코드가 안된다면 아래 코드로 교체
//    @POST("/api/users/password/temp")
//    suspend fun getTempPw(
//        @Query("email") email: String
//    ): BaseResponse<String>

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

    // 위의 코드가 안된다면 아래 코드로 교체
//    @PATCH("/api/users/profile")
//    suspend fun updateUserInfo(
//        @Body body: UpdateProfileDTO
//    ) : BaseResponse<String>

    // 이메일 인증 코드 전송
    @POST("/api/users/emails/code")
    suspend fun sendVerificationEmail(
        @Query("email") email: String
    ): ApiResponseString

    // 위의 코드가 안된다면 아래 코드로 교체
//    @POST("/api/users/emails/code")
//    suspend fun sendVerificationEmail(
//        @Query("email") email: String
//    ): BaseResponse<String>

    // 이메일 인증 코드 검증
    @GET("/api/users/emails/verify")
    suspend fun checkVerificationEmail(
        @Query("email") email: String,
        @Query("code") code: String
    ): BaseResponse<EmailVerificationResponse>
}