package com.linku.data.di

import android.content.Context
import androidx.room.Room
import com.linku.data.local.ApiCacheDao
import com.linku.data.local.CurationHistoryDao
import com.linku.data.local.LinkuDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LinkuDatabase =
        Room.databaseBuilder(context, LinkuDatabase::class.java, "linku.db")
            // 캐시 전용 DB라 스키마 변경 시 재생성해도 데이터 손실이 없다
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideCurationHistoryDao(database: LinkuDatabase): CurationHistoryDao =
        database.curationHistoryDao()

    @Provides
    fun provideApiCacheDao(database: LinkuDatabase): ApiCacheDao = database.apiCacheDao()

    /** 올해 판단을 테스트에서 고정할 수 있도록 시계를 주입합니다. */
    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemDefaultZone()
}
