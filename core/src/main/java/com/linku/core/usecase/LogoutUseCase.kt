package com.linku.core.usecase

import com.linku.core.repository.AlarmRepository
import com.linku.core.repository.UserRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val alarmRepository: AlarmRepository,
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        // FCM 토큰 조회 후 서버에서 삭제 (실패해도 로그아웃은 계속 진행)
        val token = alarmRepository.getFCMTokenFromFCM()
            .getOrNull()

        if (token != null) {
            alarmRepository.deleteFcmToken(token)
        }

        // 2. 로그아웃 API 호출 + 로컬 세션 정리
        userRepository.logout()
            .getOrThrow()

    }.onFailure {
        // 사용자가 화면을 나가는 등의 이유로 발생하는 CancellationException는
        // 정상 흐름이므로 Result로 감싸지 않고 그대로 전파
        if (it is CancellationException) throw it
    }
}
