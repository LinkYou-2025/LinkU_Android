package com.linku.deeplink

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.linku.core.error.DeepLinkError

/** `/open` 딥링크에서 초대 토큰을 전달하는 내비게이션 인자 이름입니다. */
internal const val OPEN_DEEP_LINK_TOKEN_ARGUMENT = "token"

/** 초대 토큰 인자를 포함하는 `/open` 내비게이션 라우트 템플릿입니다. */
internal const val OPEN_DEEP_LINK_ROUTE = "open?token={$OPEN_DEEP_LINK_TOKEN_ARGUMENT}"

/** 랜딩 화면의 앱 열기 버튼이 사용하는 커스텀 스킴 딥링크 URI 패턴입니다. */
internal const val CUSTOM_SCHEME_OPEN_DEEP_LINK_URI_PATTERN =
    "linku://open?token={$OPEN_DEEP_LINK_TOKEN_ARGUMENT}"

/** 딥링크 호스트에 허용하는 DNS 호스트 문자 형식입니다. */
private val DEEP_LINK_HOST_PATTERN = Regex("^[A-Za-z0-9.-]+$")

/**
 * `/open` 라우트가 사용하는 필수 초대 토큰 인자 계약을 생성합니다.
 *
 * 인자 자체는 non-null로 유지하되 기본값을 빈 문자열로 두어, 토큰이 생략된 딥링크도 라우트에
 * 진입한 뒤 [parseOpenDeepLinkToken]에서 명시적인 [DeepLinkError.MissingInvitationToken]으로
 * 처리할 수 있게 합니다.
 *
 * @return 문자열 타입이며 nullable이 아니고 빈 문자열을 기본값으로 갖는 내비게이션 인자
 */
internal fun openDeepLinkTokenArgument(): NamedNavArgument =
    navArgument(OPEN_DEEP_LINK_TOKEN_ARGUMENT) {
        type = NavType.StringType
        nullable = false
        defaultValue = ""
    }

/**
 * 딥링크 전용 호스트와 초대 토큰 인자를 조합해 HTTPS `/open` URI 패턴을 생성합니다.
 *
 * API 주소와 독립적으로 관리하는 `SERVER_HOST` 값만 받으며, 스킴이나 경로가 섞인 설정은
 * 허용하지 않습니다.
 *
 * @param deepLinkHost 딥링크에 사용할 스킴 없는 호스트
 * @return HTTPS 스킴과 `/open` 경로를 포함한 딥링크 URI 패턴
 * @throws IllegalArgumentException 호스트가 비어 있거나 DNS 호스트 이외의 형식인 경우
 */
internal fun openDeepLinkUriPattern(deepLinkHost: String): String {
    val normalizedHost = deepLinkHost.trim()
    require(normalizedHost.isNotBlank()) { "Deep link host must not be blank." }
    require(DEEP_LINK_HOST_PATTERN.matches(normalizedHost)) {
        "Deep link host must not contain a scheme, port, path, query, or fragment."
    }

    return "https://$normalizedHost/open?token={$OPEN_DEEP_LINK_TOKEN_ARGUMENT}"
}

/**
 * `/open` 딥링크의 초대 토큰에서 앞뒤 공백을 제거하고 유효성을 검사합니다.
 *
 * @param token non-null 내비게이션 인자에서 읽은 초대 토큰
 * @return 앞뒤 공백이 제거된 초대 토큰
 * @throws DeepLinkError.MissingInvitationToken 토큰이 비어 있거나 공백으로만 구성된 경우
 */
internal fun parseOpenDeepLinkToken(token: String): String {
    val normalizedToken = token.trim()

    // 빈 기본값을 도메인 오류로 변환해 라우트에서 누락된 토큰만 구체적으로 처리하게 합니다.
    if (normalizedToken.isBlank()) {
        throw DeepLinkError.MissingInvitationToken()
    }

    return normalizedToken
}
