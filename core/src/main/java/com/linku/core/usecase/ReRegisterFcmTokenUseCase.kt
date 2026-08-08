package com.linku.core.usecase

import android.util.Log
import com.linku.core.repository.AlarmRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/**
 * Firebase Cloud Messaging(FCM) 토큰을 재등록하기 위한 Use Case입니다.
 *
 * Firebase 서비스에서 현재 FCM 토큰을 가져온 후, [AlarmRepository]를 통해
 * 애플리케이션의 백엔드 서버에 해당 토큰을 등록합니다.
 * 사용자가 자동로그인이든 수동로그인이든 로그인했을 때 호출됩니다.
 *
 * @property alarmRepository FCM 토큰 작업 및 알림 설정을 관리하는 Repository입니다.
 */
class ReRegisterFcmTokenUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        val token = alarmRepository.getFCMTokenFromFCM().getOrThrow()
        alarmRepository.registerFCMToken(token).getOrThrow()
        Log.d("FCM", "FCM 토큰 재등록 성공")

        Unit
    }.onFailure {
        // 사용자가 화면을 나가는 등의 이유로 발생하는 CancellationException는
        // 정상 흐름이므로 Result로 감싸지 않고 그대로 전파
        if (it is CancellationException) throw it
    }
}
