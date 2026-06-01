package com.linku.data.mapper

import com.linku.core.model.alarm.AlarmList
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.data.api.dto.server.alarm.AlarmSummaryDTO
import com.linku.data.api.dto.server.alarm.AlarmsDTO

/**
 * 서버 계층의 알람 관련 데이터 전송 객체(DTO)를 도메인 모델로 변환하는 유틸리티 객체입니다.
 *
 * 이 매퍼는 [AlarmsDTO] 및 [AlarmSummaryDTO]를 각각 [AlarmList]와 [AlarmSummary]로 변환하여,
 * 데이터를 애플리케이션의 도메인 계층에서 사용할 수 있는 형식으로 매핑하는 역할을 수행합니다.
 */
object AlarmMapper {
    fun AlarmsDTO.toDomain(): AlarmList = AlarmList(
        alarms = alarmList.map { it.toDomain() },
        nextCursor = nextCursor,
        hasNext = hasNext
    )

    private fun AlarmSummaryDTO.toDomain(): AlarmSummary = AlarmSummary(
        id = alarmId,
        alarmType = AlarmType.from(alarmType),
        whenSubmitted = createAt,
        message = message,
        targetId = targetId,
        isRead = isRead
    )
}