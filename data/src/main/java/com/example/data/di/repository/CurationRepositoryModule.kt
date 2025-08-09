package com.example.data.di.repository

import android.content.Context
import com.example.core.repository.CurationRepository
import com.example.data.api.ServerApi
import com.example.data.implementation.preference.AuthPreferenceImpl
import com.example.data.implementation.repository.CurationRepositoryImpl
import com.example.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CurationRepositoryModule {


    @Provides
    @Singleton
    fun provideLinkuRepository(
        serverApi: ServerApi,
        authPreference: AuthPreference
    ): CurationRepository {
        return CurationRepositoryImpl(
            serverApi = serverApi,
            authPreference = authPreference
        )
    }
}