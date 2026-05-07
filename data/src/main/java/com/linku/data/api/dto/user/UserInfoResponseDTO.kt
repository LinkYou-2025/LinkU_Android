package com.linku.data.api.dto.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserInfoResponseDTO(
    // Done 통합 : 01.13 완료 했습니다. (username 제거)
    @field:Json(name = "nickName")
    val nickName: String? = null,

    @field:Json(name = "email")
    val email: String,

    @field:Json(name = "gender") //03.14 테스트 계정 성별 null로 수정함.
    val gender: Gender,

    @field:Json(name = "job")
    val job: Job,

    @field:Json(name = "myLinku")
    val myLinku: Long,

    @field:Json(name = "myFolder")
    val myFolder: Long,

    @field:Json(name = "myAiLinku")
    val myAiLinku: Long,

    @field:Json(name = "purposes")
    val purposes: List<String> = emptyList(),

    @field:Json(name = "interests")
    val interests: List<String> = emptyList()
)

@JsonClass(generateAdapter = false)
enum class Gender(val value: String) {
    @field:Json(name = "MALE") MALE("MALE"),
    @field:Json(name = "FEMALE") FEMALE("FEMALE");
}

@JsonClass(generateAdapter = true)
data class Job(
    @field:Json(name = "id")
    val id: Long,

    @field:Json(name = "name")
    val name: String
)