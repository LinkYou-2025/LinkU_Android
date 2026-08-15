package com.linku.data.api.dto.server

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 링크 존재 여부 검사에 필요한 응답만 나타냅니다.
 *
 * 서버가 함께 반환하는 링크 상세 필드는 현재 유효성 검사 결과에 사용하지 않으며 Moshi가 무시합니다.
 *
 * @property isExist 현재 사용자가 해당 링크를 이미 저장했는지 여부
 */
@JsonClass(generateAdapter = true)
data class LinkuIsExistDTO(
    @field:Json(name = "isExist")
    val isExist: Boolean,
)
