package com.linku.login.constants

/**
 * 서버 URL 상수 모음
 *
 * [02.21] 코드래빗 피드백 반영 - 하드코딩된 URL 분리
 * TODO: 추후 BuildConfig로 환경별(dev/staging/prod) 분리 예정
 *   build.gradle.kts에 buildConfigField 추가 필요 → 팀장이 결정해주세요.
 */
object ServerConfig {
    private const val BASE_URL = "https://linkuserver.store"

    const val KAKAO_LOGIN_URL  = "$BASE_URL/login/kakao"
    const val GOOGLE_LOGIN_URL = "$BASE_URL/login/google"
}
