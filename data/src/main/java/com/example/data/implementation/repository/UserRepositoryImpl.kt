package com.example.data.implementation.repository

import android.util.Log
import com.example.core.model.LoginResult
import com.example.core.model.TokenReissueResult
import com.example.core.model.UserInfo
import com.example.core.repository.UserRepository
import com.example.core.session.SessionStore
import com.example.data.api.ApiError
import com.example.data.api.ServerApi
import com.example.data.api.UserApi
import com.example.data.api.dto.server.JoinDTO
import com.example.data.api.dto.server.LoginRequestDTO
import com.example.data.preference.AuthPreference
import com.example.data.api.dto.server.DeleteReasonDTO
import com.example.data.api.dto.server.UserInfoDTO
import com.example.data.api.withAuth
import com.example.data.api.withAuthHeaderRaw
import com.example.data.api.dto.server.TempPasswordRequestDTO
import com.example.data.api.dto.server.UpdateProfileDTO
import com.example.data.api.withAuthRaw
import com.example.data.api.withErrorHandling
import com.example.data.api.withErrorHandlingRaw
import retrofit2.HttpException
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
    private val sessionStore: SessionStore
) : UserRepository {


    // ENUM 매핑
    private val purposeMap = mapOf(
        "취업·커리어 준비" to "CAREER",
        "학업/리포트 정리" to "STUDY",
        "업무자료 아카이빙" to "WORK",
        "사이드 프로젝트/창업 준비" to "SIDE_PROJECT",
        "자기계발/정보 수집" to "SELF_DEVELOPMENT",
        "그냥 나중에 읽고 싶은 글 저장" to "LATER_READING",
        "인사이트 모으기" to "INSIGHTS",
        "블로그/콘텐츠 작성 참고용" to "CREATION_REFERENCE",
        "기타" to "OTHERS"
    )

    private val interestMap = mapOf(
        "비즈니스/마케팅" to "BUSINESS",
        "IT/개발" to "IT",
        "디자인/크리에이티브" to "DESIGN",
        "심리/자기계발" to "PSYCHOLOGY",
        "커리어/채용" to "CAREER",
        "시사/트렌드" to "CURRENT_EVENTS",
        "학업/리포트 참고" to "STUDY",
        "스타트업/창업" to "STARTUP",
        "사회/문화/환경" to "SOCIETY",
        "글쓰기/콘텐츠 작성" to "WRITING",
        "책/인사이트 요약" to "INSIGHTS",
        "그냥 모아두고 싶은 글들" to "COLLECT"
    )

    private val reversePurposeMap = purposeMap.entries.associate { it.value to it.key }
    private val reverseInterestMap = interestMap.entries.associate { it.value to it.key }


    // checkNickname - ApiResponseString 반환하므로 withErrorHandlingRaw 사용
    override suspend fun checkNickname(nickname: String): Boolean {
        Log.d(TAG, "[API 호출] checkNickname nickname=$nickname")

        return try {
            val response = serverApi.withErrorHandlingRaw {
                checkNickname(nickname)  // ApiResponseString 반환
            }
            // response가 ApiResponseString이면 그에 맞게 처리
            val isAvailable = response.result?.contains("사용 가능") == true
            Log.d(TAG, "[닉네임 API 응답] 사용가능=$isAvailable")
            isAvailable
        } catch (e: ApiError) {
            Log.e(TAG, "[닉네임 API 오류] ${e.message}")
            false
        }
    }

    override suspend fun login(email: String, password: String): LoginResult {
        Log.d(TAG, "[로그인 시도]")

        // API 호출 및 결과 수신
        val response = serverApi.withErrorHandling {
            signIn(LoginRequestDTO(email, password))
        }

        Log.d(TAG, "[로그인 성공]")

        return LoginResult(
            userId = response.userId?.toInt() ?: -1,
            accessToken = response.accessToken ?: "",
            refreshToken = response.refreshToken ?: "",
            status = response.status ?: "",
            inactiveDate = response.inactiveDate?.toString()
        )
    }

    override suspend fun signUp(
        nickname: String,
        email: String,
        password: String,
        gender: Int,
        jobId: Int,
        purposeList: List<String>,
        interestList: List<String>
    ): Boolean {
        val safePurposeList = purposeList.map { purposeMap[it] ?: it }
        val safeInterestList = interestList.map { interestMap[it] ?: it }

        require(safePurposeList.isNotEmpty()) { "purposeList는 비어 있을 수 없습니다." }
        require(safeInterestList.isNotEmpty()) { "interestList는 비어 있을 수 없습니다." }

        val dto = JoinDTO(
            nickName = nickname,
            email = email,
            password = password,
            gender = gender,
            jobId = jobId,
            purposeList = safePurposeList,
            interestList = safeInterestList
        )

        serverApi.withErrorHandling { signUp(dto) }
        Log.d(TAG, "[회원가입 성공]")
        return true
    }

    // ApiResponseString 반환 → withErrorHandlingRaw
    override suspend fun sendEmailCode(email: String, code: String): Boolean {
        return try {
            val response = serverApi.withErrorHandlingRaw {
                sendVerificationEmail(email, code)
            }
            response.isSuccess == true
        } catch (e: ApiError) {
            Log.e(TAG, "[이메일 코드 전송 실패] ${e.message}")
            false
        }
    }

    // BaseResponse<EmailVerificationResponse> 반환 → withErrorHandling
    override suspend fun verifyEmailCode(email: String, code: String): Boolean {
        return try {
            serverApi.withErrorHandling { checkVerificationEmail(email, code) }
            true
        } catch (e: ApiError) {
            Log.e(TAG, "[이메일 코드 검증 실패] ${e.message}")
            false
        }
    }

    // BaseResponse<TokenPair> 반환 → withErrorHandling
    override suspend fun reissue(refreshToken: String): TokenReissueResult {
        Log.d(TAG, "[토큰 재발급 시도]")

        val response = serverApi.withErrorHandling {
            reissue(refreshToken)
        }

        Log.d(TAG, "[토큰 재발급 성공]")

        return TokenReissueResult(
            accessToken = response.accessToken ?: "",
            refreshToken = response.refreshToken ?: ""
        )
    }

    // ApiResponseString 반환 → withErrorHandlingRaw
    override suspend fun requestTempPassword(email: String): Boolean {
        Log.d(TAG, "[임시PW 요청] email=$email")

        return try {
            val response = serverApi.withErrorHandlingRaw {
                requestTempPassword(email)
            }
            val success = response.isSuccess == true
            Log.d(TAG, "[임시PW 요청 결과] success=$success")
            success
        } catch (e: ApiError) {
            Log.e(TAG, "[임시PW 요청 실패] ${e.message}")
            false
        }
    }


    // 인증 필요 API (withAuth)

    override suspend fun getUserInfo(userId: Long): UserInfo {
        val dto = serverApi.withAuth(authPreference) {
            getUserInfo(userId)
        }

        val displayPurposes = dto.purposes.map { reversePurposeMap[it] ?: it }
        val displayInterests = dto.interests.map { reverseInterestMap[it] ?: it }

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
        )
    }

    override suspend fun updateUserInfo(
        nickname: String,
        jobId: Long,
        purposes: List<String>,
        interests: List<String>
    ): Boolean {
        val mappedPurposes = purposes.mapNotNull { purposeMap[it] }
        val mappedInterests = interests.mapNotNull { interestMap[it] }

        val dto = UpdateProfileDTO(
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
        val dto = DeleteReasonDTO(reason)
        serverApi.withAuth(authPreference) { deleteUser(dto) }
        return true
    }

    // BaseResponse<UserInfoDTO> 반환 → withAuth
    override suspend fun getNickname(userId: Long): String? {
        return try {
            val dto = serverApi.withAuth(authPreference) {
                getUserInfo(userId)
            }
            val nick = dto.nickName
            Log.d(TAG, "닉네임=$nick")
            nick?.takeIf { it.isNotBlank() }
        } catch (e: ApiError) {
            Log.e(TAG, "닉네임 가져오기 실패: ${e.message}")
            null
        }
    }

    // logout? - TODO : 지현아... 세션으로 마이페이지 해야할 듯...
    override suspend fun logout() {
        runCatching {
            serverApi.withAuthRaw(authPreference) {
                logout()
            }
        }.onFailure { e ->
            Log.w(TAG, "logout API failed: ${e.message}")
        }

        clearAuthData()
    }

    private suspend fun clearAuthData() {
        authPreference.clear()   // 토큰 비우기
        sessionStore.clear()     // 유저정보 비우기
        Log.d(TAG, "모든 로컬 세션 데이터 삭제 완료")
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}
