package com.linku.core.deeplink

/**
 * API 서버 주소와 독립적으로 사용하는 딥링크 설정입니다.
 *
 * @property host 스킴, 포트, 경로를 포함하지 않는 딥링크 전용 호스트
 */
data class DeepLinkConfig(
    val host: String,
)
