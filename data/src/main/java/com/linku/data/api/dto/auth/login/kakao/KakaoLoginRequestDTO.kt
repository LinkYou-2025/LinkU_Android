package com.linku.data.api.dto.auth.login.kakao



import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KakaoLoginRequestDTO(
    val token : String
)
