package com.linku.data.implementation.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.linku.core.model.search.RecentQuery
import com.linku.core.repository.RecentSearchRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecentSearchRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val moshi: Moshi,
    private val maxSize: Int = 20
) : RecentSearchRepository {

    private val KEY = stringPreferencesKey("recent_search_json")
    private val type = Types.newParameterizedType(List::class.java, RecentQuery::class.java)
    private val adapter = moshi.adapter<List<RecentQuery>>(type)

    override fun observe(limit: Int): Flow<List<RecentQuery>> =
        dataStore.data.map { pref ->
            val raw = pref[KEY].orEmpty()
            Log.d("RecentSearchRepositoryImpl", "observe: $raw")

            if (raw.isBlank()) emptyList()
            else runCatching { adapter.fromJson(raw) ?: emptyList() }.getOrElse { emptyList() }
                .sortedByDescending { it.timestamp }
                .take(limit)
        }

    override suspend fun add(query: String) {
        Log.d("RecentSearchRepositoryImpl", "add: $query")

        val text = query.trim()
        if (text.isBlank()) return
        val now = System.currentTimeMillis()
        dataStore.edit { pref ->
            Log.d("RecentSearchRepositoryImpl", "add: ${pref[KEY]}")

            val current = pref[KEY].orEmpty()
            val list = if (current.isBlank()) emptyList() else
                runCatching { adapter.fromJson(current) ?: emptyList() }.getOrElse { emptyList() }

            // 대소문자 무시 중복 제거
            val filtered = list.filterNot { it.text.equals(text, ignoreCase = true) }

            val updated = (listOf(RecentQuery(text, now)) + filtered).take(maxSize)

            Log.d("RecentSearchRepositoryImpl", "add: $updated")

            pref[KEY] = adapter.toJson(updated)
        }
    }

    override suspend fun remove(query: String) {
        dataStore.edit { pref ->
            Log.d("RecentSearchRepositoryImpl", "remove: $query")

            val current = pref[KEY].orEmpty()
            val list = if (current.isBlank()) emptyList() else
                runCatching { adapter.fromJson(current) ?: emptyList() }.getOrElse { emptyList() }
            pref[KEY] = adapter.toJson(list.filterNot { it.text == query })
        }
    }

    override suspend fun clear() {
        dataStore.edit { it.remove(KEY) }
    }
}
