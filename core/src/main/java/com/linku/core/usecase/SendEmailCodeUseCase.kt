package com.linku.core.usecase

import com.linku.core.repository.AuthRepository
import javax.inject.Inject

/**
 * 회원가입 이메일 인증 코드를 전송하거나 다시 전송하는 UseCase입니다.
 *
 * @property authRepository 인증 코드 전송 API를 호출하는 레포지토리
 */
class SendEmailCodeUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    /**
     * 인증 코드를 전송합니다.
     *
     * @param email 인증 코드를 받을 이메일
     * @return 전송 결과
     */
    suspend operator fun invoke(email: String): Result<Unit> =
        authRepository.sendEmailCode(email)
}
