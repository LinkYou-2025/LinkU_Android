package com.linku.data.di.repository

import com.linku.core.repository.CategoryRepository
import com.linku.data.implementation.repository.CategoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CategoryRepositoryModule {

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun provideLinkuRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository
}
