package com.example.data.implementation.repository

import android.util.Log
import com.example.core.model.LoginResult
import com.example.core.model.UserInfo
import com.example.core.repository.UserRepository
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
import retrofit2.HttpException
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference
) : UserRepository {

    // 서버 ENUM 매핑 (클래스 내부에 추가)
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

    // ENUM -> 한글 (역매핑)
    private val reversePurposeMap = purposeMap.entries.associate { it.value to it.key }
    private val reverseInterestMap = interestMap.entries.associate { it.value to it.key }

    override suspend fun checkNickname(nickname: String): Boolean {
        return try {
            Log.d("UserRepository", " [API 호출] checkNickname nickname=$nickname")

            val response = userApi.checkNickname(nickname)

            Log.d("UserRepository", " [닉네임 API 응답] isSuccess=${response.isSuccess}, message=${response.message}, result=${response.result}")

            //  서버 메시지에 따라 사용 가능 여부 결정
            response.isSuccess == true && response.result?.contains("사용 가능") == true
        } catch (e: HttpException) {
            Log.e("UserRepository", " [닉네임 API 오류] code=${e.code()} msg=${e.message()}")
            false
        } catch (e: Exception) {
            Log.e("UserRepository", " [닉네임 API 호출 실패]", e)
            false
        }
    }
    override suspend fun login(email: String, password: String): LoginResult {
        val response = userApi.signIn(LoginRequestDTO(email, password))
        val result = response.result ?: throw IllegalStateException("로그인 실패: ${response.message}")

        // userId 저장
        authPreference.userId = result.userId?.toLong() ?: -1L

        // access/refresh 저장 (널/빈값 방어)
        result.accessToken?.takeIf { it.isNotBlank() }?.let { authPreference.accessToken = it }
        result.refreshToken?.takeIf { it.isNotBlank() }?.let { authPreference.refreshToken = it } // ⬅️ 추가

        // ⬇️ 리플렉션으로 refresh 찾던 블럭은 전부 제거하세요.

        // (이하 기존 반환 로직 유지)
        return LoginResult(
            userId = result.userId?.toInt() ?: -1,
            token = result.accessToken ?: "",
            status = result.status ?: "",
            inactiveDate = result.inactiveDate?.toString()
        )
    }
//    override suspend fun login(email: String, password: String): LoginResult {
//        val response = userApi.signIn(LoginRequestDTO(email, password))
//        Log.d("UserRepository", "[로그인 응답] isSuccess=${response.isSuccess}, message=${response.message}, result=${response.result}")
//        val result = response.result ?: throw IllegalStateException("로그인 실패: ${response.message}")
//        authPreference.userId = result.userId?.toLong() ?: -1L //로그인할 때, userId 저장하기! -> 추후 큐레이션에 닉네임 표시용.
//
//        //  accessToken 저장 (if 사용 → 타입 추론 오류 방지)
//        val accessToken: String? = result.accessToken
//        if (accessToken != null) {
//            // AuthPreference에 맞는 실제 메서드명으로 교체 필요
//            authPreference.accessToken = accessToken
//        }
//        //inactiveDate(String?) → OffsetDateTime? 안전 변환
//        val parsedInactiveDate: OffsetDateTime? = try {
//            result.inactiveDate?.let { OffsetDateTime.parse(it) }
//        } catch (e: DateTimeParseException) {
//            Log.w("UserRepository", "inactiveDate 파싱 실패 → ${result.inactiveDate}")
//            null
//        }
//
//        return LoginResult(
//            userId = result.userId?.toInt() ?: -1,
//            token = result.accessToken ?: "",
//            status = result.status ?: "",
//            inactiveDate = result.inactiveDate?.toString()
//        )
//    }
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

        // 회원가입은 토큰 없이 호출해야 함
        val response = userApi.signUp(dto)
        Log.d("UserRepository", " [회원가입 응답] isSuccess=${response.isSuccess} message=${response.message}")

        return response.isSuccess == true
    }
//    override suspend fun signUp(
//        nickname: String,
//        email: String,
//        password: String,
//        gender: Int,
//        jobId: Int,
//        purposeList: List<String>,
//        interestList: List<String>
//    ): Boolean {
//        // gender & jobId 유효성 체크
//        require(gender in 1..2) { "gender 값이 잘못되었습니다. (1=남성, 2=여성)" }
//        require(jobId in 1..6) { "jobId 값이 잘못되었습니다. (1~6)" }
//
//        // null 대신 항상 빈 리스트 전달
//        val safePurposeList = if (purposeList.isEmpty()) emptyList() else purposeList
//        val safeInterestList = if (interestList.isEmpty()) emptyList() else interestList
//
//        val dto = JoinDTO(
//            nickName = nickname,
//            email = email,
//            password = password,
//            gender = gender,
//            jobId = jobId,
//            purposeList = safePurposeList,
//            interestList = safeInterestList
//        )
//
//        val token = "Bearer ${authPreference.accessToken}" //  JWT 토큰 추가
//        Log.d("UserRepository", " [회원가입 요청] token=$token dto=$dto")
//
//        // userApi.signUp에 토큰 전달 필요 → UserApi 수정 필요
//        val response = userApi.signUp(dto, token)
//
//        Log.d("UserRepository", " [회원가입 응답] isSuccess=${response.isSuccess} message=${response.message}")
//        return response.isSuccess == true
//    }

