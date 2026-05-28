package com.linku.core.error

/**
 * LinkU 서비스에서 발생하는 모든 API 에러의 최상위 추상 클래스입니다.
 *
 * 서버 응답 에러 및 클라이언트측 네트워크 에러를 계층 구조로 정의하며,
 * 각 에러는 고유한 [code]와 사용자에게 보여줄 [serverMessage]를 포함합니다.
 *
 * @property code 에러를 식별하기 위한 고유 코드 (예: "COMMON401", "NETWORK_NO_CONNECTION")
 * @property serverMessage 에러에 대한 상세 설명 또는 사용자에게 표시할 메시지
 * @see Exception
 */
sealed class BaseError(
    serverMessage: String
) : Exception(serverMessage)