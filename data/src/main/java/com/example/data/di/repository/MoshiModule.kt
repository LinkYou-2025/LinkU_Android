package com.example.data.di.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.data.serializer.LocalDateSerializer
import com.example.data.serializer.LocalDateTimeSerializer
import com.example.data.serializer.OffsetDateTimeSerializer
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