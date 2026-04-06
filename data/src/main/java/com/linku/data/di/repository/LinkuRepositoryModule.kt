package com.linku.data.di.repository

import com.linku.core.repository.LinkuRepository
import com.linku.data.api.ServerApi
import com.linku.data.implementation.repository.LinkuRepositoryImpl
import com.linku.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LinkuRepositoryModule {

    @Provides
    @Singleton
    fun provideLinkuRepository(
        serverApi: ServerApi,
        authPreference: AuthPreference
    ): LinkuRepository {
        return LinkuRepositoryImpl(
            serverApi = serverApi,
            authPreference = authPreference
        )
    }
}