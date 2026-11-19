package com.linku.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/** [ApiCacheEntity] 접근 객체입니다. */
@Dao
interface ApiCacheDao {

    /** @return 저장된 JSON. 없으면 null */
    @Query("SELECT json FROM api_cache WHERE userId = :userId AND `key` = :key")
    suspend fun get(userId: Long, key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: ApiCacheEntity)

    /** 로그아웃 시 모든 캐시를 비웁니다. */
    @Query("DELETE FROM api_cache")
    suspend fun clearAll()
}
