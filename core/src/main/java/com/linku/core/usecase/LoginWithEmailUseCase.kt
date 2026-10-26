package com.linku.core.usecase

import com.linku.core.model.LoginResult
import com.linku.core.model.auth.LoginType
import com.linku.core.preference.AuthPreference
import com.linku.core.repository.AuthRepository
import javax.inject.Inject

/**
 * 이메일 로그인 처리 결과입니다.
 */
sealed interface EmailLoginOutcome {

    /**
     * 정상 로그인입니다. 토큰과 사용자 ID가 정식 세션으로 저장되었습니다.
     *
     * @property loginResult 로그인 응답
     */
    data class Authenticated(val loginResult: LoginResult) : EmailLoginOutcome

    /**
     * 탈퇴 유예기간 계정입니다. 복구 API 호출용 토큰만 임시 저장되었고 정식 세션은 저장되지 않았습니다.
     *
     * @property loginResult 로그인 응답. 복구가 확정되면 정식 세션 저장에 사용합니다.
     */
    data class RecoveryRequired(val loginResult: LoginResult) : EmailLoginOutcome
}

/**
 * 이메일과 비밀번호로 로그인하고, 응답 상태에 맞게 인증 정보를 저장하는 UseCase입니다.
 *
 * 로그인 응답이 `INACTIVE`(탈퇴 유예기간 계정)이면 정식 세션(`LOGGED_IN`)을 저장하지 않고 복구 API 호출용
 * 토큰만 임시 저장합니다. `LOGGED_IN`을 저장하면 다음 실행 시 자동 로그인이 서버 상태를 다시 확인하지 않고
 * 로컬 플래그만 보고 바로 홈으로 보내 버리기 때문입니다.
 *
 * 실패하면 예외를 그대로 던집니다. 호출한 쪽이 취소 예외는 다시 던지고, 나머지는 화면용 오류로 변환합니다.
 *
 * @property authRepository 로그인 API를 호출하는 레포지토리
 * @property authPreference 기기 정보와 인증 토큰을 저장하는 저장소
 */
class LoginWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val authPreference: AuthPreference,
) {

    /**
     * 이메일 로그인을 수행합니다.
     *
     * @param email 로그인할 이메일
     * @param password 로그인할 비밀번호
     * @return 정상 로그인 또는 복구가 필요한 계정을 나타내는 [EmailLoginOutcome]
     */
    suspend operator fun invoke(email: String, password: String): EmailLoginOutcome {
        val deviceId = authPreference.getDeviceId()
        val deviceType = authPreference.getDeviceType()

        val loginResult = authRepository.login(
            email = email,
            password = password,
            deviceId = deviceId,
            deviceType = deviceType
        ).getOrThrow()

        if (loginResult.status == INACTIVE_STATUS) {
            // 탈퇴 유예기간 계정 -> 복구 API 호출용 토큰만 임시 저장함.
            authPreference.updateAccessToken(
                loginResult.accessToken,
                loginResult.refreshToken
            )
            return EmailLoginOutcome.RecoveryRequired(loginResult)
        }

        authPreference.saveTokens(
            accessToken = loginResult.accessToken,
            refreshToken = loginResult.refreshToken,
            userId = loginResult.userId,
            loginType = LoginType.EMAIL
        )
        return EmailLoginOutcome.Authenticated(loginResult)
    }

    private companion object {
        /** 탈퇴 유예기간 계정을 나타내는 로그인 응답 상태 값입니다. */
        const val INACTIVE_STATUS = "INACTIVE"
    }
}
