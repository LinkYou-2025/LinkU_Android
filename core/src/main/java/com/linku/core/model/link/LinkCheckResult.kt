package com.linku.core.model.link

sealed interface LinkCheckResult {

    // 저장한 적 없는 링크이며, 백엔드 유효성 검사 통과
    data object Available : LinkCheckResult

    // 이미 저장한 링크
    data object AlreadySaved : LinkCheckResult
}