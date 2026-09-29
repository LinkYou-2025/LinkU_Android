package com.linku.core.util

/** 이미지 주소에 이미 명시된 URI 스킴이 있는지 판별합니다. */
private val imageUriSchemePattern = Regex("^[A-Za-z][A-Za-z0-9+.-]*:")

/**
 * 이미지 URL의 누락된 HTTPS 스킴을 보정하고 기본 이미지가 필요한 값은 제외합니다.
 *
 * 앞뒤 공백을 제거한 뒤 HTTPS 주소는 그대로 유지하고, 스킴이 없는 주소에는
 * `https://`를 추가합니다. `//host/path` 형태에는 `https:`를 추가합니다.
 * 명시적인 HTTP 주소는 HTTPS로 강제 전환하지 않고 `null`을 반환합니다.
 * 스킴 비교는 대소문자를 구분하지 않으며 경로와 서명 쿼리는 변경하지 않습니다.
 * 기존 동작과의 호환성을 위해 그 밖의 명시적인 URI 스킴은 그대로 유지합니다.
 *
 * 호출 화면은 반환값이 `null`이면 기본 이미지를 표시해야 합니다.
 *
 * @receiver 서버 이미지 주소 또는 기존 이미지 URI이며, `null`일 수 있습니다.
 * @return 보정된 주소입니다. `null`, 빈 문자열, 공백 또는 HTTP 주소는 `null`입니다.
 */
fun String?.toImageUrl(): String? {
    // 빈 값으로 네트워크 요청을 만들지 않도록 먼저 기본 이미지 대상으로 분류합니다.
    val value = this?.trim()?.takeIf { it.isNotEmpty() } ?: return null

    return when {
        value.startsWith("https://", ignoreCase = true) -> value
        value.startsWith("http://", ignoreCase = true) -> null
        value.startsWith("//") -> "https:$value"
        imageUriSchemePattern.containsMatchIn(value) -> value
        else -> "https://$value"
    }
}
