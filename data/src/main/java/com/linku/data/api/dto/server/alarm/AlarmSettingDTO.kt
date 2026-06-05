package com.linku.data.api.dto.server.alarm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlarmSettingDTO(
    @field:Json(name = "isAllEnabled")
    val isAllEnabled: Boolean,
    @field:Json(name = "isLinkEnabled")
    val isLinkEnabled: Boolean,
    @field:Json(name = "isFolderEnabled")
    val isFolderEnabled: Boolean,
    @field:Json(name = "isCurationEnabled")
    val isCurationEnabled: Boolean,
    @field:Json(name = "isNoticeEnabled")
    val isNoticeEnabled: Boolean
)
