<<<<<<<< HEAD:data/src/main/java/com/linku/data/api/dto/auth/login/kakao/KakaoLoginResponseDTO.kt
package com.linku.data.api.dto.auth.login.kakao
========
package com.linku.data.api.dto.login.kakao
>>>>>>>> fd1304faab6b86e04c17e31a0786ce151290d292:data/src/main/java/com/linku/data/api/dto/login/kakao/KakaoLoginRequestDTO.kt

import com.squareup.moshi.Json

data class KakaoLoginResponseDTO(
    @Json(name = "userId") val userId: Long,
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String,
    @Json(name = "status") val status: String
)