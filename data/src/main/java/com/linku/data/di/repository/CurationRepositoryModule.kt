package com.linku.data.di.repository

import com.linku.core.repository.CurationRepository
import com.linku.data.api.CurationApi
import com.linku.data.implementation.repository.CurationRepositoryImpl
import com.linku.core.preference.AuthPreference
import com.linku.data.local.CurationHistoryDao
import com.linku.data.local.ApiCacheDao
import com.squareup.moshi.Moshi
import java.time.Clock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object CurationRepositoryModule {

@Provides
@Singleton
fun provideCurationRepository(
    curationApi: CurationApi,
    historyDao: CurationHistoryDao,
    authPreference: AuthPreference,
    clock: Clock,
    apiCacheDao: ApiCacheDao,
    moshi: Moshi,
): CurationRepository {
    return CurationRepositoryImpl(
        curationApi = curationApi,
        historyDao = historyDao,
        authPreference = authPreference,
        clock = clock,
        apiCacheDao = apiCacheDao,
        moshi = moshi,
    )
}
}