package com.linku.data.di.repository

import com.linku.core.repository.UserRepository
import com.linku.data.implementation.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}

/*
* . UserRepository 인터페이스와 UserRepositoryImpl 구현체 연결
*/