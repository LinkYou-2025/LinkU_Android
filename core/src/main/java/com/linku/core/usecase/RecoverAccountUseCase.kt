package com.linku.core.usecase

import com.linku.core.model.auth.LoginType
import com.linku.core.preference.AuthPreference
import com.linku.core.repository.UserRepository
import javax.inject.Inject

/**
 * 탈퇴 유예기간 계정 복구 처리 결과입니다.
 */
sealed interface RecoverAccountOutcome {

    /** 복구에 성공해 정식 세션이 저장되었습니다. */
    data object Recovered : RecoverAccountOutcome

    /** 복구 API는 성공했지만, 그 사이 복구 모달이 닫혀 이어서 처리할 로그인 정보가 없어 아무것도 저장하지 않았습니다. */
    data object Ignored : RecoverAccountOutcome

    /**
     * 복구에 실패했습니다. 계정은 여전히 비활성 상태이므로 임시 저장된 토큰을 정리했습니다.
     *
     * @property error 실패 원인
     */
    data class Failed(val error: Throwable) : RecoverAccountOutcome
}

/**
 * 탈퇴 유예기간 계정을 복구하고, 성공하면 정식 세션으로 저장하는 UseCase입니다.
 *
 * 로그인 응답으로 이미 저장된 복구 전용 토큰으로 `users/recover`를 호출합니다. 복구가 확정된 시점에만 복구
 * API가 새로 내려준 정식 토큰을 저장합니다.
 *
 * @property userRepository 복구 API를 호출하는 레포지토리
 * @property authPreference 인증 토큰을 저장하고 정리하는 저장소
 */
class RecoverAccountUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference,
) {

    /**
     * 계정 복구를 수행합니다.
     *
     * 복구 응답이 도착한 뒤에 [consumePendingUserId]를 호출해 이어서 처리할 사용자 ID를 가져옵니다. 응답을
     * 기다리는 동안 복구 모달이 닫힌 경우를 구분하기 위해서입니다.
     *
     * @param consumePendingUserId 이어서 처리할 로그인 응답의 사용자 ID를 꺼내고 보류 상태를 해제하는 함수.
     * 없으면 `null`을 돌려줍니다.
     * @return 복구 결과를 나타내는 [RecoverAccountOutcome]
     */
    suspend operator fun invoke(consumePendingUserId: () -> Long?): RecoverAccountOutcome {
        val recoverResult = userRepository.recoverUser()
        val pendingUserId = consumePendingUserId()

        recoverResult.exceptionOrNull()?.let { error ->
            // 복구 실패 시 계정은 여전히 비활성 상태이므로 임시 저장된 토큰을 정리함.
            authPreference.clear()
            return RecoverAccountOutcome.Failed(error)
        }

        val recovered = recoverResult.getOrThrow()
        if (pendingUserId == null) return RecoverAccountOutcome.Ignored

        // 복구가 확정된 시점에만 정식 세션(LOGGED_IN=true)으로 저장함.
        // recover API가 새로 내려준 정식 토큰을 씀 (로그인 때 받은 복구 전용 토큰이 아님).
        authPreference.saveTokens(
            accessToken = recovered.accessToken,
            refreshToken = recovered.refreshToken,
            userId = pendingUserId,
            loginType = LoginType.EMAIL
        )
        return RecoverAccountOutcome.Recovered
    }
}
