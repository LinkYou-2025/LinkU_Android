package com.linku.data.di.repository

import com.linku.core.repository.AIArticleRepository
import com.linku.data.implementation.repository.AIArticleRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AIArticleRepositoryModule {

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun provideLinkuRepository(
        impl: AIArticleRepositoryImpl
    ): AIArticleRepository
}
