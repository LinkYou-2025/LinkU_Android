package com.linku.data.di.repository

import com.linku.core.repository.CurationRepository
import com.linku.data.api.CurationApi
import com.linku.data.implementation.repository.CurationRepositoryImpl
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
): CurationRepository {
    return CurationRepositoryImpl(
        curationApi = curationApi,
    )
}
}