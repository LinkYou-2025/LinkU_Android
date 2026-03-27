package com.linku.data.di.api

import com.linku.data.api.CurationApi
import com.linku.data.preference.AuthPreference
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import com.linku.data.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object CurationApiModule {

    @Provides
    @Singleton
    @Named("curation") // ★ 충돌 방지용
    fun provideCurationOkHttpClient(
        authPreference: AuthPreference
    ): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            // ★ 전용 타임아웃
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            // 토큰 자동 부착
            .addNetworkInterceptor { chain ->
                val b = chain.request().newBuilder()
                authPreference.accessToken?.let { b.addHeader("Authorization", "Bearer $it") }
                chain.proceed(b.build())
            }
            .addInterceptor(logger)
            .build()
    }

    @Provides
    @Singleton
    @Named("curation") // ★ 충돌 방지용
    fun provideCurationRetrofit(
        moshi: Moshi,
        @Named("curation") client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideCurationApi(
        @Named("curation") retrofit: Retrofit
    ): CurationApi {
        return retrofit.create(CurationApi::class.java)
    }
}