package com.linku.data.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.linku.core.model.ParentFolderSort
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.folderDataStore by preferencesDataStore(name = "folder_prefs")

/**
 * 기기에 저장되는 폴더 화면 설정을 관리합니다.
 *
 * 정렬 기준은 표시 문구가 아닌 API `sort` 쿼리 값으로 저장하며, 읽을 수 없는 저장소나
 * 알 수 없는 값은 기본값인 [ParentFolderSort.NAME]으로 복구합니다.
 *
 * @param context `folder_prefs` Preferences DataStore에 접근할 애플리케이션 Context입니다.
 */
class FolderSortPreference(
    private val context: Context,
) {
    private object Keys {
        val PARENT_FOLDER_SORT_QUERY = stringPreferencesKey("parent_folder_sort_query")
    }

    /** 저장된 상위 폴더 정렬 기준이며, 값이 없으면 가나다 순을 방출합니다. */
    val parentFolderSort: Flow<ParentFolderSort> = context.folderDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            ParentFolderSort.fromQuery(preferences[Keys.PARENT_FOLDER_SORT_QUERY])
        }

    /**
     * 상위 폴더 정렬 기준의 API 쿼리 값을 기기에 저장합니다.
     *
     * @param sort 저장할 정렬 기준입니다.
     */
    suspend fun setParentFolderSort(sort: ParentFolderSort) {
        context.folderDataStore.edit { preferences ->
            preferences[Keys.PARENT_FOLDER_SORT_QUERY] = sort.query
        }
    }
}
