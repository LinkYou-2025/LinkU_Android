package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class JoinDTO (

    @Json(name = "nickName")
    val nickName: String,

    @Json(name = "email")
    val email: String,

    @Json(name = "password")
    val password: String,

    @Json(name = "gender")
    val gender: Int,

    @Json(name = "jobId")
    val jobId: Int,

    @Json(name = "purposeList")
    val purposeList: List<String>,

    @Json(name = "interestList")
    val interestList: List<String>

)