package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class LinkuUpdateDTO(

    @Json(name = "folderId")
    val folderId: Long? = null,

    @Json(name = "categoryId")
    val categoryId: Long,

    @Json(name = "linku")
    val linku: String,

    @Json(name = "memo")
    val memo: String,

    @Json(name = "emotionId")
    val emotionId: Long,

    @Json(name = "domainId")
    val domainId: Long,

    @Json(name = "title")
    val title: String

)