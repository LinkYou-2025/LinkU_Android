package com.linku.data.di.preference

import android.content.Context
import com.linku.data.implementation.preference.NotificationPreferenceImpl
import com.linku.data.preference.NotificationPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationPreferenceModule {
    @Provides
    @Singleton
    fun provideNotificationPreference(
        @ApplicationContext context: Context
    ): NotificationPreference {
        return NotificationPreferenceImpl(context)
    }
}