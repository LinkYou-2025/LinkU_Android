package com.linku.data.di

import com.linku.core.analytics.AnalyticsLogger
import com.linku.data.analytics.FirebaseAnalyticsLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsLogger(
        impl: FirebaseAnalyticsLogger
    ): AnalyticsLogger
}
