package com.example.data.implementation.repository

import android.util.Log
import com.example.core.model.LoginResult
import com.example.core.model.TokenReissueResult
import com.example.core.model.UserInfo
import com.example.core.model.auth.Interest
import com.example.core.model.auth.Purpose
import com.example.core.repository.UserRepository
import com.example.core.datastore.session.LoginSessionStore
import com.example.data.api.ApiError
import com.example.data.api.ServerApi
import com.example.data.api.dto.auth.signup.email.SignUpEmailRequestDTO
import com.example.data.api.dto.auth.login.email.LoginRequestDTO
import com.example.data.preference.AuthPreference
import com.example.data.api.dto.user.DeleteUserRequestDTO
import com.example.data.api.withAuth
import com.example.data.api.dto.user.UpdateUserProfileRequestDTO
import com.example.data.api.withAuthRaw
import com.example.data.api.withErrorHandling
import com.example.data.api.withErrorHandlingRaw
import javax.inject.Inject
import com.example.data.mapper.SocialProfileMapper
import com.example.core.model.auth.*
import com.example.data.api.dto.auth.login.kakao.KakaoLoginRequestDTO
import com.example.data.api.dto.auth.login.kakao.KakaoLoginResponseDTO
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
    private val loginSessionStore: LoginSessionStore
) : UserRepository {

    // 인증 필요 API (withAuth)

    override suspend fun getUserInfo(userId: Long): UserInfo {

        val dto = serverApi.withAuth(authPreference) {
            getUserInfo(/*userId*/)
        }

        // 📍 서버 원본 데이터 확인
        Log.d(TAG, "📍 [서버 원본] purposes: ${dto.purposes}")
        Log.d(TAG, "📍 [서버 원본] interests: ${dto.interests}")


        val displayPurposes = dto.purposes.mapNotNull { serverKey ->
            Purpose.fromServerKey(serverKey)?.displayName ?: serverKey.also {
                Log.w(TAG, "알 수 없는 Purpose serverKey: $serverKey")
            }
        }
        val displayInterests = dto.interests.mapNotNull { serverKey ->
            Interest.fromServerKey(serverKey)?.displayName ?: serverKey.also {
                Log.w(TAG, "알 수 없는 Interest serverKey: $serverKey")
            }
        }

        // 📍 변환 후 데이터 확인
        Log.d(TAG, "📍 [변환 후] purposes: $displayPurposes")
        Log.d(TAG, "📍 [변환 후] interests: $displayInterests")

        return UserInfo(
            nickname = dto.nickName.orEmpty(),
            email = dto.email,
            gender = dto.gender.value,
            jobId = dto.job.id,
            jobName = dto.job.name,
            myLinku = dto.myLinku,
            myFolder = dto.myFolder,
            myAiLinku = dto.myAiLinku,
            purposes = displayPurposes,
            interests = displayInterests
        ).also { userInfo ->
            // 세션을 업데이트, 지현이가 편할 수 있게
            Log.d(TAG, "📍 [세션 저장] purposes: ${userInfo.purposes}")
            Log.d(TAG, "📍 [세션 저장] interests: ${userInfo.interests}")
            loginSessionStore.saveLogin(
                userId = userId,
                nickname = userInfo.nickname,
                email = userInfo.email,
                gender = userInfo.gender,
                jobId = userInfo.jobId,
                jobName = userInfo.jobName,
                myLinku = userInfo.myLinku,
                myFolder = userInfo.myFolder,
                myAiLinku = userInfo.myAiLinku,
                purposes = userInfo.purposes,
                interests = userInfo.interests

            )
            Log.d(TAG, "📍 [세션 저장 완료]")
        }
    }

    override suspend fun updateUserInfo(
        nickname: String,
        jobId: Long,
        purposes: List<String>,
        interests: List<String>
    ): Boolean {
        // 수정 시에도 한글 -> ENUM 변환 후 전송
        val mappedPurposes = purposes.mapNotNull { displayName ->
            Purpose.fromDisplayName(displayName)?.serverKey
        }
        val mappedInterests = interests.mapNotNull { displayName ->
            Interest.fromDisplayName(displayName)?.serverKey
        }

        val dto = UpdateUserProfileRequestDTO(
            nickname = nickname,
            jobId = jobId,
            purposes = mappedPurposes,
            interests = mappedInterests
        )


        val response = serverApi.withAuthRaw(authPreference) {
            updateUserInfo(dto) // ApiResponseString 반환
        }

        // response.isSuccess가 false라면 예외를 던지거나 false를 반환하도록 처리
        if (response.isSuccess != true) {
            throw ApiError.BusinessError(null, response.result ?: "수정 실패")
        }

        return true
    }

    // BaseResponse<withDrawalResultDTO> 반환 → withAuth
    override suspend fun deleteUser(reason: String): Boolean {
        val dto = DeleteUserRequestDTO(reason)
        serverApi.withAuth(authPreference) { deleteUser(dto) }
        return true
    }

    // BaseResponse<UserInfoDTO> 반환 → withAuth
    override suspend fun getNickname(userId: Long): String? {
        return try {
            val dto = serverApi.withAuth(authPreference) {
                getUserInfo(/*userId*/)
            }
            val nick = dto.nickName
            Log.d(TAG, "닉네임=$nick")
            nick?.takeIf { it.isNotBlank() }
        } catch (e: ApiError) {
            Log.e(TAG, "닉네임 가져오기 실패: ${e.message}")
            null
        }
    }

    // logout
    override suspend fun logout() {
        authPreference.clear()
        loginSessionStore.clear()
        //clearAuthData()
        Log.d(TAG, "로그아웃 완료")
    }


    companion object {
        private const val TAG = "UserRepository"
    }


    override suspend fun refreshUserInfo(userId: Long) {
        getUserInfo(userId)
        // getUserInfo 내부에서 sessionStore.saveLogin()
    }

    override suspend fun updateUserProfile(
        nickname: String,
        jobId: Long,
        jobName: String,
        purposes: List<String>,
        interests: List<String>
    ) {
        // 서버 DB 수정
        updateUserInfo(
            nickname = nickname,
            jobId = jobId,
            purposes = purposes,
            interests = interests
        )

        // 서버 성공 시 로컬 세션 즉시 반영
        loginSessionStore.updateProfile(
            nickname = nickname,
            jobId = jobId,
            jobName = jobName,
            purposes = purposes,
            interests = interests
        )
    }


}
