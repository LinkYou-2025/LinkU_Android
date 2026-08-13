package com.linku.data.implementation.repository

import android.util.Log
import com.linku.core.model.Nickname
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
import com.linku.data.preference.AuthPreference
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import kotlin.onSuccess

class UserRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
) : UserRepository {


    override suspend fun getUserInfo(userId: Long): Result<UserInfo> {
        Log.d(TAG, "[유저 정보 가져오기 시도] userId=$userId")

        return safeApiCall(
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
        }
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
    override suspend fun getNickname(): Result<Nickname> {
        LinkuLog.d(TAG, "닉네임 api 호출 시도중")
        return safeApiCall {
            serverApi.checkNickname()
        }.map{dto ->
            Nickname(
                nickname = dto.nickname,
            )
        }.onSuccess {
            Log.d(TAG, "[닉네임 조회 성공] nickname=$it")
        }.onFailure {
            Log.e(TAG, "[닉네임 조회 실패] ${it.message}")
        }
    }

    // logout. deleteUser()와 동일하게 서버 호출이 성공했을 때만 로컬 세션을 지움.
    override suspend fun logout(): Result<Unit> {
        return try {
            val deviceId = authPreference.getDeviceId()
            Log.d(TAG, "[로그아웃 시도] deviceId=$deviceId")

            safeApiCallUnit { serverApi.logout(deviceId) }.getOrThrow()
            authPreference.clear()
            Log.d(TAG, "로그아웃 완료")
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "[로그아웃 실패] ${e.message}")
            Result.failure(e)
        }
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
