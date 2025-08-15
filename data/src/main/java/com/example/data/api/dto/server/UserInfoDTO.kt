package com.example.data.api.dto.server

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class UserInfoDTO(
//    @Json(name = "nickname")
//    val nickname: String,

    @Json(name = "nickname")
    val nickname: String? = null,
    @Json(name = "nickName")
    val nickName: String? = null,

    @Json(name = "email")
    val email: String,

    @Json(name = "gender")
    val gender: Gender,

    @Json(name = "job")
    val job: Job,

    @Json(name = "myLinku")
    val myLinku: Long,

    @Json(name = "myFolder")
    val myFolder: Long,

    @Json(name = "myAiLinku")
    val myAiLinku: Long
)

@JsonClass(generateAdapter = false)
enum class Gender(val value: String) {
    @Json(name = "MALE") MALE("MALE"),
    @Json(name = "FEMALE") FEMALE("FEMALE");
}

data class Job(
    @Json(name = "id")
    val id: Long,

    @Json(name = "name")
    val name: String
)