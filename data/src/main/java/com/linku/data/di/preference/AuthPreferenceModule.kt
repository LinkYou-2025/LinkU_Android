package com.linku.data.di.preference

import android.content.Context
import com.linku.data.implementation.preference.AuthPreferenceImpl
import com.linku.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AuthPreferenceModule {
    @Provides
    @Singleton
    fun provideAuthPreference(
        @ApplicationContext context: Context
    ): AuthPreference {
        return AuthPreferenceImpl(context)
    }
}