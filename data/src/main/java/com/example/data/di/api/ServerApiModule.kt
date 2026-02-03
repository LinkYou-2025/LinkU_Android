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
import okhttp3.Interceptor


/*
* 토큰을 읽는 첫 번째 지점임.
* AuthPreferenceImpl에 저장된 토큰 꺼내서 사용하는 곳.
* */
@Module
@InstallIn(SingletonComponent::class) //앱이 꺼질 때까지 하나만 만들어서 어디서든 돌려쓸 예쩡임.
object ServerApiModule {

    // 요청/응답 로그 출력
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    // 인증 인터센터임. 토큰에 헤더를 자동 추가함.
    @Provides
    @Singleton
    fun provideAuthInterceptor(authPreference: AuthPreference): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val path = originalRequest.url.encodedPath

            // UserApi 정의서에 기반한 토큰 미필요 경로 리스트 (정확한 경로 명시)
            val skipAuthPaths = setOf(
                "/api/users/reissue",
                "/api/users/login",
                "/api/users/join",
                "/api/users/check-nickname",
                "/api/users/emails/code",
                "/api/users/emails/verify",
                "/api/users/password/temp"
            )

            // path가 "/api/users/login"일 때만 true가 됨
            val isSkipPath = skipAuthPaths.contains(path)

            val newRequest = if (isSkipPath) {
                originalRequest
            } else {
                originalRequest.newBuilder().apply {
                    authPreference.accessToken?.let { token ->
                        addHeader("Authorization", "Bearer $token")
                    }
                }.build()
            }
            chain.proceed(newRequest)
        }
    }
    
    //OkHttpClient : 네트워크 전송
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(authInterceptor)  // 토큰 붙이기
            .addInterceptor(loggingInterceptor)       // 로그 출력
            .build()
    }

    // retrofit : 한개 생성해서 공유함.
    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideServerApi(retrofit: Retrofit): ServerApi {
        return retrofit.create(ServerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

}