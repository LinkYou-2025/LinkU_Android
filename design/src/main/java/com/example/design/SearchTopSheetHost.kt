package com.example.design

import androidx.compose.runtime.*
import kotlin.math.min

/**
 * UI는 SearchBarTopSheet를 그대로 사용하고,
 * 이 파일은 '검색 로직 + 상태'만 제공하는 호스트입니다.
 * 큐레이션에서 검색기능에 사용합니다. -윤지-
 */
@Composable
fun SearchTopSheetHost(
    visible: Boolean,
    allItems: List<FastSearchItem>,   // 🔹 검색 대상 전체(서버/DB에서 만든 리스트)
    onDismiss: () -> Unit
) {
    // 최근 검색(최신 우선, 최대 10개)
    var recent by remember { mutableStateOf(listOf<String>()) }

    // 필터링 결과
    var filtered by remember { mutableStateOf<List<FastSearchItem>>(emptyList()) }

    // SearchBarTopSheet가 350ms 디바운스를 이미 해주므로 여기선 즉시 필터만
    val onQueryChange: (String) -> Unit = { raw ->
        val q = raw.trim()
        filtered = if (q.length < 2) emptyList() else filterAndRank(allItems, q).take(20)
    }

    val onQuerySave: (String) -> Unit = { raw ->
        val q = raw.trim()
        if (q.length >= 2) {
            recent = listOf(q) + recent.filterNot { it.equals(q, ignoreCase = true) }
            if (recent.size > 10) recent = recent.take(10)
        }
    }

    val onQueryDelete: (String) -> Unit = { t ->
        recent = recent.filterNot { it.equals(t, ignoreCase = true) }
    }

    val onQueryClear: () -> Unit = { recent = emptyList() }

    SearchBarTopSheet(
        visible = visible,
        onDismiss = onDismiss,
        onQueryChange = onQueryChange,
        onQuerySave = onQuerySave,
        onQueryDelete = onQueryDelete,
        onQueryClear = onQueryClear,
        fastSearchItems = filtered,
        recentQuerys = recent
    )
}

/** 간단 랭킹: 제목 매칭 가중치↑, 매칭 위치 빠를수록↑, 제목 짧을수록↑ */
private fun filterAndRank(items: List<FastSearchItem>, query: String): List<FastSearchItem> {
    val q = query.lowercase()
    val scored = items.mapNotNull { item ->
        val lt = item.title.lowercase()
        val lu = item.url.lowercase()
        val ti = lt.indexOf(q)
        val ui = lu.indexOf(q)
        if (ti < 0 && ui < 0) return@mapNotNull null

        val titleHit = if (ti >= 0) 1 else 0
        val urlHit = if (ui >= 0) 1 else 0
        val positionScore = when {
            ti >= 0 -> 100 - min(ti, 100)
            ui >= 0 -> 50 - min(ui, 50)
            else -> 0
        }
        val lengthScore = 50 - min(item.title.length, 50)
        val score = (titleHit * 200) + (urlHit * 50) + positionScore + lengthScore
        item to score
    }

    return scored.sortedWith(
        compareByDescending<Pair<FastSearchItem, Int>> { it.second }
            .thenBy { it.first.title.lowercase() }
    ).map { it.first }
}