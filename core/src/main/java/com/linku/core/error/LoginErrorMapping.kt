package com.linku.core.error

import com.linku.core.model.auth.LoginErrorType

/**
 * 이메일 로그인이 실제로 던지는 예외를 화면에 표시할 [LoginErrorType]으로 변환합니다.
 *
 * 로그인 레포지토리는 내부적으로 `safeApiCall`을 쓰기 때문에, 실패 시 도메인 에러([ApiError], [NetworkError])
 * 계열을 던집니다. 예전엔 이 함수가 `@Deprecated ApiError`만 알고 있어서 실제로는 절대 매칭되지 않았고,
 * 비밀번호 오류 같은 모든 로그인 실패가 else 분기(UNKNOWN_ERROR)로 빠지는 버그가 있었음.
 *
 * `safeApiCall`이 `HttpException`/`IOException`류를 이미 내부에서 도메인 에러로 전부 변환해주므로,
 * 이 함수까지 raw `HttpException`/`IOException`이 넘어오는 경우는 없음 (죽은 분기라 제거함).
 *
 * @receiver 로그인 중 발생한 예외
 * @return 화면에 표시할 로그인 오류 종류
 */
fun Exception.toLoginErrorType(): LoginErrorType = when (this) {
    is ApiError.User.LoginFailed -> LoginErrorType.INVALID_CREDENTIALS // USERS4012 - 이메일/비밀번호 불일치
    is ApiError.Common.Unauthorized -> LoginErrorType.INVALID_CREDENTIALS
    is ApiError.User.Inactive -> LoginErrorType.INACTIVE_USER_ERROR
    is NetworkError -> LoginErrorType.NETWORK_ERROR // 연결 끊김/타임아웃
    is ApiError.Common.InternalServer -> LoginErrorType.SERVER_ERROR
    else -> LoginErrorType.UNKNOWN_ERROR
}
