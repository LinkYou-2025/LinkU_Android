package com.example.data.api.dto.auth.login.email

import com.squareup.moshi.Json

data class LoginResponseDTO (

    @Json(name = "userId")
    val userId: Long? = null, //null 불가.

    @Json(name = "accessToken")
    val accessToken: String = "", // null인 경우 빈문자열을 받도록 수정함.
    //

    @Json(name = "refreshToken")
    val refreshToken: String = "", // null인 경우 빈문자열을 받도록 수정함.

    @Json(name = "status")
    val status: String = "",   // null인 경우 빈문자열을 받도록 수정함.

    @Json(name = "inactiveDate")
    val inactiveDate: String? = null  // 이건 nullable로 수정함. 서버에서 null로 옴....

)