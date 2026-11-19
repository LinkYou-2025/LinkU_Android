package com.linku.data.local

import androidx.room.Entity

/**
 * 월별 큐레이션 히스토리 한 달치를 저장하는 캐시 행입니다.
 *
 * 월 값("yyyy-MM")에 연도가 포함되어 있어 (userId, month)만으로 행이 유일합니다.
 *
 * @property userId 이 행을 받아온 사용자. 계정이 바뀌어도 남의 캐시가 보이지 않게 합니다.
 * @property month 월. ex) "2025-11"
 * @property curationId 그 달의 큐레이션 ID. 큐레이션이 없으면 null
 * @property thumbnailUrl 썸네일 URL. 없으면 null
 * @property fetchedYear 서버에서 받아온 시점의 올해. 이 값이 [month]의 연도보다 크면 확정된 과거 데이터입니다.
 */
@Entity(tableName = "curation_history", primaryKeys = ["userId", "month"])
data class CurationHistoryEntity(
    val userId: Long,
    val month: String,
    val curationId: Long?,
    val thumbnailUrl: String?,
    val fetchedYear: Int,
)
