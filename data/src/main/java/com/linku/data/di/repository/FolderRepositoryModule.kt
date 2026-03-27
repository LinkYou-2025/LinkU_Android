package com.linku.data.di.repository

import com.linku.core.repository.FolderRepository
import com.linku.data.api.ServerApi
import com.linku.data.implementation.repository.FolderRepositoryImpl
import com.linku.data.preference.AuthPreference
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