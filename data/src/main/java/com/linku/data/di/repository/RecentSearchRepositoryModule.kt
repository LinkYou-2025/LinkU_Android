package com.linku.data.di.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.linku.core.repository.RecentSearchRepository
import com.linku.data.implementation.repository.RecentSearchRepositoryImpl
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.recentDataStore by preferencesDataStore(name = "recent_search")

@Module
@InstallIn(SingletonComponent::class)
object RecentSearchRepositoryModule {

    @Provides
    @Singleton
    fun provideRecentSearchDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.recentDataStore

    @Provides
    @Singleton
    fun provideRecentSearchRepository(
        dataStore: DataStore<Preferences>,
        moshi: Moshi
    ): RecentSearchRepository {
        return RecentSearchRepositoryImpl(
            dataStore = dataStore,
            moshi = moshi,
            maxSize = 20
        )
    }
}
