package com.linku.core.usecase

import com.linku.core.model.alarm.AlarmDetail
import com.linku.core.repository.AlarmRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/**
 * 특정 알람을 읽음 상태로 업데이트하고 상세 정보를 조회하는 유스케이스입니다.
 *
 * 이 유스케이스는 두 가지 작업을 순차적으로 수행합니다.
 * 1. [alarmRepository]를 통해 특정 알람을 읽음(read) 처리합니다.
 * 2. 해당 알람의 최신 [AlarmDetail] 정보를 가져와 반환합니다.
 */
class NoticeAlarmReadUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository
) {
    suspend operator fun invoke(alarmId: Long): Result<AlarmDetail> = runCatching {

        // 노션 정책짐을 참고. 클릭 시 즉시 알람 처리를 우리 다인눈나가 원하시기 때문에
        // 상세조회보다 먼저 호출
        alarmRepository.readAlarm(alarmId)

        alarmRepository.getAlarmDetail(alarmId)
            .getOrThrow()

    }.onFailure{
        // 사용자가 화면을 나가는 등의 이유로 발생하는 CancellationException는
        // 정상 흐름이므로 Result로 감싸지 않고 그대로 전파ㄷ
        if (it is CancellationException) throw it
    }
}