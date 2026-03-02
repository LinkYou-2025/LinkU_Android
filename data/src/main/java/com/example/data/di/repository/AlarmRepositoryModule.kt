package com.example.data.di.repository

import com.example.core.repository.AlarmRepository
import com.example.data.api.ServerApi
import com.example.data.implementation.repository.AlarmRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmRepositoryModule {

    @Provides
    @Singleton
    fun provideAlarmRepository(
        serverApi: ServerApi
    ): AlarmRepository {
        return AlarmRepositoryImpl(serverApi = serverApi)
    }
}