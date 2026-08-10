package com.linku.data.api.dto.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckUserTermsDTO(

    @field:Json(name = "termsStatus")
    val termsStatus: TermsStatusDTO,

    @field:Json(name = "allRequiredAgreed")
    val allRequiredAgreed: Boolean,
)

@JsonClass(generateAdapter = true)
data class TermsStatusDTO(
    @field:Json(name = "TERMS_OF_USE")
    val termsOfUse: Boolean,
    @field:Json(name = "PRIVACY_POLICY")
    val privacyPolicy: Boolean,
    @field:Json(name = "MARKETING")
    val marketing: Boolean,
)