package com.linku.core.util

import android.net.Uri

/** HTTP(S) 스킴이 없는 문자열 앞에 HTTPS 스킴을 붙입니다. */
fun ensureHttpScheme(raw: String): String =
    if (raw.startsWith("http://") || raw.startsWith("https://")) raw
    else "https://$raw"

/**
 * 이미지 주소를 정리하고 스킴이 없으면 HTTPS 스킴을 추가합니다.
 *
 * 프로토콜 상대 주소(`//host/path`)는 HTTPS 주소로 변환합니다.
 * 이미 스킴이 있는 주소는 스킴 종류와 대소문자에 관계없이 유지하므로,
 * 서명된 URL의 경로·쿼리나 로컬 이미지 URI를 변경하지 않습니다.
 *
 * @receiver 서버에서 받은 이미지 주소이며, `null`일 수 있습니다.
 * @return 앞뒤 공백과 누락된 스킴을 보정한 주소입니다. 빈 값은 `null`을 반환합니다.
 */
fun String?.toImageUrl(): String? {
    // 이미지가 없는 경우 잘못된 "https://" 요청을 만들지 않도록 먼저 제외합니다.
    val value = this?.trim()?.takeIf { it.isNotEmpty() } ?: return null

    return when {
        value.startsWith("//") -> "https:$value"
        Uri.parse(value).scheme != null -> value
        else -> "https://$value"
    }
}
