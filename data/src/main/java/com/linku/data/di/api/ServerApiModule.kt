package com.linku.data.di.api

import android.util.Log
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

/**
 * 서버 통신에 사용하는 Retrofit, OkHttp 및 API 구현체를 Hilt 싱글턴으로 제공합니다.
 *
 * 인증이 필요 없는 공개 클라이언트와 액세스 토큰을 사용하는 인증 클라이언트를 분리하여
 * 각 API가 알맞은 네트워크 구성을 주입받도록 합니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object ServerApiModule {

    /**
     * 빌드 유형에 맞게 HTTP 요청과 응답을 기록하는 로깅 인터셉터를 제공합니다.
     *
     * 디버그 빌드에서는 본문을 포함하는 [HttpLoggingInterceptor.Level.BODY]를 사용하고,
     * 릴리스 빌드에서는 [HttpLoggingInterceptor.Level.NONE]으로 로그를 비활성화합니다.
     * 출력 전 [SensitiveHttpLogSanitizer]를 적용하여 Bearer 인증 정보, 초대 URL 경로 토큰 및
     * `token` 쿼리 값을 마스킹합니다.
     *
     * @return 현재 빌드 유형의 로그 수준과 민감 정보 정제 처리가 적용된 인터셉터
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor { message ->
            Log.d("OkHttp", SensitiveHttpLogSanitizer.sanitize(message))
        }.apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

    /**
     * 로그인, 회원가입 및 토큰 재발급처럼 인증 헤더가 필요하지 않은 Retrofit을 제공합니다.
     *
     * @param loggingInterceptor 빌드 유형에 맞게 HTTP 로그를 처리하는 인터셉터
     * @param moshi 서버 응답과 요청 본문을 변환하는 Moshi 인스턴스
     * @return [PublicClient]로 구분되는 인증 불필요 Retrofit
     */
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

    /**
     * 액세스 토큰 인증이 필요한 서버 API용 Retrofit을 제공합니다.
     *
     * 요청에 인증 헤더가 없을 때 저장된 액세스 토큰을 추가하고, 인증 실패 시
     * [TokenAuthenticator]가 토큰 갱신 흐름을 처리하도록 구성합니다.
     *
     * @param loggingInterceptor 빌드 유형에 맞게 HTTP 로그를 처리하는 인터셉터
     * @param authPreference 저장된 액세스 토큰을 제공하는 인증 환경설정
     * @param tokenAuthenticator 인증 실패 시 토큰 갱신과 요청 재시도를 처리하는 인증자
     * @param moshi 서버 응답과 요청 본문을 변환하는 Moshi 인스턴스
     * @return [AuthClient]로 구분되는 인증 필요 Retrofit
     */
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

    /**
     * 인증 헤더가 필요하지 않은 인증 API 구현체를 제공합니다.
     *
     * @param retrofit [PublicClient]로 구분된 공개 Retrofit
     * @return 로그인, 회원가입 및 토큰 재발급 요청에 사용하는 [AuthApi]
     */
    @Provides
    @Singleton
    fun provideAuthApi(@PublicClient retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    /**
     * 액세스 토큰 인증이 적용된 서버 API 구현체를 제공합니다.
     *
     * @param retrofit [AuthClient]로 구분된 인증 Retrofit
     * @return 인증이 필요한 서버 요청에 사용하는 [ServerApi]
     */
    @Provides
    @Singleton
    fun provideServerApi(@AuthClient retrofit: Retrofit): ServerApi =
        retrofit.create(ServerApi::class.java)
}
