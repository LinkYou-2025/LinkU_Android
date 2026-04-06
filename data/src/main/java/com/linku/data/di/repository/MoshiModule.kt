package com.linku.data.di.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.linku.data.serializer.LocalDateSerializer
import com.linku.data.serializer.LocalDateTimeSerializer
import com.linku.data.serializer.OffsetDateTimeSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MoshiModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(OffsetDateTimeSerializer())
            .add(LocalDateSerializer())
            .add(LocalDateTimeSerializer())
            .build()
    }
}