package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class LoginResultDTO (

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
    val inactiveDate: String = ""   // null인 경우 빈문자열을 받도록 수정함.

)