package com.linku.data.di.api

import com.linku.data.api.AuthClient
import com.linku.data.api.CurationApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CurationApiModule {

    @Provides
    @Singleton
    fun provideCurationApi(
        @AuthClient retrofit: Retrofit
    ): CurationApi =
        retrofit.create(CurationApi::class.java)

}