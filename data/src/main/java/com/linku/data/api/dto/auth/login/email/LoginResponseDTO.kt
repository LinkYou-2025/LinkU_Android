package com.linku.data.api.dto.auth.login.email

import com.squareup.moshi.Json

// null인 경우 빈문자열을 받도록 수정했었는데,
// 그러면 오류 잡기 너무 힘들 것 같아서 다시 수정했습니당.(03.24)
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponseDTO (

    @field:Json(name = "userId")
    val userId: Long,

    @field:Json(name = "accessToken")
    val accessToken: String,

    // 탈퇴 유예기간(INACTIVE) 계정은 서버가 refreshToken을 null로 내려줌 (복구 전용 accessToken만 발급)
    @field:Json(name = "refreshToken")
    val refreshToken: String?,

    @field:Json(name = "status")
    val status: String = "",

    @field:Json(name = "inactiveDate")
    val inactiveDate: String? = null

)