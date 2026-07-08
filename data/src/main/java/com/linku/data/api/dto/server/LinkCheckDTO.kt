package com.linku.data.api.dto.server

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LinkCheckDTO(
    @SerializedName("userId")
    val userId: Long?,

    @SerializedName("linkuId")
    val linkuId: Long?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("memo")
    val memo: String?,

    @SerializedName("emotionId")
    val emotionId: Long?,

    @SerializedName("createdAt")
    val createdAt: String?,

    @SerializedName("updatedAt")
    val updatedAt: String?,

    @SerializedName("exist")
    val exist: Boolean?
)