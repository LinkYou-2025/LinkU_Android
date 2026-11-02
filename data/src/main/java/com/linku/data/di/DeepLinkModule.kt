package com.linku.data.di

import com.linku.core.deeplink.InvitationLinkFactory
import com.linku.data.implementation.deeplink.DefaultInvitationLinkFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** 공통 초대 링크 생성 계약과 Android URI 기반 구현체를 연결합니다. */
@Module
@InstallIn(SingletonComponent::class)
abstract class DeepLinkModule {

    /** 앱이 제공한 호스트 설정을 사용하는 초대 링크 생성기를 제공합니다. */
    @Binds
    @Singleton
    abstract fun bindInvitationLinkFactory(
        impl: DefaultInvitationLinkFactory,
    ): InvitationLinkFactory
}
