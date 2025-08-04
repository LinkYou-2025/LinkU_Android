package com.example.data.di.repository

import com.example.core.repository.FolderRepository
import com.example.data.api.ServerApi
import com.example.data.implementation.repository.FolderRepositoryImpl
import com.example.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FolderRepositoryModule {

    @Provides
    @Singleton
    fun provideLinkuRepository(
        serverApi: ServerApi,
        authPreference: AuthPreference
    ): FolderRepository {
        return FolderRepositoryImpl(
            serverApi = serverApi,
            authPreference = authPreference
        )
    }
}