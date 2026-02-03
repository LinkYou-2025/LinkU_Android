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


    // ENUM 매핑s
    private val purposeMap = mapOf(
        "자기계발/정보 수집" to "SELF_DEVELOPMENT",
        "사이드 프로젝트/창업 준비" to "SIDE_PROJECT",
        "기타" to "OTHERS",
        "그냥 나중에 읽고싶은 글 저장" to "LATER_READING",
        "취업·커리어 준비" to "CAREER",
        "블로그/콘텐츠 작성 참고용" to "CREATION_REFERENCE",
        "인사이트 모으기" to "INSIGHTS",
        "업무자료 아카이빙" to "WORK",
        "학업/리포트 정리" to "STUDY"
    )

    private val interestMap = mapOf(
        "비즈니스/마케팅" to "BUSINESS",
        "학업/리포트 참고" to "STUDY",
        "커리어/채용" to "CAREER",
        "심리/자기계발" to "PSYCHOLOGY",
        "디자인/크리에이티브" to "DESIGN",
        "IT/개발" to "IT",
        "글쓰기/콘텐츠 작성" to "WRITING",
        "시사/트렌드" to "CURRENT_EVENTS",
        "스타트업/창업" to "STARTUP",
        "그냥 모아두고 싶은 글들" to "COLLECT",
        "사회/문화/환경" to "SOCIETY",
        "책/인사이트 요약" to "INSIGHTS"
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
            val isAvailable = response.isSuccess == true
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
            userId = response.userId?.toInt() ?: throw IllegalStateException("로그인 응답에 userId가 누락되었습니다."),
            accessToken = response.accessToken
                ?: throw ApiError.BusinessError(null, "accessToken이 없습니다"),
            refreshToken = response.refreshToken
                ?: throw ApiError.BusinessError(null, "refreshToken이 없습니다"),
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
        // UI에서 넘어온 한글 리스트를 ENUM 리스트로 변환
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
            accessToken = response.accessToken
               ?: throw ApiError.BusinessError(null, "accessToken이 없습니다"),
            refreshToken = response.refreshToken
                ?: throw ApiError.BusinessError(null, "refreshToken이 없습니다")
        )
    }

    // ApiResponseString 반환 → withErrorHandlingRaw
    override suspend fun requestTempPassword(email: String): Boolean {
        Log.d(TAG, "[임시PW 요청] email=${email.take(3)}***")
        // Log.d(TAG, "[임시PW 요청] email=$email") 보안 문제로 주석처리 단, 오류 발생시 사용해주세요.

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
        //val fullToken = authPreference.accessToken
        //Log.d(TAG, "📍 Full AccessToken: $fullToken")
        val dto = serverApi.withAuth(authPreference) {
            getUserInfo(userId)
        }

        // 📍 서버 원본 데이터 확인
        Log.d(TAG, "📍 [서버 원본] purposes: ${dto.purposes}")
        Log.d(TAG, "📍 [서버 원본] interests: ${dto.interests}")


        // 서버에서 온 ENUM(CAREER 등)을 UI용 한글("취업 커리어 준비")로 변환
        val displayPurposes = dto.purposes.map { reversePurposeMap[it] ?: it }
        val displayInterests = dto.interests.map { reverseInterestMap[it] ?: it }

        // 📍 변환 후 데이터 확인
        Log.d(TAG, "📍 [변환 후] purposes: $displayPurposes")
        Log.d(TAG, "📍 [변환 후] interests: $displayInterests")

        return UserInfo(
            nickname = dto.nickName.orEmpty(),
            email = dto.email,
            gender = dto.gender.value,
            jobId = dto.job.id.toLong(),
            jobName = dto.job.name,
            myLinku = dto.myLinku.toLong(),
            myFolder = dto.myFolder.toLong(),
            myAiLinku = dto.myAiLinku.toLong(),
            purposes = displayPurposes,
            interests = displayInterests
        ).also { userInfo ->
            // 세션을 업데이트, 지현이가 편할 수 있게
            Log.d(TAG, "📍 [세션 저장] purposes: ${userInfo.purposes}")
            Log.d(TAG, "📍 [세션 저장] interests: ${userInfo.interests}")
            sessionStore.saveLogin(
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
        val mappedPurposes = purposes.map { purposeMap[it] ?: it }
        val mappedInterests = interests.map { interestMap[it] ?: it }

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
        clearAuthData()
        Log.d(TAG, "로그아웃 완료")
    }
    private suspend fun clearAuthData() {
        // 중복 실행 방지함. 이미 로그아웃 상태면 아무것도 하지 않음
        if (authPreference.userId == null && !authPreference.isLoggedIn) return

        authPreference.clear()
        sessionStore.clear()
        Log.d(TAG, "모든 로컬 세션 데이터 삭제 완료")
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}
