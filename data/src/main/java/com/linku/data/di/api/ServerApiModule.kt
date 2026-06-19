package com.linku.data.di.api

import com.linku.data.BuildConfig
import com.linku.data.api.AuthApi
import com.linku.data.api.AuthClient
import com.linku.data.api.PublicClient
import com.linku.data.api.ServerApi
import com.linku.data.api.TokenAuthenticator
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServerApiModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

    /* 토큰 불필요 Retrofit → AuthApi용 (로그인, 회원가입, 토큰 재발급) */
    @Provides
    @Singleton
    @PublicClient
    fun providePublicRetrofit(
        loggingInterceptor: HttpLoggingInterceptor,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.SERVER_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        )
        .build()

    /* 토큰 필요 Retrofit → ServerApi, UserApi용*/
    @Provides
    @Singleton
    @AuthClient
    fun provideAuthRetrofit(
        loggingInterceptor: HttpLoggingInterceptor,
        authPreference: AuthPreference,
        tokenAuthenticator: TokenAuthenticator,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.SERVER_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(
            OkHttpClient.Builder()
                .authenticator(tokenAuthenticator)
                .addInterceptor { chain ->
                    if (chain.request().header("Authorization") != null) {
                        return@addInterceptor chain.proceed(chain.request())
                    }
                    val token = runBlocking { authPreference.getAccessToken() }
                    val request = if (!token.isNullOrBlank()) {
                        chain.request().newBuilder()
                            .header("Authorization", "Bearer $token")
                            .build()
                    } else chain.request()
                    chain.proceed(request)
                }
                .addInterceptor(loggingInterceptor)
                .build()
        )
        .build()

    /* AuthApi → 토큰 불필요 (로그인, 회원가입, reissue 등) */
    @Provides
    @Singleton
    fun provideAuthApi(@PublicClient retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    /* ServerApi → 토큰 필요 */
    @Provides
    @Singleton
    fun provideServerApi(@AuthClient retrofit: Retrofit): ServerApi =
        retrofit.create(ServerApi::class.java)
}