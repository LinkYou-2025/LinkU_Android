package com.example.core.api



import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Headers

// 닉네임 중복 확인 응답 DTO
data class NicknameResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: String?
)

// 로그인/회원가입 관련 API
interface LoginApi {

    /**
     * 🔹 닉네임 중복 확인 API
     *  - 헤더에 Bearer JWT 필요
     */
    // 로그인은 JWT 필요 (No-Auth 없음)
    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // 이메일 인증 API → JWT 필요 없음
    @POST("api/users/emails/code")
    @Headers("No-Auth: true")
    suspend fun sendEmailCode(@Query("email") email: String): EmailCodeResponse

    @GET("api/users/emails/verify")
    @Headers("No-Auth: true")
    suspend fun verifyEmailCode(
        @Query("email") email: String,
        @Query("code") code: String
    ): EmailVerifyResponse

    // 닉네임 중복 확인 → JWT 필요
    @GET("api/users/check-nickname")
    suspend fun checkNickname(@Query("nickname") nickname: String): NicknameResponse

    @POST("api/users")  // 서버 명세서에 맞게 URL 확인 필요
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse
}