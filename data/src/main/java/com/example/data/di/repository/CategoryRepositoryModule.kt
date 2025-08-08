package com.example.data.di.repository

import com.example.core.repository.CategoryRepository
import com.example.data.api.ServerApi
import com.example.data.implementation.repository.CategoryRepositoryImpl
import com.example.data.preference.AuthPreference
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