package com.linku.data.mapper

import com.linku.core.model.search.RecentQuery
import com.linku.data.api.dto.search.SearchHistoryItemResponseDTO

fun SearchHistoryItemResponseDTO.toDomain(): RecentQuery =
    RecentQuery(
        searchHistoryId = searchHistoryId,
        keyword = keyword
    )
