package com.linku.data.api.dto.auth.login.social

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SocialLoginResponseDTO(

    @field:Json(name = "userId")
    val userId: Long,

    @field:Json(name = "accessToken")
    val accessToken: String,

    // 탈퇴 유예기간(INACTIVE) 계정은 서버가 refreshToken을 null로 내려줌 (복구 전용 accessToken만 발급)
    @field:Json(name = "refreshToken")
    val refreshToken: String?,

    @field:Json(name = "status")
    val status: String?,

    @field:Json(name = "inactiveDate")
    val inactiveDate: String? = "",
)