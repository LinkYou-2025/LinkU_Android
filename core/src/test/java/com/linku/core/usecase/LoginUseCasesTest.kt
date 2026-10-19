package com.linku.core.usecase

import com.linku.core.error.ApiError
import com.linku.core.model.LoginResult
import com.linku.core.model.TokenReissueResult
import com.linku.core.model.UserInfo
import com.linku.core.model.auth.AutoLoginState
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 이메일 로그인, 계정 복구, 자동 로그인, 인증 정보 정리 유스케이스가 기존 로그인 화면의 동작과 같은
 * 저장 규칙을 지키는지 검증합니다.
 */
class LoginUseCasesTest {

    /** 정상 로그인 응답이면 정식 세션(토큰, 사용자 ID, 이메일 로그인 방식)을 저장하고 임시 저장은 하지 않는지 검증합니다. */
    @Test
    fun `active account saves a full session`() = runTest {
        val preference = FakeAuthPreference()
        val result = loginResult(status = "ACTIVE")
        val useCase = LoginWithEmailUseCase(FakeAuthRepository(Result.success(result)), preference)

        val outcome = useCase("a@b.com", "pw")

        assertEquals(EmailLoginOutcome.Authenticated(result), outcome)
        assertEquals(listOf(SavedTokens("access", "refresh", 7L, LoginType.EMAIL)), preference.savedTokens)
        assertTrue(preference.updatedAccessTokens.isEmpty())
    }

    /** 탈퇴 유예기간 계정이면 정식 세션을 저장하지 않고 복구 API용 토큰만 임시 저장하는지 검증합니다. */
    @Test
    fun `inactive account stores only a temporary recovery token`() = runTest {
        val preference = FakeAuthPreference()
        val result = loginResult(status = "INACTIVE")
        val useCase = LoginWithEmailUseCase(FakeAuthRepository(Result.success(result)), preference)

        val outcome = useCase("a@b.com", "pw")

        assertEquals(EmailLoginOutcome.RecoveryRequired(result), outcome)
        assertTrue(preference.savedTokens.isEmpty())
        assertEquals(listOf("access" to "refresh"), preference.updatedAccessTokens)
    }

    /** 로그인 요청에 저장된 기기 정보가 그대로 전달되는지 검증합니다. */
    @Test
    fun `login sends the stored device info`() = runTest {
        val repository = FakeAuthRepository(Result.success(loginResult(status = "ACTIVE")))
        val useCase = LoginWithEmailUseCase(repository, FakeAuthPreference(deviceId = "dev-1", deviceType = "PHONE"))

        useCase("a@b.com", "pw")

        assertEquals(listOf(LoginRequest("a@b.com", "pw", "dev-1", "PHONE")), repository.loginRequests)
    }

    /** 로그인 API가 실패하면 예외를 그대로 던지고 아무 것도 저장하지 않는지 검증합니다. */
    @Test
    fun `login failure is rethrown without saving anything`() = runTest {
        val preference = FakeAuthPreference()
        val error = ApiError.User.LoginFailed("bad credentials")
        val useCase = LoginWithEmailUseCase(FakeAuthRepository(Result.failure(error)), preference)

        val thrown = assertThrows(ApiError.User.LoginFailed::class.java) {
            kotlinx.coroutines.runBlocking { useCase("a@b.com", "wrong") }
        }

        assertSame(error, thrown)
        assertTrue(preference.savedTokens.isEmpty())
        assertTrue(preference.updatedAccessTokens.isEmpty())
    }

    /** 복구에 성공하면 복구 API가 내려준 새 토큰과 로그인 응답의 사용자 ID로 정식 세션을 저장하는지 검증합니다. */
    @Test
    fun `recover success saves the new tokens with the pending user id`() = runTest {
        val preference = FakeAuthPreference()
        val useCase = RecoverAccountUseCase(
            FakeUserRepository(Result.success(RecoverResult("new-access", "new-refresh"))),
            preference,
        )

        val outcome = useCase { 42L }

        assertEquals(RecoverAccountOutcome.Recovered, outcome)
        assertEquals(listOf(SavedTokens("new-access", "new-refresh", 42L, LoginType.EMAIL)), preference.savedTokens)
        assertFalse(preference.cleared)
    }

    /** 복구 응답을 기다리는 동안 모달이 닫혀 이어서 처리할 정보가 없으면 아무것도 저장하지 않는지 검증합니다. */
    @Test
    fun `recover success without a pending login is ignored`() = runTest {
        val preference = FakeAuthPreference()
        val useCase = RecoverAccountUseCase(
            FakeUserRepository(Result.success(RecoverResult("new-access", "new-refresh"))),
            preference,
        )

        val outcome = useCase { null }

        assertEquals(RecoverAccountOutcome.Ignored, outcome)
        assertTrue(preference.savedTokens.isEmpty())
        assertFalse(preference.cleared)
    }

    /** 복구에 실패하면 임시 저장된 토큰을 정리하고 실패 원인을 돌려주는지 검증합니다. */
    @Test
    fun `recover failure clears the temporary tokens`() = runTest {
        val preference = FakeAuthPreference()
        val error = IllegalStateException("grace period expired")
        val useCase = RecoverAccountUseCase(FakeUserRepository(Result.failure(error)), preference)

        val outcome = useCase { 42L }

        assertEquals(RecoverAccountOutcome.Failed(error), outcome)
        assertTrue(preference.cleared)
        assertTrue(preference.savedTokens.isEmpty())
    }

