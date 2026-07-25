package com.linku.design

import androidx.compose.runtime.*
import androidx.paging.PagingData
import com.linku.design.top.search.RecentSearchItem
import com.linku.design.top.search.SearchBarTopSheet
import com.linku.design.top.search.SearchBarUiState
import com.linku.design.top.search.SearchResultItem
import kotlinx.coroutines.flow.flowOf
import kotlin.math.min

/**
 * UI는 SearchBarTopSheet를 그대로 사용하고,
 * 이 파일은 '검색 로직 + 상태'만 제공하는 호스트입니다.
 * 큐레이션에서 검색기능에 사용합니다. -윤지-
 */
@Composable
fun SearchTopSheetHost(
    visible: Boolean,
    allItems: List<SearchResultItem>,   // 🔹 검색 대상 전체(서버/DB에서 만든 리스트)
    onDismiss: () -> Unit
) {
    // 최근 검색(최신 우선, 최대 10개)
    var recent by remember { mutableStateOf(listOf<RecentSearchItem>()) }
    var nextRecentId by remember { mutableLongStateOf(0L) }

    // 필터링 결과
    var filtered by remember { mutableStateOf<List<SearchResultItem>>(emptyList()) }

    // SearchBarTopSheet가 350ms 디바운스를 이미 해주므로 여기선 즉시 필터만
    val onQueryChange: (String) -> Unit = { raw ->
        val q = raw.trim()
        filtered = if (q.length < 2) emptyList() else filterAndRank(allItems, q).take(20)
    }

    val onQuerySave: (String) -> Unit = { raw ->
        val q = raw.trim()
        if (q.length >= 2) {
            val item = recent.firstOrNull {
                it.keyword.equals(q, ignoreCase = true)
            } ?: RecentSearchItem(
                searchHistoryId = nextRecentId++,
                keyword = q
            )
            recent = listOf(item) + recent.filterNot {
                it.keyword.equals(q, ignoreCase = true)
            }
            if (recent.size > 10) recent = recent.take(10)
        }
    }

    val onQueryDelete: (Long) -> Unit = { searchHistoryId ->
        recent = recent.filterNot { it.searchHistoryId == searchHistoryId }
    }

    val onQueryClear: () -> Unit = { recent = emptyList() }
    val searchResults = remember(filtered) {
        flowOf(PagingData.from(filtered))
    }

    SearchBarTopSheet(
        visible = visible,
        onDismiss = onDismiss,
        onQueryChange = onQueryChange,
        onQuerySave = onQuerySave,
        onQueryDelete = onQueryDelete,
        onQueryClear = onQueryClear,
        searchResults = searchResults,
        uiState = SearchBarUiState(recentQueries = recent),
        onLinkClick = {}
    )
}

/** 간단 랭킹: 제목 매칭 가중치↑, 매칭 위치 빠를수록↑, 제목 짧을수록↑ */
private fun filterAndRank(items: List<SearchResultItem>, query: String): List<SearchResultItem> {
    val q = query.lowercase()
    val scored = items.mapNotNull { item ->
        val lt = item.title.lowercase()
        val ti = lt.indexOf(q)
        if (ti < 0) return@mapNotNull null

        val positionScore = 100 - min(ti, 100)
        val lengthScore = 50 - min(item.title.length, 50)
        val score = 200 + positionScore + lengthScore
        item to score
    }

    return scored.sortedWith(
        compareByDescending<Pair<SearchResultItem, Int>> { it.second }
            .thenBy { it.first.title.lowercase() }
    ).map { it.first }
}
