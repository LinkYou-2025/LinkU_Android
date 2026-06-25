package com.linku.core.di

import com.linku.core.repository.AlarmRepository
import com.linku.core.usecase.FirstPushAlarmAllowedUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideFirstPushAlarmAllowedUseCase(
        alarmRepository: AlarmRepository
    ): FirstPushAlarmAllowedUseCase {
        return FirstPushAlarmAllowedUseCase(alarmRepository)
    }

}