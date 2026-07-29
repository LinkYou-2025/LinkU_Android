package com.linku.data.di.api

import com.linku.data.BuildConfig
import com.linku.data.api.CurationApi
import com.linku.data.preference.AuthPreference
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * 큐레이션 서버 통신에 사용하는 전용 OkHttp, Retrofit 및 API 구현체를 제공합니다.
 *
 * 큐레이션 응답 생성 시간을 고려한 전용 타임아웃과 액세스 토큰 부착 정책을 한곳에서 관리하며,
 * 요청·응답 본문이나 인증 정보를 기록하는 HTTP 로거는 설치하지 않습니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object CurationApiModule {

    /**
     * 큐레이션 요청에 맞춘 타임아웃과 액세스 토큰 부착 정책을 가진 OkHttp 클라이언트를 제공합니다.
     *
     * @param authPreference 현재 사용자의 액세스 토큰을 제공하는 인증 환경설정
     * @return `curation` 한정자로 구분되는 큐레이션 전용 OkHttp 클라이언트
     */
    @Provides
    @Singleton
    @Named("curation")
    fun provideCurationOkHttpClient(
        authPreference: AuthPreference
    ): OkHttpClient =
        OkHttpClient.Builder()
            // 비교적 오래 걸릴 수 있는 큐레이션 생성 요청에만 전용 타임아웃을 적용합니다.
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addNetworkInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                // 네트워크 인터셉터는 동기 계약이므로 저장소의 suspend 조회를 현재 스레드에서 연결합니다.
                val token = runBlocking { authPreference.getAccessToken() }
                if (!token.isNullOrBlank()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()

    /**
     * 큐레이션 전용 OkHttp 클라이언트를 사용하는 Retrofit을 제공합니다.
     *
     * @param moshi 큐레이션 요청과 응답 본문을 변환하는 Moshi 인스턴스
     * @param client `curation` 한정자로 구분된 큐레이션 전용 OkHttp 클라이언트
     * @return `curation` 한정자로 구분되는 큐레이션 전용 Retrofit
     */
    @Provides
    @Singleton
    @Named("curation")
    fun provideCurationRetrofit(
        moshi: Moshi,
        @Named("curation") client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()

    /**
     * 큐레이션 요청에 사용하는 API 구현체를 제공합니다.
     *
     * @param retrofit `curation` 한정자로 구분된 큐레이션 전용 Retrofit
     * @return 큐레이션 요청을 수행하는 [CurationApi] 구현체
     */
    @Provides
    @Singleton
    fun provideCurationApi(
        @Named("curation") retrofit: Retrofit
    ): CurationApi =
        retrofit.create(CurationApi::class.java)
}
