package com.linku.data.di.api

import com.linku.data.BuildConfig
import com.linku.data.api.AuthClient
import com.linku.data.api.CurationApi
import com.linku.data.api.alarm.AlarmApi
import com.linku.data.preference.AuthPreference
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CurationApiModule {

    @Provides
    @Singleton
    fun provideCurationApi(
        @AuthClient retrofit: Retrofit
    ): CurationApi =
        retrofit.create(CurationApi::class.java)

}