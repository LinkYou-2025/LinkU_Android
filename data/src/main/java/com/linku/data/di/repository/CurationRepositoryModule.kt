package com.linku.data.di.repository

import com.linku.core.repository.CurationRepository
import com.linku.data.api.ServerApi
import com.linku.data.implementation.repository.CurationRepositoryImpl
import com.linku.data.preference.AuthPreference
import dagger.Module
import com.linku.data.api.CurationApi
import com.squareup.moshi.Moshi
import dagger.Provides
import dagger.hilt.InstallIn
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