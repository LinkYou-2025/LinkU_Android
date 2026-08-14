package com.linku.home.model

/**
 * 시스템 클립보드에서 읽은 링크와 해당 복사 이벤트를 함께 나타냅니다.
 *
 * 복사 시각이 제공되는 일반적인 환경에서는 같은 URL을 다시 복사해도 [copiedAtMillis]가 달라지므로
 * 별개의 클립보드 항목으로 취급됩니다.
 *
 * @property url 공백을 제거한 http 또는 https 링크
 * @property copiedAtMillis `ClipData`가 전역 클립보드에 복사된 시각. 제공되지 않으면 `0`
 */
data class ClipboardLinkCandidate(
    val url: String,
    val copiedAtMillis: Long,
)
