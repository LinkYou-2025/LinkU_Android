package com.linku.core.usecase

import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import javax.inject.Inject

/**
 * 푸시 알림의 초기 등록 및 구성을 처리하는 유즈케이스입니다.
 *
 * 이 유즈케이스는 FCM 토큰을 가져와 서버에 등록하고, 사용자가 푸시 알림을
 * 처음으로 수신할 수 있도록 기기의 알람 설정을 업데이트하는 과정을 조율합니다.
 *
 * @property alarmRepository FCM 토큰 관리 및 알람 설정 업데이트를 담당하는 레포지토리입니다.
 *
 * 제작 이유: 사용자가 처음 푸시알람 설정을 했을 때는, fcm에서 현재 토큰을 가져옴 -> 서버에 토큰 등록 api 호출 -> 푸사알림 활성화 api 호출
 * 이 3개의 과정을 거쳐야 함. 근데 이 로직이 그대로 뷰모델에 박혀버리면?
 * 딱 봐도 스파게티가 될 가능성이 보임
 * 따라서 유스케이스 레이어를 신설
 */
class FirstPushAlarmAllowedUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {

        val token = alarmRepository.getFCMTokenFromFCM()
            .getOrThrow()

        alarmRepository.registerFCMToken(token)
            .getOrThrow()

        alarmRepository.updateAlarmSetting(AlarmType.ALL)
    }

}