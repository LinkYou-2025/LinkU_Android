package com.linku.core.usecase

import com.linku.core.model.LoginResult
import com.linku.core.repository.AuthRepository
import javax.inject.Inject

/**
 * 카카오 인증 토큰으로 서버에 소셜 로그인을 요청하는 UseCase입니다.
 *
 * 응답의 회원 상태(TEMP, ACTIVE, INACTIVE)에 따른 화면 흐름은 호출한 쪽이 결정합니다.
 *
 * @property authRepository 소셜 로그인 API를 호출하는 레포지토리
 */
class LoginWithKakaoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    /**
     * 카카오 로그인을 요청합니다.
     *
     * @param token 카카오 SDK 인증 완료 후 받은 토큰
     * @return 로그인 응답
     */
    suspend operator fun invoke(token: String): Result<LoginResult> =
        authRepository.loginWithKakao(token)
}
