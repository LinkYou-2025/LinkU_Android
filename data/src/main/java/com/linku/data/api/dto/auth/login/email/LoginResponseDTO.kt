package com.linku.data.api.dto.auth.login.email

import com.squareup.moshi.Json

// null인 경우 빈문자열을 받도록 수정했었는데,
// 그러면 오류 잡기 너무 힘들 것 같아서 다시 수정했습니당.(03.24)
data class LoginResponseDTO (

    @Json(name = "userId")
    val userId: Long, // 항상 있어야 함

    @Json(name = "accessToken")
    val accessToken: String, // 항상 있어야 함. -> 기본 값 제거

    @Json(name = "refreshToken")
    val refreshToken: String,  // 항상 있어야 함. -> 기본 값 제거

    @Json(name = "status")
    val status: String = "", // 없을 수도 있음 → 기본값 유지

    @Json(name = "inactiveDate")
    val inactiveDate: String? = null  // 이건 nullable로 수정함. 서버에서 null로 옴....

)