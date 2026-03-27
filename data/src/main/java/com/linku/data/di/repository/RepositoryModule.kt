package com.linku.data.di.repository

import com.linku.core.repository.AuthRepository
import com.linku.core.repository.UserRepository
import com.linku.data.implementation.repository.AuthRepositoryImpl
import com.linku.data.implementation.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/* UserRepository 달라고 하면 UserRepositoryImpl 주는 모둘*/
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}

