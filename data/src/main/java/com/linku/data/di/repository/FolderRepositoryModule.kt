package com.linku.data.di.repository

import com.linku.core.repository.FolderRepository
import com.linku.data.implementation.repository.FolderRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FolderRepositoryModule {

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindFolderRepository(
        impl: FolderRepositoryImpl
    ): FolderRepository
}