package com.linku.data.implementation.repository

import android.util.Log
import com.linku.core.error.ApiError
import com.linku.core.model.UserInfo
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Purpose
import com.linku.core.model.auth.RecoverResult
import com.linku.core.repository.UserRepository
import com.linku.core.util.logging.LinkuLog
import com.linku.core.util.logging.d
import com.linku.data.api.ServerApi
import com.linku.data.api.dto.auth.login.RecoverUserRequestDTO
import com.linku.data.api.dto.user.DeleteUserRequestDTO
import com.linku.data.api.dto.user.UpdateUserProfileRequestDTO
import com.linku.data.api.safeApiCall
import com.linku.data.api.safeApiCallUnit
import com.linku.data.util.SingleFlight
import com.linku.core.preference.AuthPreference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.onSuccess

class UserRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
) : UserRepository {

    private val _cachedUserInfo = MutableStateFlow<UserInfo?>(null)
    override val cachedUserInfo: StateFlow<UserInfo?> = _cachedUserInfo.asStateFlow()

    /**
     * 로그인 직후처럼 같은 순간에 여러 곳에서 부르는 `GET /users/me`를 한 번의 요청으로 합칩니다.
     * 이미 진행 중인 요청이 있을 때만 결과를 공유하고, 끝난 뒤의 호출은 항상 새로 조회합니다.
     * (다른 기기에서 바꾼 정보를 화면 진입마다 반영해야 하므로 결과를 시간으로 캐시하지 않습니다.)
     */
    private val userInfoFlight = SingleFlight<Result<UserInfo>>()

    /** `GET /auth/nickname`의 동시 중복 호출을 합칩니다. 동작 원칙은 [userInfoFlight]와 같습니다. */
    private val nicknameFlight = SingleFlight<Result<String>>()

    override suspend fun getUserInfo(userId: Long): Result<UserInfo> = userInfoFlight.run {
        Log.d(TAG, "[유저 정보 가져오기 시도] userId=$userId")

        safeApiCall(
            apiCall = { serverApi.getUserInfo() }
        ).map { dto ->
            UserInfo(
                nickname = dto.nickName.orEmpty(),
                email = dto.email,
                gender = dto.gender.value,
                jobId = dto.job.id,
                jobName = dto.job.name,
                myLinku = dto.myLinku,
                myFolder = dto.myFolder,
                myAiLinku = dto.myAiLinku,
                purposes = dto.purposes.mapNotNull { Purpose.fromServerKey(it)?.displayName ?: it },
                interests = dto.interests.mapNotNull {
                    Interest.fromServerKey(it)?.displayName ?: it
                }
            )
        }.onSuccess { info ->
            _cachedUserInfo.value = info
        }
    }

    override suspend fun updateUserInfo(
        nickname: String,
        jobId: Long,
        purposes: List<String>,
        interests: List<String>
    ): Result<Unit> {

        val dto = UpdateUserProfileRequestDTO(
            nickname = nickname,
            jobId = jobId,
            purposes = purposes.mapNotNull { Purpose.fromDisplayName(it)?.serverKey },
            interests = interests.mapNotNull { Interest.fromDisplayName(it)?.serverKey }
        )

        return safeApiCallUnit {
            serverApi.updateUserInfo(dto)
        }.onSuccess {
            // 수정 전에 출발한 진행 중 조회와 합쳐지면 수정 전 값을 받으므로, 이후 호출은 새로 조회하게 합니다.
            invalidateInFlightRequests()
        }
    }

    /** 진행 중인 사용자 정보와 닉네임 조회 공유를 끊어 이후 호출이 새 요청이 되게 합니다. */
    private suspend fun invalidateInFlightRequests() {
        userInfoFlight.invalidate()
        nicknameFlight.invalidate()
    }

    override suspend fun deleteUser(reason: String): Result<Unit> {
        val result = safeApiCallUnit { serverApi.deleteUser(DeleteUserRequestDTO(reason)) }
        if (result.isFailure) {
            Log.e(TAG, "[회원 탈퇴 실패] ${result.exceptionOrNull()?.message}")
            return result
        }

        // 탈퇴 성공 시에만 로컬 세션 제거. 실패하면 계정은 그대로라 로그인 상태를 유지해야 함.
        // DataStore I/O 예외가 여기서 나면 Result 계약 밖으로 새지 않도록 잡아서 failure로 변환함.
        return try {
            authPreference.clear()
            _cachedUserInfo.value = null
            invalidateInFlightRequests()
            result
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "[회원 탈퇴 세션 정리 실패] ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun recoverUser(): Result<RecoverResult> {
        LinkuLog.d(TAG, "[계정 복구 시도]")

        val savedDeviceId = authPreference.getDeviceId()
        val savedDeviceType = authPreference.getDeviceType()

        return safeApiCall {
            serverApi.recoverUser(
                RecoverUserRequestDTO(
                    deviceId = savedDeviceId,
                    deviceType = savedDeviceType
                )
            )

        }.map { dto ->
            RecoverResult(
                accessToken = dto.accessToken,
                refreshToken = dto.refreshToken
            )
        }.onSuccess { result ->
            LinkuLog.d(TAG, "[계정 복구 성공] 정식 토큰 저장")
            authPreference.updateAccessToken(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken
            )
        }
    }

    // 닉네임 조회
    override suspend fun getNickname(): Result<String> = nicknameFlight.run {
        LinkuLog.d(TAG, "닉네임 api 호출 시도중")
        safeApiCall {
            serverApi.checkNickname()
        }.map { dto ->
            dto.nickname
        }.onSuccess {
            Log.d(TAG, "[닉네임 조회 성공] nickname=$it")
        }.onFailure {
            Log.e(TAG, "[닉네임 조회 실패] ${it.message}")
        }
    }

    // logout. deleteUser()와 동일하게 서버 호출이 성공했을 때만 로컬 세션을 지움.
    // 단, 서버가 이미 세션을 무효로 보는 인증 에러(만료된/무효한 토큰)는 "이미 로그아웃된 상태"와
    // 동치이므로 예외적으로 로컬 세션을 지운다 - 그렇지 않으면 만료된 세션에서는 로그아웃 버튼이
    // 영원히 동작하지 않게 된다(서버 호출이 항상 인증 에러로 실패하므로).
    override suspend fun logout(): Result<Unit> {
        return try {
            val deviceId = authPreference.getDeviceId()
            Log.d(TAG, "[로그아웃 시도] deviceId=$deviceId")

            val result = safeApiCallUnit { serverApi.logout(deviceId) }
            val error = result.exceptionOrNull()
            if (result.isFailure && !isSessionAlreadyInvalid(error)) {
                throw error ?: IllegalStateException("로그아웃 실패")
            }

            authPreference.clear()
            _cachedUserInfo.value = null
            // 다음에 로그인하는 계정이 이전 계정의 진행 중 조회 결과를 받지 않도록 끊습니다.
            invalidateInFlightRequests()
            Log.d(TAG, "로그아웃 완료")
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "[로그아웃 실패] ${e.message}")
            Result.failure(e)
        }
    }

    private fun isSessionAlreadyInvalid(error: Throwable?): Boolean {
        return error is ApiError.Common.Unauthorized ||
            error is ApiError.Auth.Unauthorized ||
            error is ApiError.Auth.TokenExpired ||
            error is ApiError.Auth.InvalidToken
    }

    override suspend fun updateMarketingTerms(): Result<Unit> {
        return safeApiCallUnit { serverApi.updateMarketingTerms() }
            .onSuccess {
                LinkuLog.d(TAG, "[마케팅 업데이트 성공]")
            }.onFailure {
                LinkuLog.d(TAG, "[마케팅 업데이트 실패]")
            }
    }

    override suspend fun checkMarketingTermsAgreed(): Result<Boolean> {
        return safeApiCall { serverApi.checkTermsStatus() }
            .map { dto ->
                dto.termsStatus.marketing
            }
            .onSuccess {
                LinkuLog.d(TAG, "약관 상태 조회 성공")
            }.onFailure {
                LinkuLog.d(TAG, "약관 상태 조회 실패")
            }
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}
