package com.example.data.implementation.repository

import android.util.Log
import com.example.core.model.LoginResult
import com.example.core.model.TokenReissueResult
import com.example.core.model.auth.*
import com.example.core.repository.AuthRepository
import com.example.core.datastore.session.LoginSessionStore
import com.example.data.api.ApiError
import com.example.data.api.ServerApi
import com.example.data.api.dto.auth.login.email.LoginRequestDTO
import com.example.data.api.dto.auth.login.kakao.KakaoLoginRequestDTO
import com.example.data.api.dto.auth.login.kakao.KakaoLoginResponseDTO
import com.example.data.api.dto.auth.signup.email.SignUpEmailRequestDTO
import com.example.data.api.withErrorHandling
import com.example.data.api.withErrorHandlingRaw
import com.example.data.mapper.SocialProfileMapper
import com.example.data.preference.AuthPreference
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
    private val loginSessionStore: LoginSessionStore
) : AuthRepository {
    override val sessionState: Flow<LoginSessionStore.SessionSnapshot>
        get() = loginSessionStore.session //레포지토리가 세션 Flow를 책임질 수 있도록 수정함.

    // checkNickname - ApiResponseString 반환하므로 withErrorHandlingRaw 사용
    override suspend fun checkNickname(nickname: String){
        Log.d(TAG, "[API 호출] checkNickname nickname=$nickname")
        serverApi.withErrorHandling { checkNickname(nickname) }
        // 성공이면 반환, 실패 -> ApiError throw
    }

    override suspend fun login(email: String, password: String): LoginResult {
        Log.d(TAG, "[로그인 시도]")

        // API 호출 및 결과 수신
        val emailSignUpResponse = serverApi.withErrorHandling {
            signIn(LoginRequestDTO(email, password))
        }

        Log.d(TAG, "[로그인 성공]")

        // INACTIVE인데 inactiveDate가 null이면 서버 오류 -> 백엔드 서원이의 요청임.
        if (emailSignUpResponse.status == "INACTIVE" && emailSignUpResponse.inactiveDate == null) {
            throw ApiError.BusinessError(null, "INACTIVE 상태인데 inactiveDate가 없습니다")
        }

        return LoginResult(
            userId = emailSignUpResponse.userId,
            accessToken = emailSignUpResponse.accessToken,
            refreshToken = emailSignUpResponse.refreshToken,
            status = emailSignUpResponse.status.ifBlank { "ACTIVE" }, // 빈 문자열이면 ACTIVE
            inactiveDate = emailSignUpResponse.inactiveDate  // String? 그대로
        )
    } //TODO :뷰모델에서 어떤 에러인지 판단하도록 수정하기.
    //여기서부터 낼 리펙토링하기!
    override suspend fun signUpWithEmail(
        nickname: String,
        email: String,
        password: String,
        gender: Int,
        jobId: Int,
        purposeList: List<Purpose>, // 뷰모델이 enum으로 넘김.
        interestList: List<Interest> // 뷰모델이 enum으로 넘김.
    ) : SignUpEmailResult {
        Log.d(TAG, "[회원가입 시도]")

        require(purposeList.isNotEmpty()) { "purposeList는 비어 있을 수 없습니다." }
        require(interestList.isNotEmpty()) { "interestList는 비어 있을 수 없습니다." }

        val signUpEmailRequest = SignUpEmailRequestDTO(
            nickName = nickname,
            email = email,
            password = password,
            gender = gender,
            jobId = jobId,
            purposeList = purposeList.map { it.serverKey },   // enum → serverKey(it은 Purpose.NETWORKING -> serverKey = "NETWORKING")으로 작동.
            interestList = interestList.map { it.serverKey }  // enum → serverKey
        )

        val response = serverApi.withErrorHandling { signUpWithEmail(signUpEmailRequest) }
        // ↑ SignUpEmailResponseDTO 타입으로 자동 추론됨
        Log.d(TAG, "[회원가입 성공]")

        return SignUpEmailResult(
            userId = response.userId,
            createdAt = response.createdAt
        )

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

    companion object {
        private const val TAG = "UserRepository"
    }

    // 소셜로 회원가입 이후 프로필 정보 입력 받는 api
    override suspend fun completeSocialProfile(
        socialToken: String,
        nickName: String,
        gender: Gender,
        job: Job,
        purposes: List<Purpose>,
        interests: List<Interest>
    ): Boolean {

        val request = SocialProfileMapper.toRequest(
            nickName = nickName,
            gender = gender,
            job = job,
            purposes = purposes,
            interests = interests
        )

        serverApi.withErrorHandling {
            completeSocialProfile(
                authorization = "Bearer $socialToken",
                body = request
            )
        }

        Log.d(TAG, "[소셜 프로필 완료] 성공")
        return true
    }

    //tag 상수로 통일하기. 지금 serverApi.withErrorHandling  적극적으로 리펙토링하기.

    override suspend fun loginWithKakao(token: String): LoginResult {
        Log.d("UserRepositoryImpl", "loginWithKakao token: $token")
        //Log.d(TAG, "loginWithKakao start")
        val kakaoResponse: KakaoLoginResponseDTO

        try{
            Log.d("UserRepositoryImpl", "loginWithKakao try")
            // Log.d(TAG, "loginWithKakao try")
            kakaoResponse = serverApi.withErrorHandling {
                kakaoLogin(KakaoLoginRequestDTO(token = token))
            }

            Log.d("UserRepositoryImpl", "loginWithKakao response: $kakaoResponse")
            //Log.d(TAG, "loginWithKakao success: userId=${kakaoResponse.userId}, status=${kakaoResponse.status}")
        } catch (e: Exception){
            Log.e("UserRepositoryImpl", "loginWithKakao error: $e")
            //Log.e(TAG, "loginWithKakao error", e)
            throw e
        }

        Log.d("UserRepositoryImpl", "loginWithKakao return: $kakaoResponse")

        return LoginResult( // run을 간결하게 수정함.
            userId = kakaoResponse.userId,
            accessToken = kakaoResponse.accessToken,
            refreshToken = kakaoResponse.refreshToken,
            status = kakaoResponse.status ?: "",
            inactiveDate = ""  // 카카오 로그인엔 inactiveDate 없으니까 빈 문자열
        )

    }
}