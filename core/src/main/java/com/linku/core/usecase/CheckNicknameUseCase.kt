package com.linku.core.usecase

import com.linku.core.repository.AuthRepository
import javax.inject.Inject

/**
 * 닉네임 중복 여부를 서버에 확인하는 UseCase입니다.
 *
 * @property authRepository 닉네임 확인 API를 호출하는 레포지토리
 */
class CheckNicknameUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    /**
     * 닉네임을 사용할 수 있는지 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 사용할 수 있으면 성공, 중복이거나 확인에 실패하면 실패 결과
     */
    suspend operator fun invoke(nickname: String): Result<Unit> =
        authRepository.checkNickname(nickname)
}
