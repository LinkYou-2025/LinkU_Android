package com.linku.data.local

import androidx.room.Entity

/**
 * 서버 응답 본문(JSON)을 그대로 저장하는 범용 읽기 캐시 행입니다.
 *
 * 화면 하나가 여러 API를 조합해 쓰는 경우, API별 테이블을 만들지 않고 응답 단위로 보관합니다.
 *
 * @property userId 응답을 받아온 사용자. 계정이 바뀌어도 남의 캐시가 보이지 않게 합니다.
 * @property key 응답 종류와 파라미터. ex) "detail:98", "tags:2025-08"
 * @property json 응답 DTO를 직렬화한 JSON
 */
@Entity(tableName = "api_cache", primaryKeys = ["userId", "key"])
data class ApiCacheEntity(
    val userId: Long,
    val key: String,
    val json: String,
)
