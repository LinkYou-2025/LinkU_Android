package com.linku.data.di.api

/**
 * OkHttp 로그에 포함될 수 있는 알려진 인증 정보와 초대 토큰을 마스킹합니다.
 *
 * 정제 범위는 Bearer 인증 헤더, 초대 URL 경로 및 `token` 쿼리 매개변수이며,
 * 임의의 요청 또는 응답 본문에 포함된 모든 민감 정보를 포괄하지는 않습니다.
 */
internal object SensitiveHttpLogSanitizer {
    /** `Authorization: Bearer` 접두사 뒤의 자격 증명을 찾는 정규식입니다. */
    private val authorizationHeaderRegex = Regex(
        pattern = "(?i)(Authorization:\\s*Bearer\\s+)[^\\s]+"
    )

    /** `/invitations/` 경로 바로 뒤에 위치한 초대 토큰을 찾는 정규식입니다. */
    private val invitationPathRegex = Regex(
        pattern = "(/invitations/)[^/?\\s\\\"]+"
    )

    /** URL의 `?token=` 또는 `&token=` 뒤에 위치한 쿼리 값을 찾는 정규식입니다. */
    private val tokenQueryRegex = Regex(
        pattern = "([?&]token=)[^&\\s\\\"]+"
    )

    /**
     * HTTP 로그 메시지에서 알려진 민감 값만 `<redacted>`로 치환합니다.
     *
     * @param message OkHttp 로깅 인터셉터가 전달한 단일 로그 메시지
     * @return 일치한 민감 값은 마스킹하고 나머지 구조는 유지한 로그 문자열
     */
    fun sanitize(message: String): String =
        message
            .replace(authorizationHeaderRegex, "$1<redacted>")
            .replace(invitationPathRegex, "$1<redacted>")
            .replace(tokenQueryRegex, "$1<redacted>")
}
