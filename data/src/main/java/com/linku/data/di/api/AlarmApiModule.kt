package com.linku.data.di.api

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

    // 더미데이터 삽입용. 추후 수정 예정
    @Provides
    @Singleton
    fun provideAlarmApi(): AlarmApi {
        return FakeAlarmApi()
    }
}