package com.example.data.di.repository

import android.content.Context
import com.example.core.repository.CurationRepository
import com.example.data.api.ServerApi
import com.example.data.implementation.preference.AuthPreferenceImpl
import com.example.data.implementation.repository.CurationRepositoryImpl
import com.example.data.preference.AuthPreference
import dagger.Module
import com.example.data.api.CurationApi
import com.squareup.moshi.Moshi
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CurationRepositoryModule {


//    @Provides
//    @Singleton
//    fun provideLinkuRepository(
//        serverApi: ServerApi,
//        authPreference: AuthPreference
//    ): CurationRepository {
//        return CurationRepositoryImpl(
//            serverApi = serverApi,
//            authPreference = authPreference
//        )
//    }
@Provides
@Singleton
fun provideCurationRepository(                // 함수명도 의미 맞게 변경 권장
    serverApi: ServerApi,
    curationApi: CurationApi,                 // ★ 추가
    authPreference: AuthPreference,
    moshi: Moshi
): CurationRepository {
    return CurationRepositoryImpl(
        serverApi = serverApi,
        curationApi = curationApi,
        authPreference = authPreference,
        moshi = moshi                  // ✅ 전달
    )
}
}