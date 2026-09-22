package com.linku.home.util

import com.linku.core.util.UrlValidationResult
import com.linku.core.util.validateUrlInput
import com.linku.home.model.ClipboardLinkCandidate

/**
 * 시스템 클립보드의 최신 텍스트를 앱 진입 시 처리할 웹 링크 후보로 변환합니다.
 *
 * 클립보드 전체가 링크 저장 공통 검사 기준을 통과한 URL 하나인 경우에만 후보를 생성합니다.
 * 프로토콜 없는 도메인은 허용하지만 일반 문장에 URL이 섞인 입력은 원문을 임의로 해석하지 않습니다.
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
    if (validateUrlInput(normalizedUrl) != UrlValidationResult.Valid) {
        return null
    }

    return ClipboardLinkCandidate(
        url = normalizedUrl,
        copiedAtMillis = copiedAtMillis,
    )
}
