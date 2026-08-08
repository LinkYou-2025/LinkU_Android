package com.linku.core.di

import com.linku.core.repository.AlarmRepository
import com.linku.core.usecase.FirstPushAlarmAllowedUseCase
import com.linku.core.usecase.ReRegisterFcmTokenUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * UseCase 의존성을 제공하기 위한 Dagger Hilt 모듈입니다.
 *
 * 애플리케이션 전반에서 사용되는 다양한 비즈니스 로직 UseCase의 인스턴스를 생성하고 관리하며,
 * [SingletonComponent] 내에서 싱글톤으로 제공되도록 보장합니다.
 *
 * 링큐 개발자분들은 다 쌉 천재분들이라(진심임 항상 많이배웁니다 지금 작업중인 시점이 새벽이라 감성터져서 이런 주석도 쓰네요)
 * 이미 아시는 내용이겠지만 클린 아키텍쳐에는
 * Repository 계층과 Presentation 계층 사이에 UseCase계층이 있습니다.
 * 일반적인 api 연동작업이라면 UseCase를 만드는 것은 오버엔지니어링이라고 생각하지만,
 * 제가 이번에 맡은 알림 관련 요구사항에서
 * fcm에서 현재 토큰을 가져옴 -> 서버에 토큰 등록 api 호출 -> 푸사알림 활성화 api 호출 이라는 일련의
 * 복잡한 네트워크 작업이 한 번에 진행되어야 하기에.. 그래서 UseCase계층을 신설해 봤습니다.
 *
 * 다른 기능에서도 유스케이스가 필요한 경우가 생긴다면, 이 모듈을 쓰면 될 듯 해유 :)
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideFirstPushAlarmAllowedUseCase(
        alarmRepository: AlarmRepository
    ): FirstPushAlarmAllowedUseCase {
        return FirstPushAlarmAllowedUseCase(alarmRepository)
    }

    @Provides
    @Singleton
    fun provideReRegisterFcmTokenUseCase(
        alarmRepository: AlarmRepository
    ): ReRegisterFcmTokenUseCase {
        return ReRegisterFcmTokenUseCase(alarmRepository)
    }

}