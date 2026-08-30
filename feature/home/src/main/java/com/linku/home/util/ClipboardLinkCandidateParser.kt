package com.linku.home.util

import com.linku.home.model.ClipboardLinkCandidate
import java.net.URI

/**
 * 시스템 클립보드의 최신 텍스트를 앱 진입 시 처리할 웹 링크 후보로 변환합니다.
 *
 * 클립보드 전체가 HTTP 또는 HTTPS 절대 URL 하나인 경우에만 후보를 생성합니다. 일반 문장에 URL이
 * 섞여 있거나 다른 스킴을 사용하면 사용자가 복사한 원문을 임의로 해석하지 않고 무시합니다.
 *
 * @param clipboardText 클립보드 첫 번째 항목을 문자열로 변환한 값
 * @param copiedAtMillis Android `ClipDescription`이 제공하는 복사 시각
 * @return 저장 화면 자동 진입에 사용할 후보 또는 유효한 웹 URL이 아니면 `null`
 */
fun parseClipboardLinkCandidate(
    clipboardText: String?,
    copiedAtMillis: Long,
): ClipboardLinkCandidate? {
    val normalizedUrl = clipboardText?.trim().orEmpty()
    if (normalizedUrl.isEmpty()) {
        return null
    }

    val uri = runCatching { URI(normalizedUrl) }.getOrNull() ?: return null
    val isSupportedScheme = uri.scheme?.lowercase() in SUPPORTED_CLIPBOARD_LINK_SCHEMES
    if (!isSupportedScheme || uri.rawAuthority.isNullOrBlank()) {
        return null
    }

    return ClipboardLinkCandidate(
        url = normalizedUrl,
        copiedAtMillis = copiedAtMillis,
    )
}

/** 클립보드 자동 진입에서 허용하는 웹 URL 스킴입니다. */
private val SUPPORTED_CLIPBOARD_LINK_SCHEMES = setOf("http", "https")
