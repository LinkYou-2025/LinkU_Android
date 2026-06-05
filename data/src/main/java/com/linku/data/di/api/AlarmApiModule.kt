package com.linku.data.di.api

import com.linku.data.api.AuthClient
import com.linku.data.api.alarm.AlarmApi
import com.linku.data.api.alarm.FakeAlarmApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmApiModule {

    @Provides
    @Singleton
    fun provideAlarmApi(
        @AuthClient retrofit: Retrofit
    ): AlarmApi =
        retrofit.create(AlarmApi::class.java)

}