package com.linku.core.usecase

import com.linku.core.repository.AuthRepository
import javax.inject.Inject

/**
 * 비밀번호 재설정 링크를 이메일로 전송하는 UseCase입니다.
 *
 * @property authRepository 재설정 메일 전송 API를 호출하는 레포지토리
 */
class SendPasswordResetEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    /**
     * 비밀번호 재설정 메일을 전송합니다.
     *
     * @param email 재설정 링크를 받을 이메일
     * @return 전송 결과
     */
    suspend operator fun invoke(email: String): Result<Unit> =
        authRepository.sendPasswordResetEmail(email)
}
