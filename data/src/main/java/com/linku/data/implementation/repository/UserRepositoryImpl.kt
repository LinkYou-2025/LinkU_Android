package com.linku.data.implementation.repository

import android.util.Log
import com.linku.core.model.UserInfo
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Purpose
import com.linku.core.repository.UserRepository
import com.linku.data.api.ServerApi
import com.linku.data.api.dto.user.DeleteUserRequestDTO
import com.linku.data.api.dto.user.UpdateUserProfileRequestDTO
import com.linku.data.api.safeApiCall
import com.linku.data.api.safeApiCallUnit
import com.linku.data.preference.AuthPreference
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

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
        return safeApiCallUnit {
            serverApi.deleteUser(DeleteUserRequestDTO(reason))
        }.onSuccess {
            // 탈퇴 성공 시에만 로컬 세션 제거. 실패하면 계정은 그대로라 로그인 상태를 유지해야 함.
            authPreference.clear()
        }.onFailure {
            Log.e(TAG, "[회원 탈퇴 실패] ${it.message}")
        }
    }

    override suspend fun recoverUser(): Boolean {
        return try {
            // BaseResponse.isSuccess까지 확인. HTTP 2xx여도 응답 바디상 실패일 수 있어 getOrThrow()로 걸러냄.
            safeApiCallUnit { serverApi.recoverUser() }.getOrThrow()
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "[계정 복구 실패] ${e.message}")
            false
        }
    }

//    // BaseResponse<UserInfoDTO> 반환 → withAuth
//    override suspend fun getNickname(userId: Long): String? {
//        return try {
//            serverApi.getUserInfo().result.nickName?.takeIf { it.isNotBlank() }
//        } catch (e: Exception) {
//            null
//        }
//    }

    // 기존 코드 최대한 그대로 사용함.
    override suspend fun getNickname(): String? {
        return safeApiCall {
            serverApi.checkNickname()
        }.onSuccess {
            Log.d(TAG, "[닉네임 조회 성공] nickname=$it")
        }.onFailure {
            Log.e(TAG, "[닉네임 조회 실패] ${it.message}")
        }.getOrNull()?.nickname
    }

    // logout. deleteUser()와 동일하게 서버 호출이 성공했을 때만 로컬 세션을 지움.
    override suspend fun logout(): Boolean {
        return try {
            // getDeviceId() 실패도 try 안에서 잡아야 false로 이어져 onError 처리가 정상 동작함.
            val deviceId = authPreference.getDeviceId()
            Log.d(TAG, "[로그아웃 시도] deviceId=$deviceId")

            safeApiCallUnit { serverApi.logout(deviceId) }.getOrThrow()
            authPreference.clear()
            Log.d(TAG, "로그아웃 완료")
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "[로그아웃 실패] ${e.message}")
            false
        }
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}
