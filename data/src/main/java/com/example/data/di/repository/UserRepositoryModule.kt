package com.example.data.di.repository

import com.example.core.repository.UserRepository
import com.example.data.api.ServerApi
import com.example.data.implementation.repository.UserRepositoryImpl
import com.example.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserRepositoryModule {
    @Provides
    @Singleton
    fun provideUserRepository(
        serverApi: ServerApi,
        authPreference: AuthPreference
    ): UserRepository {
        return UserRepositoryImpl(
            serverApi = serverApi,
            authPreference = authPreference,
        )
    }
}