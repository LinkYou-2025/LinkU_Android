package com.linku.data.di.repository

import com.linku.core.repository.RecentSearchRepository
import com.linku.data.implementation.repository.RecentSearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RecentSearchRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRecentSearchRepository(
        impl: RecentSearchRepositoryImpl
    ): RecentSearchRepository
}
