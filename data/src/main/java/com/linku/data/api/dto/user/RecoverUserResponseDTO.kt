package com.linku.data.api.dto.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class RecoverUserResponseDTO(

    @field:Json(name = "userId")
    val userId: Long,

    @field:Json(name = "nickname")
    val nickname: String,

    @field:Json(name = "createdAt")
    val createdAt: OffsetDateTime,

    @field:Json(name = "status")
    val status: String,

    // OffsetDateTimeSerializer가 명시적 JSON null을 못 걸러내 파싱 예외를 던지는 문제 회피용.
    // 이 값은 도메인 로직에서 쓰이지 않으므로 원시 문자열로만 받아 파싱을 건너뜀.
    @field:Json(name = "inactiveDate")
    val inactiveDate: String? = null,

    @field:Json(name = "accessToken")
    val accessToken: String,

    @field:Json(name = "refreshToken")
    val refreshToken: String

)
