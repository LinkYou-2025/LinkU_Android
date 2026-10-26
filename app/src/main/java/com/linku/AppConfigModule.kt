package com.linku

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** 앱 모듈의 빌드 설정을 [AppConfig]로 제공합니다. */
@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    /**
     * 현재 빌드 변형의 서버 호스트를 담은 [AppConfig]를 제공합니다.
     *
     * @return 빌드 설정을 담은 [AppConfig]
     */
    @Provides
    @Singleton
    fun provideAppConfig(): AppConfig = object : AppConfig {
        override val deepLinkHost: String = BuildConfig.SERVER_HOST
    }
}
