package com.linku.core.model.auth

data class SocialLoginData(
    val provider: String,//카카오, 네이버, 구글
    val result: String,
    val status: String?,
    val accessToken: String?,
    val refreshToken: String?,
    val socialToken: String?,
    val errorCode: String?
)

/**
 * data class SocialLoginData(
 *     val provider: String,       // kakao / google / naver
 *     val result: String,         // SUCCESS / FAIL
 *     val status: String?,        // ACTIVE / TEMP (SUCCESS일 때만)
 *     val accessToken: String?,   // ACTIVE: 일반 로그인용
 *     val refreshToken: String?,  // ACTIVE: 자동 로그인용 (신규 추가 요청)
 *     (배제함. 보안 이슈)val userId: Long?,          // ACTIVE: fetchAndSaveUserSession용 (신규 추가 요청)
 *     val socialToken: String?,   // TEMP: 추가 회원가입용
 *     val errorCode: String?      // FAIL일 때
 * )
 * */