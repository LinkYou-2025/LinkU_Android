package com.linku.deeplink

/** `/open` 딥링크에서 초대 토큰을 전달하는 내비게이션 인자 이름입니다. */
internal const val OPEN_DEEP_LINK_TOKEN_ARGUMENT = "token"

/** 초대 토큰 인자를 포함하는 `/open` 내비게이션 라우트 템플릿입니다. */
internal const val OPEN_DEEP_LINK_ROUTE = "open?token={$OPEN_DEEP_LINK_TOKEN_ARGUMENT}"

/**
 * 서버 도메인과 초대 토큰 인자를 조합해 `/open` 딥링크 URI 패턴을 생성합니다.
 *
 * @param deepLinkDomain 딥링크에 사용할 서버 도메인
 * @return 도메인 끝의 슬래시가 제거된 `/open` 딥링크 URI 패턴
 */
internal fun openDeepLinkUriPattern(deepLinkDomain: String): String =
    "${deepLinkDomain.trimEnd('/')}/open?token={$OPEN_DEEP_LINK_TOKEN_ARGUMENT}"

/** `/open` 딥링크의 초대 토큰을 파싱한 결과입니다. */
internal sealed interface OpenDeepLinkParseResult {
    /**
     * 비어 있지 않은 초대 토큰이 포함된 파싱 결과입니다.
     *
     * @property token 앞뒤 공백이 제거된 초대 토큰
     */
    data class Invitation(
        val token: String,
    ) : OpenDeepLinkParseResult

    /** 초대 토큰이 없거나 공백뿐이어서 처리할 수 없는 파싱 결과입니다. */
    data object Invalid : OpenDeepLinkParseResult
}

/**
 * `/open` 딥링크의 초대 토큰을 정규화하고 토큰 존재 여부를 판별합니다.
 *
 * @param token 내비게이션 인자에서 읽은 초대 토큰
 * @return 공백을 제거한 토큰이 있으면 [OpenDeepLinkParseResult.Invitation],
 * 그렇지 않으면 [OpenDeepLinkParseResult.Invalid]
 */
internal fun parseOpenDeepLinkToken(token: String?): OpenDeepLinkParseResult {
    val normalizedToken = token?.trim().orEmpty()

    return if (normalizedToken.isBlank()) {
        OpenDeepLinkParseResult.Invalid
    } else {
        OpenDeepLinkParseResult.Invitation(normalizedToken)
    }
}
