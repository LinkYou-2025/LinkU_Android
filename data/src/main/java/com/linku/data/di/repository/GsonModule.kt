package com.linku.data.di.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.linku.data.serializer.LocalDateSerializer
import com.linku.data.serializer.LocalDateTimeSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GsonModule {
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
            .create()
    }
}