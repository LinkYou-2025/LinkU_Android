<<<<<<<< HEAD:data/src/main/java/com/linku/data/api/dto/auth/login/email/LoginRequestDTO.kt
package com.linku.data.api.dto.auth.login.email
========
package com.linku.data.api.dto.server
>>>>>>>> fd1304faab6b86e04c17e31a0786ce151290d292:data/src/main/java/com/linku/data/api/dto/server/LoginRequestDTO.kt

import com.squareup.moshi.Json

data class LoginRequestDTO (

    @Json(name = "email")
    val email: String,

    @Json(name = "password")
    val password: String

)