package com.linku.core.usecase

import com.linku.core.model.LoginResult
import com.linku.core.model.TokenReissueResult
import com.linku.core.model.UserInfo
import com.linku.core.model.auth.Gender
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Job
import com.linku.core.model.auth.LoginType
import com.linku.core.model.auth.Purpose
import com.linku.core.model.auth.RecoverResult
import com.linku.core.model.auth.SignUpEmailResult
import com.linku.core.model.auth.UserSession
import com.linku.core.preference.AuthPreference
import com.linku.core.repository.AuthRepository
import com.linku.core.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

/** 저장소에 저장된 토큰 한 건을 기록하는 테스트용 값입니다. */
internal data class SavedTokens(
    val accessToken: String,
    val refreshToken: String?,
    val userId: Long,
    val loginType: LoginType,
)

/** 로그인 API로 전달된 요청 한 건을 기록하는 테스트용 값입니다. */
internal data class LoginRequest(
    val email: String,
    val password: String,
    val deviceId: String,
    val deviceType: String,
)

/**
 * 호출을 기록하고 미리 정한 결과를 돌려주는 테스트용 [AuthRepository]입니다.
 *
 * @property loginResponse 이메일 로그인에 돌려줄 결과
 */
internal class FakeAuthRepository(
    private val loginResponse: Result<LoginResult> = Result.failure(IllegalStateException("login 결과가 지정되지 않았습니다.")),
) : AuthRepository {
    val loginRequests = mutableListOf<LoginRequest>()

    /** `메서드명:인자` 형태로 기록한 호출 목록입니다. */
    val calls = mutableListOf<String>()

    var checkNicknameResult: Result<Unit> = Result.success(Unit)
    var signUpResult: Result<SignUpEmailResult> =
        Result.success(SignUpEmailResult(userId = 1L, createdAt = "now", accessToken = "a", refreshToken = "r"))
    var sendEmailCodeResult: Result<Unit> = Result.success(Unit)
    var verifyEmailCodeResult: Result<Unit> = Result.success(Unit)
    var sendPasswordResetResult: Result<Unit> = Result.success(Unit)
    var kakaoResult: Result<LoginResult> = loginResponse
    var googleResult: Result<LoginResult> = loginResponse
    var completeProfileResult: Result<Boolean> = Result.success(true)

    override val sessionState: Flow<UserSession> = flowOf(UserSession())

    override suspend fun login(
        email: String,
        password: String,
        deviceId: String,
        deviceType: String,
    ): Result<LoginResult> {
        loginRequests += LoginRequest(email, password, deviceId, deviceType)
        return loginResponse
    }

    override suspend fun checkNickname(nickname: String): Result<Unit> {
        calls += "checkNickname:$nickname"
        return checkNicknameResult
    }

    override suspend fun signUpWithEmail(
        nickname: String,
        email: String,
        password: String,
        gender: Gender,
        jobId: Int,
        purposeList: List<Purpose>,
        interestList: List<Interest>,
        termsMap: Map<String, Boolean>,
    ): Result<SignUpEmailResult> {
        calls += "signUp:$nickname,$email,$password,$gender,$jobId,${purposeList.size},${interestList.size},$termsMap"
        return signUpResult
    }

    override suspend fun sendEmailCode(email: String): Result<Unit> {
        calls += "sendEmailCode:$email"
        return sendEmailCodeResult
    }

    override suspend fun verifyEmailCode(email: String, code: String): Result<Unit> {
        calls += "verifyEmailCode:$email,$code"
        return verifyEmailCodeResult
    }

    override suspend fun reissue(refreshToken: String): Result<TokenReissueResult> = unused()

    override suspend fun completeSocialProfile(
        socialToken: String,
        nickName: String,
        gender: Gender,
        job: Job,
        purposes: List<Purpose>,
        interests: List<Interest>,
        termsMap: Map<String, Boolean>,
        loginType: LoginType,
    ): Result<Boolean> {
        calls += "completeSocialProfile:$socialToken,$nickName,$gender,$job,${purposes.size},${interests.size},$termsMap,$loginType"
        return completeProfileResult
    }

    override suspend fun loginWithKakao(token: String): Result<LoginResult> {
        calls += "kakao:$token"
        return kakaoResult
    }

    override suspend fun loginWithGoogle(token: String): Result<LoginResult> {
        calls += "google:$token"
        return googleResult
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        calls += "sendPasswordReset:$email"
        return sendPasswordResetResult
    }
}

