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
import com.linku.data.preference.AuthPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val loginSessionStore: LoginSessionStore,
    private val authPreference: AuthPreference
) : AuthRepository {
    override val sessionState: Flow<LoginSessionStore.SessionSnapshot>
        get() = loginSessionStore.session

    /** 닉네임 중복 확인 */
    override suspend fun checkNickname(nickname: String): Result<Unit> =
        safeApiCallUnit {
            Log.d(TAG, "[닉네임 중복 확인] nickname=$nickname")
            authApi.checkNickname(nickname)
        }

    /** 이메일 로그인 */
    override suspend fun login(
        email: String,
        password: String,
        deviceId: String,
        deviceType: String
    ): Result<LoginResult> =
        try {
            safeApiCall {
                authApi.signIn(
                    LoginRequestDTO(
                        email = email,
                        password = password,
                        deviceId = "android-$deviceId",
                        deviceType = deviceType
                    )
                )
            }.let { response ->
                Log.d(TAG, "[로그인 성공]")

                // 계정 비활성화 데이터 무결성 검증 규칙 유지
                // TODO : 이 경우, 부활(?) api 작동할 수 있도록 해야함.
                if (response.status == "INACTIVE" && response.inactiveDate == null) {
                    throw ApiError.User.Inactive(
                        message = "INACTIVE 상태인데 inactiveDate가 없습니다."
                    )
                }

                authPreference.saveTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    userId = response.userId
                )
                Log.d(TAG, "[토큰 저장 완료]")

                Result.success(
                    LoginResult(
                        userId = response.userId,
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken,
                        status = response.status.ifBlank { "ACTIVE" },
                        inactiveDate = response.inactiveDate
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Undefined Error")
            Result.failure(e)
        }

    /** 3. 이메일 회원가입 */
    override suspend fun signUpWithEmail(
        nickname: String,
        email: String,
        password: String,
        gender: Int,
        jobId: Int,
        purposeList: List<Purpose>,
        interestList: List<Interest>,
        termsMap: Map<String, Boolean>
    ): Result<SignUpEmailResult> {
        Log.d(TAG, "[회원가입 시도]")

        if (purposeList.isEmpty()) return Result.failure(IllegalArgumentException("purposeList는 비어 있을 수 없습니다."))
        if (interestList.isEmpty()) return Result.failure(IllegalArgumentException("interestList는 비어 있을 수 없습니다."))

        try {
            safeApiCall {
                authApi.signUpWithEmail(
                    SignUpEmailRequestDTO(
                        nickName = nickname,
                        email = email,
                        password = password,
                        gender = gender,
                        jobId = jobId,
                        purposeList = purposeList.map { it.serverKey },
                        interestList = interestList.map { it.serverKey },
                        termsMap = termsMap
                    )
                )
            }.let { response ->
                Log.d(TAG, "[회원가입 성공]")
                return Result.success(
                    SignUpEmailResult(
                        userId = response.userId,
                        createdAt = response.createdAt
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "[회원가입 실패] ${e.message}")
            return Result.failure(e)
        }
    }

    /* 이메일 인증 코드 전송 */
    override suspend fun sendEmailCode(email: String): Result<Unit> {
        Log.d(TAG, "[이메일 코드 전송 시도] email=$email")
        return safeApiCallUnit {
            authApi.sendVerificationEmail(EmailCodeRequestDTO(email = email))
        }.onSuccess {
            Log.d(TAG, "[이메일 코드 전송 성공]")
        }
    }
    /* 이메일 인증 코드 검증  */
    override suspend fun verifyEmailCode(email: String, code: String): Result<Unit> {
        Log.d(TAG, "[이메일 코드 검증 시도] email=$email")
        return safeApiCallUnit {
            authApi.checkVerificationEmail(EmailVerifyRequestDTO(email = email, code = code))
        }.onSuccess {
            Log.d(TAG, "[이메일 코드 검증 성공]")
        }
    }

    /* 토큰 재발급 */
    override suspend fun reissue(refreshToken: String): Result<TokenReissueResult> {
        Log.d(TAG, "[토큰 재발급 시도]")

        val deviceId = try {
            loginSessionStore.deviceId.first()
                ?: return Result.failure(
                    ApiError.Common.Unauthorized(
                        message = "기기 정보가 없습니다. 다시 로그인해주세요."
                    )
                )
        } catch (e: Exception) {
            return Result.failure(e)
        }

        try {
            safeApiCall {
                authApi.reissue(
                    ReissueRequestDTO(
                        refreshToken = refreshToken,
                        deviceId = deviceId
                    )
                )
            }.let { response ->
                Log.d(TAG, "[토큰 재발급 성공]")
                return Result.success(
                    TokenReissueResult(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "[토큰 재발급 실패] ${e.message}")
            return Result.failure(e)
        }
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
    ): Result<Boolean> {
        Log.d(TAG, "[소셜 프로필 완성 시도]")

        try {
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
            }.let {
                Log.d(TAG, "[소셜 프로필 완성 성공]")
                return Result.success(true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "[소셜 프로필 완성 실패] ${e.message}")
            return Result.failure(e)
        }
    }

    /* 카카오 소셜 로그인 */
    override suspend fun loginWithKakao(token: String): Result<LoginResult> {
        Log.d(TAG, "[카카오 로그인 시도]")

        try {
            safeApiCall {
                authApi.kakaoLogin(SocialLoginRequestDTO(token = token))
            }.let { response ->
                Log.d(TAG, "[카카오 로그인 성공]")

                authPreference.saveTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    userId = response.userId
                )
                Log.d(TAG, "[토큰 저장 완료]")

                return Result.success(
                    LoginResult(
                        userId = response.userId,
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken,
                        status = response.status ?: "",
                        inactiveDate = ""
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "[카카오 로그인 실패] ${e.message}")
            return Result.failure(e)
        }
    }

    // 구글로 로그인 api
    override suspend fun loginWithGoogle(token: String): Result<LoginResult> {
        Log.d(TAG, "[구글 로그인 시도]")

        try {
            safeApiCall {
                authApi.googleLogin(SocialLoginRequestDTO(token = token))
            }.let { response ->
                Log.d(TAG, "[구글 로그인 성공]")

                authPreference.saveTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    userId = response.userId
                )
                Log.d(TAG, "[토큰 저장 완료]")

                return Result.success(
                    LoginResult(
                        userId = response.userId,
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken,
                        status = response.status ?: "",
                        inactiveDate = ""
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "[구글 로그인 실패] ${e.message}")
            return Result.failure(e)
        }
    }
}