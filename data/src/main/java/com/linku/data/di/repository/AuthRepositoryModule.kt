package com.linku.data.di.repository

import com.linku.core.repository.AuthRepository
import com.linku.data.implementation.repository.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}