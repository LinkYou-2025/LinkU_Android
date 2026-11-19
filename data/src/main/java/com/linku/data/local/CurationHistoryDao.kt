package com.linku.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

/** 월별 큐레이션 히스토리 캐시 접근 객체입니다. Room이 suspend 함수를 IO 스레드에서 실행합니다. */
@Dao
interface CurationHistoryDao {

    /**
     * 한 사용자의 특정 연도 캐시를 월 순서대로 조회합니다.
     *
     * @param userId 사용자 ID
     * @param yearPrefix 연도 접두사. ex) "2025-"
     */
    @Query("SELECT * FROM curation_history WHERE userId = :userId AND month LIKE :yearPrefix || '%' ORDER BY month")
    suspend fun getByYear(userId: Long, yearPrefix: String): List<CurationHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rows: List<CurationHistoryEntity>)

    @Query("DELETE FROM curation_history WHERE userId = :userId AND month LIKE :yearPrefix || '%'")
    suspend fun deleteByYear(userId: Long, yearPrefix: String)

    /** 한 연도 캐시를 하나의 트랜잭션으로 통째로 교체합니다. 중간에 실패해도 반쪽 데이터가 남지 않습니다. */
    @Transaction
    suspend fun replaceYear(userId: Long, yearPrefix: String, rows: List<CurationHistoryEntity>) {
        deleteByYear(userId, yearPrefix)
        insertAll(rows)
    }

    /** 로그아웃 시 모든 캐시를 비웁니다. */
    @Query("DELETE FROM curation_history")
    suspend fun clearAll()
}
