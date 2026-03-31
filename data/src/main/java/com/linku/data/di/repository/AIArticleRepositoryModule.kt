package com.linku.data.di.repository

import com.linku.core.repository.AIArticleRepository
import com.linku.data.api.ServerApi
import com.linku.data.implementation.repository.AIArticleRepositoryImpl
import com.linku.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AIArticleRepositoryModule {

    @Provides
    @Singleton
    fun provideLinkuRepository(
        serverApi: ServerApi,
        authPreference: AuthPreference
    ): AIArticleRepository {
        return AIArticleRepositoryImpl(
            serverApi = serverApi,
            authPreference = authPreference
        )
    }
}