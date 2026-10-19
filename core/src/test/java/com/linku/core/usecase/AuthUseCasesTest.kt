package com.linku.core.usecase

import com.linku.core.model.LoginResult
import com.linku.core.model.auth.Gender
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Job
import com.linku.core.model.auth.LoginType
import com.linku.core.model.auth.Purpose
import com.linku.core.model.auth.RecoverResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 회원가입, 이메일 인증, 비밀번호 재설정, 소셜 로그인 유스케이스가 저장소에 인자를 그대로 전달하고 결과를
 * 변형 없이 돌려주는지 검증합니다.
 */
class AuthUseCasesTest {

    private val terms = mapOf("TERMS_OF_USE" to true, "PRIVACY_POLICY" to true, "MARKETING" to false)

    /** 닉네임 확인이 닉네임을 그대로 전달하고 저장소의 결과(성공과 실패)를 그대로 돌려주는지 검증합니다. */
    @Test
    fun `check nickname delegates and returns the repository result`() = runTest {
        val repository = FakeAuthRepository()
        val failure = Result.failure<Unit>(IllegalStateException("duplicate"))
        repository.checkNicknameResult = failure

        val result = CheckNicknameUseCase(repository)("링큐")

        assertEquals(listOf("checkNickname:링큐"), repository.calls)
        assertEquals(failure, result)
    }

    /** 이메일 회원가입이 모든 입력을 같은 순서와 값으로 저장소에 전달하는지 검증합니다. */
    @Test
    fun `sign up delegates every argument`() = runTest {
        val repository = FakeAuthRepository()
        val purposes = listOf(Purpose.entries.first())
        val interests = listOf(Interest.entries.first())

        val result = SignUpWithEmailUseCase(repository)(
            nickname = "링큐",
            email = "a@b.com",
            password = "pw",
            gender = Gender.MALE,
            jobId = 3,
            purposeList = purposes,
            interestList = interests,
            termsMap = terms,
        )

        assertEquals(listOf("signUp:링큐,a@b.com,pw,MALE,3,1,1,$terms"), repository.calls)
        assertEquals(repository.signUpResult, result)
    }

    /** 인증 코드 전송, 검증, 비밀번호 재설정 메일 전송이 각각 올바른 저장소 호출로 이어지는지 검증합니다. */
    @Test
    fun `email code and password reset use cases delegate`() = runTest {
        val repository = FakeAuthRepository()

        SendEmailCodeUseCase(repository)("a@b.com")
        VerifyEmailCodeUseCase(repository)("a@b.com", "123456")
        SendPasswordResetEmailUseCase(repository)("a@b.com")

        assertEquals(
            listOf(
                "sendEmailCode:a@b.com",
                "verifyEmailCode:a@b.com,123456",
                "sendPasswordReset:a@b.com",
            ),
            repository.calls,
        )
    }

    /** 카카오와 구글 로그인이 서로 다른 저장소 호출로 토큰을 전달하고 응답을 그대로 돌려주는지 검증합니다. */
    @Test
    fun `social login use cases call their own provider endpoint`() = runTest {
        val kakao = loginResult("ACTIVE")
        val google = loginResult("TEMP")
        val repository = FakeAuthRepository().apply {
            kakaoResult = Result.success(kakao)
            googleResult = Result.success(google)
        }

        val kakaoResult = LoginWithKakaoUseCase(repository)("kakao-token")
        val googleResult = LoginWithGoogleUseCase(repository)("google-token")

        assertEquals(listOf("kakao:kakao-token", "google:google-token"), repository.calls)
        assertEquals(kakao, kakaoResult.getOrThrow())
        assertEquals(google, googleResult.getOrThrow())
    }

    /** 소셜 프로필 완료가 로그인 수단을 포함한 모든 입력을 저장소에 전달하는지 검증합니다. */
    @Test
    fun `complete social profile delegates every argument`() = runTest {
        val repository = FakeAuthRepository()

        val result = CompleteSocialProfileUseCase(repository)(
            socialToken = "social",
            nickName = "링큐",
            gender = Gender.FEMALE,
            job = Job.entries.first(),
            purposes = listOf(Purpose.entries.first()),
            interests = listOf(Interest.entries.first()),
            termsMap = terms,
            loginType = LoginType.KAKAO,
        )

        assertEquals(
            listOf("completeSocialProfile:social,링큐,FEMALE,${Job.entries.first()},1,1,$terms,KAKAO"),
            repository.calls,
        )
        assertEquals(repository.completeProfileResult, result)
    }

    /** 최근 로그인 수단 조회가 저장소의 값을 그대로 돌려주는지 검증합니다. */
    @Test
    fun `recent login type comes from the preference`() = runTest {
        val preference = FakeAuthPreference(recentLoginType = LoginType.GOOGLE)

        assertEquals(LoginType.GOOGLE, GetRecentLoginTypeUseCase(preference)())
    }

    /** 소셜 계정 복구는 지정한 로그인 수단(이메일이 아님)으로 정식 세션을 저장하는지 검증합니다. */
    @Test
    fun `social recover saves the session with the social login type`() = runTest {
        val preference = FakeAuthPreference()
        val useCase = RecoverAccountUseCase(
            FakeUserRepository(Result.success(RecoverResult("new-access", "new-refresh"))),
            preference,
        )

        val outcome = useCase(loginType = LoginType.KAKAO) { 9L }

        assertEquals(RecoverAccountOutcome.Recovered, outcome)
        assertEquals(listOf(SavedTokens("new-access", "new-refresh", 9L, LoginType.KAKAO)), preference.savedTokens)
    }

    private fun loginResult(status: String) = LoginResult(
        userId = 7L,
        accessToken = "access",
        refreshToken = "refresh",
        status = status,
        inactiveDate = null,
    )
}
