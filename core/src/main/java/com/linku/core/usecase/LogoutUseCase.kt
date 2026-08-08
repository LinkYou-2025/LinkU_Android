package com.linku.core.usecase

import android.util.Log
import com.linku.core.BuildConfig
import com.linku.core.repository.AlarmRepository
import com.linku.core.repository.UserRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/**
 * 로그아웃 처리를 담당하는 UseCase입니다.
 *
 * 이 UseCase는 다음 과정을 수행합니다:
 * 1. 현재 기기의 FCM 토큰을 조회하여 서버에서 삭제합니다. (실패하더라도 로그아웃 프로세스는 계속 진행됩니다.)
 * 2. 서버에 로그아웃 요청을 보내고, 로컬에 저장된 사용자 세션 및 인증 정보를 초기화합니다.
 *
 * @property userRepository 사용자 정보 및 세션 관리를 위한 레포지토리
 * @property alarmRepository 알림 및 FCM 토큰 관리를 위한 레포지토리
 */
class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val alarmRepository: AlarmRepository,
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        // FCM 토큰 조회 후 서버에서 삭제 (실패해도 로그아웃은 계속 진행)
        val token = alarmRepository.getFCMTokenFromFCM()
            .getOrNull()

        if (BuildConfig.DEBUG) {
            Log.d("FCM", "로그아웃 시 FCM 토큰: $token")
        }

        if (token != null) {
            alarmRepository.deleteFcmToken(token)
        }

        // 로그아웃 API 호출 + 로컬 세션 정리
        userRepository.logout()
            .getOrThrow()

    }.onFailure {
        // 사용자가 화면을 나가는 등의 이유로 발생하는 CancellationException는
        // 정상 흐름이므로 Result로 감싸지 않고 그대로 전파
        if (it is CancellationException) throw it
    }
}
