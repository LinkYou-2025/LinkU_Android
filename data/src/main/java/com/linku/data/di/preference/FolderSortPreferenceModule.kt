package com.linku.data.di.preference

import android.content.Context
import com.linku.data.preference.FolderSortPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** 상위 폴더 정렬 설정 저장소를 애플리케이션 범위로 제공합니다. */
@Module
@InstallIn(SingletonComponent::class)
object FolderSortPreferenceModule {
    /**
     * 폴더 정렬 설정 저장소를 생성합니다.
     *
     * @param context 애플리케이션 Context입니다.
     * @return 애플리케이션에서 공유할 [FolderSortPreference]입니다.
     */
    @Provides
    @Singleton
    fun provideFolderSortPreference(
        @ApplicationContext context: Context,
    ): FolderSortPreference = FolderSortPreference(context)
}
