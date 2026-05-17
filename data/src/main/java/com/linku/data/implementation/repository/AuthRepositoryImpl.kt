package com.linku.data.implementation.repository

import android.util.Log
import com.linku.core.datastore.session.LoginSessionStore
import com.linku.core.error.ApiError
import com.linku.core.model.LoginResult
import com.linku.core.model.TokenReissueResult
import com.linku.core.model.auth.Gender
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Job
import com.linku.core.model.auth.Purpose
import com.linku.core.model.auth.SignUpEmailResult
import com.linku.core.repository.AuthRepository
import com.linku.data.api.AuthApi
import com.linku.data.api.dto.auth.login.email.LoginRequestDTO
import com.linku.data.api.dto.auth.login.social.SocialLoginRequestDTO
import com.linku.data.api.dto.auth.refreshToken.ReissueRequestDTO
import com.linku.data.api.dto.auth.signup.email.EmailCodeRequestDTO
import com.linku.data.api.dto.auth.signup.email.EmailVerifyRequestDTO
import com.linku.data.api.dto.auth.signup.email.SignUpEmailRequestDTO
import com.linku.data.api.safeApiCall
import com.linku.data.api.safeApiCallUnit
import com.linku.data.mapper.SocialProfileMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val loginSessionStore: LoginSessionStore
) : AuthRepository {
    override val sessionState: Flow<LoginSessionStore.SessionSnapshot>
        get() = loginSessionStore.session

    /** 닉네임 중복 확인 */
    override suspend fun checkNickname(nickname: String) {
        Log.d(TAG, "[닉네임 중복 확인] nickname=$nickname")
        safeApiCallUnit { authApi.checkNickname(nickname) }
    }

    /** 이메일 로그인 */
    override suspend fun login(
        email: String,
        password: String,
        deviceId: String,
        deviceType: String
    ): LoginResult {
        Log.d(TAG, "[로그인 시도]")

        // API 호출 및 결과 수신
        val emailSignUpResponse = safeApiCall {
            authApi.signIn(
                LoginRequestDTO(
                    email = email,
                    password = password,
                    deviceId = "android-$deviceId",   // android- 접두사 붙이기
                    deviceType = "PHONE"
                )
            )
        }

        Log.d(TAG, "[로그인 성공]")

        // INACTIVE인데 inactiveDate가 null이면 서버 오류
        if (emailSignUpResponse.status == "INACTIVE" && emailSignUpResponse.inactiveDate == null) {
            throw ApiError.User.Inactive(
                code = "USERS4042",
                message = "INACTIVE 상태인데 inactiveDate가 없습니다."
            )
        }

        return LoginResult(
            userId = emailSignUpResponse.userId,
            accessToken = emailSignUpResponse.accessToken,
            refreshToken = emailSignUpResponse.refreshToken,
            status = emailSignUpResponse.status.ifBlank { "ACTIVE" },
            inactiveDate = emailSignUpResponse.inactiveDate
        )
    }

    /** 이메일 회원가입 */
    override suspend fun signUpWithEmail(
        nickname: String,
        email: String,
        password: String,
        gender: Int,
        jobId: Int,
        purposeList: List<Purpose>,
        interestList: List<Interest>
    ): SignUpEmailResult {
        Log.d(TAG, "[회원가입 시도]")

        require(purposeList.isNotEmpty()) { "purposeList는 비어 있을 수 없습니다." }
        require(interestList.isNotEmpty()) { "interestList는 비어 있을 수 없습니다." }

        val signUpEmailResponse = safeApiCall {
            authApi.signUpWithEmail(
                SignUpEmailRequestDTO(
                    nickName = nickname,
                    email = email,
                    password = password,
                    gender = gender,
                    jobId = jobId,
                    purposeList = purposeList.map { it.serverKey },
                    interestList = interestList.map { it.serverKey }
                )
            )
        }

        Log.d(TAG, "[회원가입 성공]")

        return SignUpEmailResult(
            userId = signUpEmailResponse.userId,
            createdAt = signUpEmailResponse.createdAt
        )

    }

    /* 이메일 인증 코드 전송 */
    override suspend fun sendEmailCode(email: String) {
        Log.d(TAG, "[이메일 코드 전송 시도] email=$email")
        safeApiCallUnit {
            authApi.sendVerificationEmail(EmailCodeRequestDTO(email = email))
        }
        Log.d(TAG, "[이메일 코드 전송 성공]")
    }

    /* 이메일 인증 코드 검증  */
    override suspend fun verifyEmailCode(email: String, code: String): Boolean {
        Log.d(TAG, "[이메일 코드 검증 시도] email=$email")
        safeApiCallUnit {
            authApi.checkVerificationEmail(EmailVerifyRequestDTO(email = email, code = code))
        }
        Log.d(TAG, "[이메일 코드 검증 성공]")
        return true  // 예외 안 터지면 성공
    }

    /* 토큰 재발급 */
    override suspend fun reissue(refreshToken: String): TokenReissueResult {
        Log.d(TAG, "[토큰 재발급 시도]")

        val deviceId = loginSessionStore.deviceId.first()
            ?: throw ApiError.Auth.TokenExpired(
                code = "AUTH_DEVICE_NOT_FOUND",
                message = "기기 정보가 없습니다. 다시 로그인해주세요."
            )

        val response = safeApiCall {
            authApi.reissue(
                ReissueRequestDTO(
                    refreshToken = refreshToken,
                    deviceId = deviceId
                )
            )
        }

        Log.d(TAG, "[토큰 재발급 성공]")

        return TokenReissueResult(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )
    }

    companion object {
        private const val TAG = "UserRepository"
    }

    /* 소셜 로그인 후 프로필 완성 */
    override suspend fun completeSocialProfile(
        socialToken: String,
        nickName: String,
        gender: Gender,
        job: Job,
        purposes: List<Purpose>,
        interests: List<Interest>
    ): Boolean {

        Log.d(TAG, "[소셜 프로필 완성 시도]")

        safeApiCall {
            authApi.completeSocialProfile(
                authorization = "Bearer $socialToken",
                body = SocialProfileMapper.toRequest(
                    nickName = nickName,
                    gender = gender,
                    job = job,
                    purposes = purposes,
                    interests = interests
                )
            )
        }

        Log.d(TAG, "[소셜 프로필 완성 성공]")
        return true

    }

    /* 카카오 소셜 로그인 */
    override suspend fun loginWithKakao(token: String): LoginResult {
        Log.d(TAG, "[카카오 로그인 시도]")

        val response = safeApiCall {
            authApi.kakaoLogin(SocialLoginRequestDTO(token = token))
        }

        Log.d(TAG, "[카카오 로그인 성공]")

        return LoginResult(
            userId = response.userId,
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            status = response.status ?: "",
            inactiveDate = ""
        )

    }

    // 구글로 로그인 api
    override suspend fun loginWithGoogle(token: String): Result<LoginResult> {
        Log.d(TAG, "[구글 로그인 시도]")

        return try {
            val response = safeApiCall {
                authApi.googleLogin(SocialLoginRequestDTO(token = token))
            }
            Log.d(TAG, "[구글 로그인 성공]")
            Result.success(
                LoginResult(
                    userId = response.userId,
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    status = response.status ?: "",
                    inactiveDate = ""
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "[구글 로그인 실패] ${e.message}")
            Result.failure(e)
        }
    }


//    // 구글로 로그인 api
//    override suspend fun loginWithGoogle(token: String): Result<LoginResult> {
//        Log.d("UserRepositoryImpl", "loginWithGoogle token: $token")
//        val googleResponse: SocialLoginResponseDTO
//
//        try {
//            Log.d("UserRepositoryImpl", "loginWithGoogle try")
//            googleResponse = serverApi.withErrorHandling {
//                googleLogin(SocialLoginRequestDTO(token = token))
//            }
//            Result.success(
//                LoginResult(
//                    userId = googleResponse.userId,
//                    accessToken = googleResponse.accessToken,
//                    refreshToken = googleResponse.refreshToken,
//                    status = googleResponse.status ?: "",
//                    inactiveDate = ""
//                )
//            )
//
//            Log.d("UserRepositoryImpl", "loginWithGoogle response: $googleResponse")
//        } catch (e: Exception) {
//            Log.e("UserRepositoryImpl", "loginWithGoogle error: $e")
//            throw e
//        }
//
//        Log.d("UserRepositoryImpl", "loginWithGoogle return: $googleResponse")
//
//    }

}