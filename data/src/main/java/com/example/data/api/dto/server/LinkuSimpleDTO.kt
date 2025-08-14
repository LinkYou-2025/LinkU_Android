package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class LinkuSimpleDTO(

    @Json(name = "linkuId")
    val linkuId: Long,

    @Json(name = "categoryId")
    val categoryId: Long,

    @Json(name = "memo")
    val memo: String?,

    @Json(name = "emotionId")
    val emotionId: Long,

    @Json(name = "title")
    val title: String,

    @Json(name = "domain")
    val domain: String?,

    @Json(name = "domainImageUrl")
    val domainImageUrl: String?,

    @Json(name = "linkuImageUrl")
    val linkuImageUrl: String?

)