package com.linku.data.api

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
/** 인증 토큰이 필요한 API 클라이언트에 사용합니다. */
annotation class AuthClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
/** 인증 토큰이 필요 없는 공용 API 클라이언트에 사용합니다. */
annotation class PublicClient