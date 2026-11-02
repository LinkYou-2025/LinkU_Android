package com.linku.file.di

import com.linku.core.deeplink.DeepLinkConfig
import com.linku.test.file.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** 앱 모듈을 포함하지 않는 파일 기능 독립 실행 호스트의 딥링크 설정을 제공합니다. */
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkConfigModule {

    /** 메인 앱과 같은 debug/release 설정 소스로 생성한 공유 링크 호스트를 제공합니다. */
    @Provides
    @Singleton
    fun provideDeepLinkConfig(): DeepLinkConfig = DeepLinkConfig(host = BuildConfig.SERVER_HOST)
}
