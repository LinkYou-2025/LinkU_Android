package com.linku.data.di.api

import com.linku.data.api.AuthClient
import com.linku.data.api.SearchHistoryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchHistoryApiModule {

    @Provides
    @Singleton
    fun provideSearchHistoryApi(
        @AuthClient retrofit: Retrofit
    ): SearchHistoryApi =
        retrofit.create(SearchHistoryApi::class.java)
}
