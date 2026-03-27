package com.linku.data.api.dto.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class UserInfoResponseDTO(
    // Done 통합 : 01.13 완료 했습니다. (username 제거)
    @Json(name = "nickName")
    val nickName: String? = null,

    @Json(name = "email")
    val email: String,

    @Json(name = "gender") //03.14 테스트 계정 성별 null로 수정함.
    val gender: Gender,

    @Json(name = "job")
    val job: Job,

    @Json(name = "myLinku")
    val myLinku: Long,

    @Json(name = "myFolder")
    val myFolder: Long,

    @Json(name = "myAiLinku")
    val myAiLinku: Long,

    @Json(name = "purposes")
    val purposes: List<String> = emptyList(),

    @Json(name = "interests")
    val interests: List<String> = emptyList()
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