//    override suspend fun signUp(
//        nickname: String,
//        email: String,
//        password: String,
//        gender: Int,
//        jobId: Int,
//        purposeList: List<String>,
//        interestList: List<String>
//    ): Boolean {
//        val dto = JoinDTO(
//            nickName = nickname,
//            email = email,
//            password = password,
//            gender = gender,
//            jobId = jobId,
//            purposeList = purposeList,
//            interestList = interestList
//        )
//
//        val response = userApi.signUp(dto)
//        return response.isSuccess == true   // Boolean? → Boolean 변환
//    }

    override suspend fun sendEmailCode(email: String, code: String): Boolean {
        val response = userApi.sendVerificationEmail(email, code)
        return response.isSuccess == true   // Boolean? → Boolean 변환
    }

    override suspend fun verifyEmailCode(email: String, code: String): Boolean {
        val response = userApi.checkVerificationEmail(email, code)
        return response.isSuccess == true   // Boolean? → Boolean 변환
    }

    //inactiveDate 추가.

    override suspend fun deleteUser(reason: String): Boolean {
        val dto = DeleteReasonDTO(reason)
        val response = userApi.deleteUser(dto)
        return response.isSuccess == true
    }

    // 마이페이지 조회
    override suspend fun getUserInfo(userId: Long): UserInfo {
        // val response = userApi.withAuth(authPreference) { getUserInfo(userId) }
        val response = userApi.getUserInfo(userId)

        val dto: UserInfoDTO = response.result
            ?: throw IllegalStateException("마이페이지 조회 실패: ${response.message}")

        // DTO -> 도메인 매핑을 여기서 바로 처리
        val nick = dto.nickname ?: dto.nickName ?: ""   // ← Fallback 추가

        // 서버에서 받은 enum 코드 → 화면 한글 라벨로 변환
        val displayPurposes = dto.purposes.map { reversePurposeMap[it] ?: it }
        val displayInterests = dto.interests.map { reverseInterestMap[it] ?: it }

        // DTO -> 도메인 매핑을 여기서 바로 처리
        return UserInfo(
//            nickname  = dto.nickname,
            nickname  = nick,
            email     = dto.email,
            gender    = dto.gender.value,   // "MALE" | "FEMALE"
            jobId     = dto.job.id,
            jobName   = dto.job.name,
            myLinku   = dto.myLinku,
            myFolder  = dto.myFolder,
            myAiLinku = dto.myAiLinku,
            purposes  = displayPurposes,
            interests = displayInterests
        )
    }

    // 마이페이지 계정 정보 수정
    override suspend fun updateUserInfo(
        nickname: String,
        jobId: Long,
        purposes: List<String>,
        interests: List<String>
    ): Boolean {
        // 한글 → ENUM 코드로 변환
        val mappedPurposes = purposes.mapNotNull { purposeMap[it] }
        val mappedInterests = interests.mapNotNull { interestMap[it] }

        val dto = UpdateProfileDTO(
            nickname = nickname,
            jobId = jobId,
            purposes = mappedPurposes,
            interests = mappedInterests
        )

        val res = userApi.updateUserInfo(dto)
        return res.isSuccess == true
    }

    // 로그아웃
    override suspend fun logout() {
        // 서버 로그아웃: 401이면 refresh 후 1회 재시도
        runCatching {
            serverApi.withAuthHeaderRaw(authPreference) { _ ->
                // logout 이 suspend fun logout(): Unit 인 경우
                logout()
            }
        }.onFailure { e ->
            Log.w("UserRepository", "logout API failed: ${e.message}")
            // 서버 실패여도 로컬 세션은 아래에서 정리
        }

        // 로컬 세션은 항상 정리
        authPreference.accessToken = null
        authPreference.refreshToken = null
        authPreference.userId = null
    }

    //  닉네임 전용 메서드로 분리
    override suspend fun getNickname(userId: Long): String? {
        return try {
            val res = userApi.getUserInfo(userId) // BaseResponse<UserInfoDTO>
            // 서버 DTO 필드명 대응 (nickname 혹은 nickName)
            val nick = res.result?.nickname ?: res.result?.nickName
            Log.d("UserRepository", "닉네임=$nick")
            nick?.takeIf { it.isNotBlank() }
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 500) null else throw e
        } catch (e: Exception) {
            Log.e("UserRepository", "닉네임 가져오기 실패", e)
            null
        }
    }

    //유저 비밀번호 재설정
    override suspend fun requestTempPassword(email: String): Boolean {
        return try {
            val res = userApi.requestTempPassword(email) // ← @Query 호출
            Log.d("UserRepository", "[임시PW 응답] success=${res.isSuccess} code=${res.code} msg=${res.message}")
            res.isSuccess == true
        } catch (e: HttpException) {
            Log.e("UserRepository", "[임시PW API 오류] code=${e.code()} msg=${e.message()}")
            false
        } catch (e: Exception) {
            Log.e("UserRepository", "[임시PW 호출 실패]", e)
            false
        }
    }
}