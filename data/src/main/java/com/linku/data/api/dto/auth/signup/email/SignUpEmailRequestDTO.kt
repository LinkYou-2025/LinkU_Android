package com.linku.data.api.dto.auth.signup.email

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignUpEmailRequestDTO (

    @field:Json(name = "nickName")
    val nickName: String,

    @field:Json(name = "email")
    val email: String,

    @field:Json(name = "password")
    val password: String,

    @field:Json(name = "gender")
    val gender: Int,

    @field:Json(name = "jobId")
    val jobId: Int,

    @field:Json(name = "purposeList")
    val purposeList: List<String>,

    @field:Json(name = "interestList")
    val interestList: List<String>,

    @field:Json(name = "termsMap")
    val termsMap: Map<String, Boolean>

)