package com.example.data.api

import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.login.kakao.KakaoLoginRequestDTO
import com.example.data.api.dto.login.kakao.KakaoLoginResponseDTO
import com.example.data.api.dto.user.DeleteUserRequestDTO
import com.example.data.api.dto.user.EmailVerificationResponseDTO
import com.example.data.api.dto.user.SignUpEmailRequestDTO
import com.example.data.api.dto.user.SignUpEmailResponseDTO
import com.example.data.api.dto.user.LoginRequestDTO
import com.example.data.api.dto.user.LoginResponseDTO
import com.example.data.api.dto.user.SocialProfileResponseDTO
import com.example.data.api.dto.user.SocialProfileRequestDTO
import com.example.data.api.dto.user.RefreshTokenResponseDTO
import com.example.data.api.dto.user.UpdateUserProfileRequestDTO
import com.example.data.api.dto.user.UserInfoResponseDTO
import com.example.data.api.dto.user.DeleteUserResponseDTO
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
        @Header("Refresh-Token") refreshToken: String //헤더로 받음. DTO 불필요
    ): BaseResponse<RefreshTokenResponseDTO>

    @POST("users/join")
    suspend fun signUpWithEmail(
        @Body signUpEmailRequestDTO: SignUpEmailRequestDTO
    ): BaseResponse<SignUpEmailResponseDTO>


    @GET("users/check-nickname")
    suspend fun checkNickname(
        @Query("nickname") nickname: String //@ Query로 닉네임을 전달. DTO 불필요
    ): BaseResponse<String> //BaseResponse 형태임. 별도 클래스 생성 없음.


    // 회원 탈퇴
    @POST("users/inactive")
    suspend fun deleteUser(
        @Body body: DeleteUserRequestDTO
    ): BaseResponse<DeleteUserResponseDTO>

    // 로그인
    @POST("users/login")
    suspend fun signIn(
        @Body body: LoginRequestDTO
    ): BaseResponse<LoginResponseDTO>


//    // 임시 비밀번호 받기 (RequestBody 사용) -> // TODO : 나오는대로 변경 해야함....
//    @POST("users/password/temp")
//    suspend fun requestTempPassword(
//        @Query("email") email: String
//    ): ApiResponseString


    // 마이페이지 조회
    @GET("users/me")
    suspend fun getUserInfo(
    ): BaseResponse<UserInfoResponseDTO>

    // 마이페이지 수정
    @PATCH("users/profile")
    suspend fun updateUserInfo(
        @Body body: UpdateUserProfileRequestDTO
    ) : BaseResponse<String> // BaseResponse 형태임. 별도 클래스 생성 없음.


    // 이메일 인증 코드 전송
    @POST("users/emails/code")
    suspend fun sendVerificationEmail(
        @Query("email") email: String,
        @Query("code") code: String
    ): BaseResponse<String> // BaseResponse 형태임. 별도 클래스 생성 없음.

    // 이메일 인증 코드 검증
    @GET("users/emails/verify")
    suspend fun checkVerificationEmail(
        @Query("email") email: String,
        @Query("code") code: String
    ): BaseResponse<EmailVerificationResponseDTO>


    //소셜 로그인 이후 닉네임, 성별, 직업, 목적, 관심 콘텐츠만 담는 api
    @PATCH("users/social/complete")
    suspend fun completeSocialProfile(
        @Header("Authorization") authorization: String,
        @Body body: SocialProfileRequestDTO
    ): BaseResponse<SocialProfileResponseDTO>

    //카카오톡 로그인 api
    @POST("auth/mobile/kakao")
    suspend fun kakaoLogin(
        @Body request: KakaoLoginRequestDTO
    ): BaseResponse<KakaoLoginResponseDTO>
}