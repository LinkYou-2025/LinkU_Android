<<<<<<<< HEAD:data/src/main/java/com/linku/data/api/dto/auth/signup/email/SignUpEmailRequestDTO.kt
package com.linku.data.api.dto.auth.signup.email
========
package com.linku.data.api.dto.server
>>>>>>>> fd1304faab6b86e04c17e31a0786ce151290d292:data/src/main/java/com/linku/data/api/dto/server/JoinDTO.kt

import com.squareup.moshi.Json

data class SignUpEmailRequestDTO (

    @Json(name = "nickName")
    val nickName: String,

    @Json(name = "email")
    val email: String,

    @Json(name = "password")
    val password: String,

    @Json(name = "gender")
    val gender: Int,

    @Json(name = "jobId")
    val jobId: Int,

    @Json(name = "purposeList")
    val purposeList: List<String>,

    @Json(name = "interestList")
    val interestList: List<String>

)