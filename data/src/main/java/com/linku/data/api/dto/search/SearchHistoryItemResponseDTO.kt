package com.linku.data.api.dto.search

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchHistoryItemResponseDTO(
    val searchHistoryId: Long,
    val keyword: String
)
