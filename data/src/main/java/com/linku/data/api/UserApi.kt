package com.linku.data.api

import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.auth.login.RecoverUserRequestDTO
import com.linku.data.api.dto.user.DeleteUserRequestDTO
import com.linku.data.api.dto.user.DeleteUserResponseDTO
import com.linku.data.api.dto.user.NicknameResponseDTO
import com.linku.data.api.dto.user.RecoverUserResponseDTO
import com.linku.data.api.dto.user.UpdateUserProfileRequestDTO
import com.linku.data.api.dto.user.UserInfoResponseDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {

    // 닉네임 조회
    @GET("auth/nickname")
    suspend fun checkNickname(
    ): BaseResponse<NicknameResponseDTO>

//    //자동 로그인을 위한 토큰 재발급 API
//    @POST("users/reissue")
//    suspend fun reissue(
//        @Header("Refresh-Token") refreshToken: String //헤더로 받음. DTO 불필요
//    ): BaseResponse<RefreshTokenResponseDTO>
//
//    @POST("users/join")
//    suspend fun signUpWithEmail(
//        @Body signUpEmailRequestDTO: SignUpEmailRequestDTO
//    ): BaseResponse<SignUpEmailResponseDTO>
//
//
//    @GET("users/check-nickname")
//    suspend fun checkNickname(
//        @Query("nickname") nickname: String //@ Query로 닉네임을 전달. DTO 불필요
//    ): BaseResponse<String> //BaseResponse 형태임. 별도 클래스 생성 없음.


    // 회원 탈퇴
    @POST("users/inactive")
    suspend fun deleteUser(
        @Body body: DeleteUserRequestDTO
    ): BaseResponse<DeleteUserResponseDTO>

    // 로그아웃 - 요청의 deviceId에 해당하는 현재 디바이스 세션만 로그아웃
    @POST("auth/logout")
    suspend fun logout(
        @Query("deviceId") deviceId: String
    ): BaseResponse<*> // result {}

    // 회원 탈퇴 복구 (계정 활성화) - 탈퇴 유예기간(14일) 내 재로그인 시 호출.
    // 로그인 응답의 accessToken(purpose=RECOVER)이 Authorization 헤더로 자동 첨부됨.
    @POST("users/recover")
    suspend fun recoverUser(
        @Body body: RecoverUserRequestDTO
    ): BaseResponse<RecoverUserResponseDTO>

//    // 로그인
//    @POST("users/login")
//    suspend fun signIn(
//        @Body body: LoginRequestDTO
//    ): BaseResponse<LoginResponseDTO>


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
    ): BaseResponse<*> // BaseResponse 형태임. 별도 클래스 생성 없음.


//    // 이메일 인증 코드 전송
//    @POST("users/emails/code")
//    suspend fun sendVerificationEmail(
//        @Query("email") email: String,
//        @Query("code") code: String
//    ): BaseResponse<String> // BaseResponse 형태임. 별도 클래스 생성 없음.
//
//    // 이메일 인증 코드 검증
//    @GET("users/emails/verify")
//    suspend fun checkVerificationEmail(
//        @Query("email") email: String,
//        @Query("code") code: String
//    ): BaseResponse<EmailVerificationResponseDTO>


//    //소셜 로그인 이후 닉네임, 성별, 직업, 목적, 관심 콘텐츠만 담는 api
//    @PATCH("users/social/complete")
//    suspend fun completeSocialProfile(
//        @Header("Authorization") authorization: String,
//        @Body body: SocialProfileRequestDTO
//    ): BaseResponse<SocialProfileResponseDTO>
//
//    //카카오톡 로그인 api
//    @POST("auth/mobile/kakao")
//    suspend fun kakaoLogin(
//        @Body request: KakaoLoginRequestDTO
//    ): BaseResponse<KakaoLoginResponseDTO>
}