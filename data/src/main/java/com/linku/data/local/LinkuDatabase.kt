package com.linku.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * 서버 데이터의 읽기 캐시를 보관하는 Room 데이터베이스입니다.
 *
 * 원본은 서버이므로 캐시는 언제든 버려도 됩니다. 그래서 스키마가 바뀌면 마이그레이션 대신 재생성합니다.
 */
@Database(entities = [CurationHistoryEntity::class], version = 1, exportSchema = true)
abstract class LinkuDatabase : RoomDatabase() {
    abstract fun curationHistoryDao(): CurationHistoryDao
}