    /** 복구 응답이 도착한 뒤에 보류 정보를 꺼내는지(응답 전에 꺼내지 않는지) 검증합니다. */
    @Test
    fun `pending user id is consumed after the recover call returns`() = runTest {
        val events = mutableListOf<String>()
        val useCase = RecoverAccountUseCase(
            FakeUserRepository(Result.success(RecoverResult("a", "r"))) { events += "recover" },
            FakeAuthPreference(),
        )

        useCase {
            events += "consume"
            1L
        }

        assertEquals(listOf("recover", "consume"), events)
    }

    /** 이미 로그인된 상태이면 자동 로그인에 성공하는지 검증합니다. */
    @Test
    fun `auto login succeeds when already logged in`() = runTest {
        val useCase = AutoLoginUseCase(FakeAuthPreference(loggedIn = flowOf(true)))

        assertEquals(AutoLoginState.Success, useCase())
    }

    /** 로그인 상태가 아니면 자동 로그인에 실패하는지 검증합니다. */
    @Test
    fun `auto login fails when not logged in`() = runTest {
        val useCase = AutoLoginUseCase(FakeAuthPreference(loggedIn = flowOf(false)))

        assertEquals(AutoLoginState.Failed, useCase())
    }

    /** 토큰 만료만 세션을 정리하고 실패로 처리하는지 검증합니다. */
    @Test
    fun `auto login clears the session only when the token expired`() = runTest {
        val preference = FakeAuthPreference(loggedIn = flow { throw ApiError.Auth.TokenExpired("expired") })

        assertEquals(AutoLoginState.Failed, AutoLoginUseCase(preference)())
        assertTrue(preference.cleared)
    }

    /** 그 밖의 실패는 절대 로그아웃(세션 정리)하지 않고 실패로만 처리하는지 검증합니다. */
    @Test
    fun `auto login never clears the session for other failures`() = runTest {
        val preference = FakeAuthPreference(loggedIn = flow { throw IllegalStateException("network") })

        assertEquals(AutoLoginState.Failed, AutoLoginUseCase(preference)())
        assertFalse(preference.cleared)
    }

    /** 인증 정보 정리 유스케이스가 저장소를 비우는지 검증합니다. */
    @Test
    fun `clear auth session clears the preference`() = runTest {
        val preference = FakeAuthPreference()

        ClearAuthSessionUseCase(preference)()

        assertTrue(preference.cleared)
    }

    private fun loginResult(status: String) = LoginResult(
        userId = 7L,
        accessToken = "access",
        refreshToken = "refresh",
        status = status,
        inactiveDate = null,
    )

    private data class SavedTokens(
        val accessToken: String,
        val refreshToken: String?,
        val userId: Long,
        val loginType: LoginType,
    )

    private data class LoginRequest(
        val email: String,
        val password: String,
        val deviceId: String,
        val deviceType: String,
    )

    private class FakeAuthRepository(private val loginResponse: Result<LoginResult>) : AuthRepository {
        val loginRequests = mutableListOf<LoginRequest>()

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

        override suspend fun checkNickname(nickname: String): Result<Unit> = unused()
        override suspend fun signUpWithEmail(
            nickname: String,
            email: String,
            password: String,
            gender: Gender,
            jobId: Int,
            purposeList: List<Purpose>,
            interestList: List<Interest>,
            termsMap: Map<String, Boolean>,
        ): Result<SignUpEmailResult> = unused()

        override suspend fun sendEmailCode(email: String): Result<Unit> = unused()
        override suspend fun verifyEmailCode(email: String, code: String): Result<Unit> = unused()
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
        ): Result<Boolean> = unused()

        override suspend fun loginWithKakao(token: String): Result<LoginResult> = unused()
        override suspend fun loginWithGoogle(token: String): Result<LoginResult> = unused()
        override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = unused()
    }

    private class FakeUserRepository(
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

    private class FakeAuthPreference(
        private val deviceId: String = "device",
        private val deviceType: String = "PHONE",
        loggedIn: Flow<Boolean> = flowOf(false),
    ) : AuthPreference {
        val savedTokens = mutableListOf<SavedTokens>()
        val updatedAccessTokens = mutableListOf<Pair<String, String?>>()
        var cleared = false

        override val isLoggedIn: Flow<Boolean> = loggedIn
        override val sessionState: Flow<UserSession> = flowOf(UserSession())

        override suspend fun getDeviceId(): String = deviceId
        override suspend fun getDeviceType(): String = deviceType

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
        override suspend fun getLoginType(): LoginType = unused()
        override suspend fun updateLoginType(loginType: LoginType) = unused()
        override suspend fun getCachedNickname(): String? = unused()
        override suspend fun saveNickname(nickname: String) = unused()
        override suspend fun wasClipboardLinkPresented(url: String, copiedAtMillis: Long, userId: Long): Boolean = unused()
        override suspend fun savePresentedClipboardLink(url: String, copiedAtMillis: Long, userId: Long): Boolean = unused()
        override suspend fun wasLinkSavedAfterClipboardCopy(url: String, copiedAtMillis: Long, userId: Long): Boolean = unused()
        override suspend fun saveLastSavedLinkUrl(url: String, savedAtMillis: Long, userId: Long): Boolean = unused()
    }
}

private fun unused(): Nothing = error("이 테스트에서 사용하지 않는 호출입니다.")
