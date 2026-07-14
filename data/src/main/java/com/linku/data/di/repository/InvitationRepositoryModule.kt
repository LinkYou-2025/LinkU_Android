package com.linku.data.di.repository

import com.linku.core.repository.InvitationRepository
import com.linku.data.implementation.repository.InvitationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InvitationRepositoryModule {

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindInvitationRepository(
        impl: InvitationRepositoryImpl
    ): InvitationRepository
}
