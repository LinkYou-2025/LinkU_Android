package com.linku.data.di.repository

import com.linku.core.repository.AlarmRepository
import com.linku.data.implementation.repository.AlarmRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmRepositoryModule {

    @Binds
    @Singleton
    abstract fun provideAlarmRepository(
        impl: AlarmRepositoryImpl
    ): AlarmRepository
}