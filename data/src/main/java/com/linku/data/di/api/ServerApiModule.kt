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
     * 긴 JSON을 Logcat의 단일 항목 크기 제한보다 작은 조각으로 나누어 기록합니다.
     *
     * UTF-16 서로게이트 쌍을 보존하며, 동시 요청의 조각이 서로 섞이지 않도록 동기화합니다.
     * 조각의 시작/끝 위치와 전체 길이를 기록하여 수집 후 원문을 복원할 수 있습니다.
     *
     * @param message OkHttp 로깅 인터셉터가 전달한 헤더 또는 본문 문자열입니다.
     */
    @Synchronized
    private fun logHttpMessage(message: String) {
        if (message.isEmpty()) {
            android.util.Log.i("OkHttp", "[chunk 0:0/0] ")
            return
        }

        var start = 0
        while (start < message.length) {
            // 한글의 UTF-8 크기와 로그 접두부를 고려하여 조각을 900 코드 단위로 제한합니다.
            var end = minOf(start + 900, message.length)
            if (end < message.length && message[end - 1].isHighSurrogate() &&
                message[end].isLowSurrogate()
            ) {
                end--
            }
            android.util.Log.i(
                "OkHttp",
                "[chunk $start:$end/${message.length}] ${message.substring(start, end)}",
            )
            start = end
        }
    }

    /**
     * 임시 진단용으로 debug와 release의 요청/응답 본문을 `OkHttp` 태그에 기록합니다.
     *
     * 인증 및 쿠키 헤더는 가리지만 JSON 본문과 이미지 URL은 원문 그대로 기록합니다.
     * 운영 서버의 썸네일 응답을 수집한 후 release 본문 로깅을 다시 비활성화해야 합니다.
     */
    private fun httpLoggingInterceptor() = HttpLoggingInterceptor(::logHttpMessage).apply {
        redactHeader("Authorization")
        redactHeader("Proxy-Authorization")
        redactHeader("Cookie")
        redactHeader("Set-Cookie")
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * 로그인, 회원가입 및 토큰 재발급처럼 인증 헤더가 필요하지 않은 Retrofit을 제공합니다.
     *
     * 임시 진단 중에는 [httpLoggingInterceptor]로 release에서도 요청/응답 본문을 기록합니다.
     *
     * @param moshi 서버 응답과 요청 본문을 변환하는 Moshi 인스턴스
     * @return [PublicClient]로 구분되는 인증 불필요 Retrofit
     */
    @Provides
    @Singleton
    @PublicClient
    fun providePublicRetrofit(
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.SERVER_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(
            OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor())
                .build()
        )
        .build()

    /**
     * 액세스 토큰 인증이 필요한 서버 API용 Retrofit을 제공합니다.
     *
     * 요청에 인증 헤더가 없을 때 저장된 액세스 토큰을 추가하고, 인증 실패 시
     * [TokenAuthenticator]가 토큰 갱신 흐름을 처리하도록 구성합니다.
     * 임시 진단 중에는 release에서도 요청/응답 본문을 기록하고 인증·쿠키 헤더는 가립니다.
     *
     * @param authPreference 저장된 액세스 토큰을 제공하는 인증 환경설정
     * @param tokenAuthenticator 인증 실패 시 토큰 갱신과 요청 재시도를 처리하는 인증자
     * @param moshi 서버 응답과 요청 본문을 변환하는 Moshi 인스턴스
     * @return [AuthClient]로 구분되는 인증 필요 Retrofit
     */
    @Provides
    @Singleton
    @AuthClient
    fun provideAuthRetrofit(
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
                    // 호출자가 이미 지정한 인증 헤더는 변경하지 않고 그대로 사용합니다.
                    if (chain.request().header("Authorization") != null) {
                        return@addInterceptor chain.proceed(chain.request())
                    }

                    // OkHttp 인터셉터는 동기 계약이므로 저장소의 suspend 조회를 현재 스레드에서 연결합니다.
                    val token = runBlocking { authPreference.getAccessToken() }
                    val request = if (!token.isNullOrBlank()) {
                        chain.request().newBuilder()
                            .header("Authorization", "Bearer $token")
                            .build()
                    } else chain.request()
                    chain.proceed(request)
                }
                .addInterceptor(httpLoggingInterceptor())
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
