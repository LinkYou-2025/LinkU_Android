package com.linku.data.implementation.repository

import com.linku.core.model.alarm.AlarmList
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import javax.inject.Inject

/**
 * [AlarmRepository]의 가짜(Fake) 구현체입니다.
 *
 * 실제 API 연동 전 UI 개발 및 테스트를 위해 더미 데이터를 생성하여 제공하며,
 * 전달받은 커서와 크기에 따라 가상의 알람 목록을 생성합니다.
 */
class FakeAlarmRepositoryImpl @Inject constructor(

) : AlarmRepository {

    override suspend fun fetchAlarms(
        type: AlarmType,
        cursor: Long?,
        size: Int
    ): Result<AlarmList> {

        return runCatching {

            val start = cursor ?: 0L
            val linkMessage = "'요즘 대학생들이 진짜 쓰는 앱 TOP 10' 링크에 대한 AI 요약이 완료되었어요.'요즘 대학생들이 진짜 쓰는 앱 TOP 10' 링크에 대한 AI 요약이 완료되었어요.'요즘 대학생들이 진짜 쓰는 앱 TOP 10' 링크에 대한 AI 요약이 완료되었어요."

            val alarms = List(size) { index ->
                val id = start + index

                val finalType = when (type) {
                    AlarmType.ALL -> {
                        AlarmType.entries[
                            (id % (AlarmType.entries.size - 1)).toInt() + 1
                        ]
                    }
                    else -> type
                }

                val isLinkDummy = finalType == AlarmType.LINK && id % 3L == 0L

                AlarmSummary(
                    id = id,
                    alarmType = finalType,
                    whenSubmitted = "${id}분 전",
                    message = if (isLinkDummy) linkMessage else "더미 알림 $id",
                    isRead = id % 2L == 0L
                )
            }

            val nextCursor = if (start >= 50L) null else start + size

            AlarmList(
                alarms = alarms,
                nextCursor = nextCursor,
                hasNext = nextCursor != null
            )
        }
    }
}


