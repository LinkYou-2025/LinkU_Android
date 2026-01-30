package com.example.data.di.api

import com.squareup.moshi.Moshi
import com.example.data.api.ServerApi
import com.example.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import com.example.data.BuildConfig
import com.example.data.api.UserApi

/*
* 토큰을 읽는 첫 번째 지점임.
* AuthPreferenceImpl에 저장된 토큰 꺼내서 사용하는 곳.
* */
@Module
@InstallIn(SingletonComponent::class)
object ServerApiModule {
    @Provides
    @Singleton
    fun provideServerApi(
        authPreference: AuthPreference,
        moshi: Moshi,
    ): ServerApi {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            // api 요청 -> okhttp 요청을 보내기 전, 엑세스 토큰을 읽음. 값이 있으면 헤더 해서 서버로 전달
            .addNetworkInterceptor {
                val request = it.request()
                    .newBuilder()
                    .let { builder ->
                        authPreference.accessToken?.let { token ->
                            builder.addHeader("Authorization", "Bearer $token")
                        } ?: builder
                    }
                    .build()
                it.proceed(request)
            }
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
            .create(ServerApi::class.java)
    }


    // Retrofit을 주입받지 않고 직접 생성 (ServerApiModule 구조 유지)
    @Provides
    @Singleton
    fun provideUserApi(authPreference: AuthPreference, moshi: Moshi): UserApi {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addNetworkInterceptor {
                val request = it.request()
                    .newBuilder()
                    .let { builder ->
                        authPreference.accessToken?.let { token ->
                            builder.addHeader("Authorization", "Bearer $token")
                        } ?: builder
                    }
                    .build()
                it.proceed(request)
            }
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
            .create(UserApi::class.java)
    }
}