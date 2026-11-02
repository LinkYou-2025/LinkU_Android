package com.linku

import com.linku.core.deeplink.DeepLinkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** 앱 모듈의 빌드 설정을 셸과 딥링크 구현에 제공합니다. */
@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    /**
     * 현재 빌드 변형의 서버 호스트를 담은 [AppConfig]를 제공합니다.
     *
     * @param deepLinkConfig URI 생성기와 내비게이션이 공유하는 딥링크 설정
     * @return 빌드 설정을 담은 [AppConfig]
     */
    @Provides
    @Singleton
    fun provideAppConfig(deepLinkConfig: DeepLinkConfig): AppConfig = object : AppConfig {
        override val deepLinkHost: String = deepLinkConfig.host
    }

    /**
     * 현재 빌드 변형의 딥링크 호스트를 제공합니다.
     *
     * Manifest의 SERVER_HOST placeholder와 같은 빌드 설정을 사용해 URI 매칭과 공유 URL 생성의
     * 호스트가 일치하도록 합니다. API 기본 주소와는 독립적인 설정입니다.
     */
    @Provides
    @Singleton
    fun provideDeepLinkConfig(): DeepLinkConfig = DeepLinkConfig(host = BuildConfig.SERVER_HOST)
}
