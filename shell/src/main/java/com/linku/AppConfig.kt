package com.linku

/**
 * 앱 모듈이 가진 빌드 설정 중 셸이 필요로 하는 값을 전달하는 계약입니다.
 *
 * 빌드 설정(`BuildConfig`)은 앱 모듈에만 있으므로, 앱이 구현해 Hilt로 제공합니다.
 */
interface AppConfig {
    /** https 앱 링크로 인식할 서버 호스트입니다. */
    val deepLinkHost: String
}
