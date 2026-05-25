package com.linku.data.di.repository

import com.linku.core.repository.AlarmRepository
import com.linku.data.implementation.repository.FakeAlarmRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmRepositoryModule {

    // 페이크 레포지토리
    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun provideAlarmRepository(
        impl: FakeAlarmRepositoryImpl
    ): AlarmRepository
}