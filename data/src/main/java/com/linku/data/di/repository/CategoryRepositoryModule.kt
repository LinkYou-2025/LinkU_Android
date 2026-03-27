package com.linku.data.di.repository

import com.linku.core.repository.CategoryRepository
import com.linku.data.api.ServerApi
import com.linku.data.implementation.repository.CategoryRepositoryImpl
import com.linku.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CategoryRepositoryModule {

    @Provides
    @Singleton
    fun provideLinkuRepository(
        serverApi: ServerApi,
        authPreference: AuthPreference
    ): CategoryRepository {
        return CategoryRepositoryImpl(
            serverApi = serverApi,
            authPreference = authPreference
        )
    }
}