/**
 * 복구 API 호출을 기록하는 테스트용 [UserRepository]입니다.
 *
 * @property recoverResponse 복구 API에 돌려줄 결과
 * @property onRecover 복구 API가 호출될 때 실행할 동작
 */
internal class FakeUserRepository(
    private val recoverResponse: Result<RecoverResult>,
    private val onRecover: () -> Unit = {},
) : UserRepository {
    override val cachedUserInfo: StateFlow<UserInfo?> = MutableStateFlow(null)

    override suspend fun recoverUser(): Result<RecoverResult> {
        onRecover()
        return recoverResponse
    }

    override suspend fun deleteUser(reason: String): Result<Unit> = unused()
    override suspend fun getUserInfo(userId: Long): Result<UserInfo> = unused()
    override suspend fun updateUserInfo(
        nickname: String,
        jobId: Long,
        purposes: List<String>,
        interests: List<String>,
    ): Result<Unit> = unused()

    override suspend fun logout(): Result<Unit> = unused()
    override suspend fun getNickname(): Result<String> = unused()
    override suspend fun updateMarketingTerms(): Result<Unit> = unused()
    override suspend fun checkMarketingTermsAgreed(): Result<Boolean> = unused()
}

/**
 * 토큰 저장과 정리를 기록하는 테스트용 [AuthPreference]입니다.
 *
 * @property deviceId 기기 ID
 * @property deviceType 기기 종류
 * @property recentLoginType 마지막 로그인 수단
 */
internal class FakeAuthPreference(
    private val deviceId: String = "device",
    private val deviceType: String = "PHONE",
    private val recentLoginType: LoginType = LoginType.NONE,
    loggedIn: Flow<Boolean> = flowOf(false),
) : AuthPreference {
    val savedTokens = mutableListOf<SavedTokens>()
    val updatedAccessTokens = mutableListOf<Pair<String, String?>>()
    var cleared = false

    override val isLoggedIn: Flow<Boolean> = loggedIn
    override val sessionState: Flow<UserSession> = flowOf(UserSession())

    override suspend fun getDeviceId(): String = deviceId
    override suspend fun getDeviceType(): String = deviceType
    override suspend fun getLoginType(): LoginType = recentLoginType

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String?,
        userId: Long,
        loginType: LoginType,
    ) {
        savedTokens += SavedTokens(accessToken, refreshToken, userId, loginType)
    }

    override suspend fun updateAccessToken(accessToken: String, refreshToken: String?) {
        updatedAccessTokens += accessToken to refreshToken
    }

    override suspend fun clear() {
        cleared = true
    }

    override suspend fun initDeviceInfo(deviceId: String, deviceType: String) = unused()
    override suspend fun getAccessToken(): String? = unused()
    override suspend fun getRefreshToken(): String? = unused()
    override suspend fun getUserId(): Long? = unused()
    override suspend fun updateLoginType(loginType: LoginType) = unused()
    override suspend fun getCachedNickname(): String? = unused()
    override suspend fun saveNickname(nickname: String) = unused()
    override suspend fun wasClipboardLinkPresented(url: String, copiedAtMillis: Long, userId: Long): Boolean = unused()
    override suspend fun savePresentedClipboardLink(url: String, copiedAtMillis: Long, userId: Long): Boolean = unused()
    override suspend fun wasLinkSavedAfterClipboardCopy(url: String, copiedAtMillis: Long, userId: Long): Boolean = unused()
    override suspend fun saveLastSavedLinkUrl(url: String, savedAtMillis: Long, userId: Long): Boolean = unused()
}

/** 이 테스트에서 사용하지 않아야 하는 호출입니다. */
internal fun unused(): Nothing = error("이 테스트에서 사용하지 않는 호출입니다.")
