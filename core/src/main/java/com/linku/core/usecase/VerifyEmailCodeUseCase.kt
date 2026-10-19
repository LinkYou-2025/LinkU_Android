package com.linku.core.usecase

import com.linku.core.repository.AuthRepository
import javax.inject.Inject

/**
 * 회원가입 이메일 인증 코드를 검증하는 UseCase입니다.
 *
 * @property authRepository 인증 코드 검증 API를 호출하는 레포지토리
 */
class VerifyEmailCodeUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    /**
     * 인증 코드를 검증합니다.
     *
     * @param email 인증 코드를 받은 이메일
     * @param code 사용자가 입력한 인증 코드
     * @return 검증 결과
     */
    suspend operator fun invoke(email: String, code: String): Result<Unit> =
        authRepository.verifyEmailCode(email, code)
}
