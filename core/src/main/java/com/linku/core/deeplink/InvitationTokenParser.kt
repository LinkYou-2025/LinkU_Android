package com.linku.core.deeplink

import com.linku.core.error.DeepLinkError

/**
 * 초대 토큰의 앞뒤 공백을 제거하고 필수 값이 존재하는지 검사합니다.
 *
 * URI 디코딩이나 토큰 내부 문자 변경은 수행하지 않습니다. Navigation 등 호출자가 추출한
 * 토큰을 전달해야 하며, 비어 있는 입력은 구체적인 토큰 누락 오류로 변환합니다.
 *
 * @param token 딥링크에서 추출한 초대 토큰
 * @return 앞뒤 공백이 제거된 초대 토큰
 * @throws DeepLinkError.MissingInvitationToken 토큰이 비어 있거나 공백으로만 구성된 경우
 */
fun parseInvitationToken(token: String): String {
    val normalizedToken = token.trim()

    if (normalizedToken.isBlank()) {
        throw DeepLinkError.MissingInvitationToken()
    }

    return normalizedToken
}
