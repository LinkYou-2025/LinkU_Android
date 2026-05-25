package com.linku.data.di.repository

import com.linku.core.repository.LinkuRepository
import com.linku.data.implementation.repository.LinkuRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class LinkuRepositoryModule {

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindFolderRepository(
        impl: LinkuRepositoryImpl
    ): LinkuRepository
}
