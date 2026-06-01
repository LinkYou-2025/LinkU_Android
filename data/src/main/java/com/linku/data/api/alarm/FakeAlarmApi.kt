package com.linku.data.api.alarm

import com.linku.core.model.alarm.AlarmType
import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.server.alarm.AlarmSummaryDTO
import com.linku.data.api.dto.server.alarm.AlarmsDTO

/**
 * [AlarmApi] 인터페이스의 Mock 구현체입니다.
 *
 * 실제 서버 통신 없이 테스트 또는 개발 목적으로 더미 알림 데이터를 생성하여 반환합니다.
 * 알림 타입별 분기 처리, 페이지네이션(Cursor 기반), 읽음 상태 시뮬레이션 등의 로직을 포함하고 있습니다.
 *
 * 더미데이터 삽입 로직은 그냥 Claude 시켰습니당 어차피 api연동 후엔 삭제할 거라서...
 */
class FakeAlarmApi : AlarmApi {
    override suspend fun getAlarms(
        alarmType: String,
        cursor: Long?,
        size: Int
    ): BaseResponse<AlarmsDTO> {
        val start = cursor ?: 0L
        val linkMessage = "'요즘 대학생들이 진짜 쓰는 앱 TOP 10' 링크에 대한 AI 요약이 완료되었어요.'요즘 대학생들이 진짜 쓰는 앱 TOP 10' 링크에 대한 AI 요약이 완료되었어요.'요즘 대학생들이 진짜 쓰는 앱 TOP 10' 링크에 대한 AI 요약이 완료되었어요."

        val requestedType = AlarmType.from(alarmType)

        val alarms = List(size) { index ->
            val id = start + index

            val finalType = when (requestedType) {
                AlarmType.ALL -> AlarmType.entries[(id % (AlarmType.entries.size - 1)).toInt() + 1]
                else -> requestedType
            }

            val isLinkDummy = finalType == AlarmType.LINK && id % 3L == 0L

            AlarmSummaryDTO(
                alarmId = id,
                alarmType = finalType.name,
                message = if (isLinkDummy) linkMessage else "더미 알림 $id",
                createAt = "${id}분 전",
                targetId = id,
                isRead = id % 2L == 0L
            )
        }

        val nextCursor = if (start >= 50L) null else start + size

        return BaseResponse(
            isSuccess = true,
            code = "200",
            message = "success",
            result = AlarmsDTO(
                alarmList = alarms,
                nextCursor = nextCursor,
                hasNext = nextCursor != null
            )
        )
    }
}