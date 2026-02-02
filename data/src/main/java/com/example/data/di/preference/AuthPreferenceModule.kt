package com.example.data.di.preference

import android.content.Context
import com.example.data.implementation.preference.AuthPreferenceImpl
import com.example.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// 로그인 토큰을 일고 쓸 수 있는 기능을 만들었음.
@Module
@InstallIn(SingletonComponent::class)
// 앱 전체에서 공유하는 레시피임.
object AuthPreferenceModule {
    @Provides
    @Singleton // 단 하나의 유일한 객체를 만들기 위한 코드 패턴.
    // 실제 구현체는 AuthPreferenceImpl을 하나만 만들어서 줌.
    fun provideAuthPreference(
        @ApplicationContext context: Context
        // 저장소를 만들려면 안드로이드 시스템 환경 정보인(컨텍스트)가 필요함.
    ): AuthPreference {
        return AuthPreferenceImpl(context) //컨텍스트를 넣어서 실제 저장소를 조립했어. 이제 이걸 쓰도록 해.
    }
}