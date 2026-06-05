package com.linku.core.di

import android.content.Context
import com.linku.core.system.FcmTokenController
import com.linku.core.system.PermissionChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SystemModule {

    @Provides
    @Singleton
    fun providePermissionChecker(
        @ApplicationContext context: Context
    ): PermissionChecker {
        return PermissionChecker(context)
    }

    @Provides
    @Singleton
    fun provideFcmTokenController(): FcmTokenController {
        return FcmTokenController()
    }
}