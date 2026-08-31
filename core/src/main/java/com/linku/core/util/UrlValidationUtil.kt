package com.linku.core.util

import androidx.core.util.PatternsCompat

/** 링크 저장에 공통으로 적용하는 URL 형식 검사 결과입니다. */
sealed interface UrlValidationResult {
    /** 입력값 전체가 저장 가능한 URL 하나인 상태입니다. */
    data object Valid : UrlValidationResult

    /** 입력값이 비어 있거나 URL 하나로만 구성되지 않은 상태입니다. */
    data object InvalidFormat : UrlValidationResult

    /** 입력값에서 URL이 두 개 이상 감지된 상태입니다. */
    data object MultipleLinks : UrlValidationResult
}

/**
 * 링크 저장에 사용할 입력값을 공통 형식 기준으로 검사합니다.
 *
 * 프로토콜이 있는 URL뿐 아니라 `www.naver.com`, `naver.com`처럼 프로토콜이 없는 도메인도
 * 허용합니다. 단, 입력값 전체가 감지된 URL 하나와 정확히 일치해야 합니다.
 *
 * @param input 링크 저장 후보 문자열
 * @return URL 개수와 전체 일치 여부에 따른 [UrlValidationResult]
 */
fun validateUrlInput(input: String): UrlValidationResult {
    val trimmedInput = input.trim()
    if (trimmedInput.isEmpty()) {
        return UrlValidationResult.InvalidFormat
    }

    val detectedUrls = extractWebUrls(trimmedInput)
    return when {
        detectedUrls.size >= 2 -> UrlValidationResult.MultipleLinks
        detectedUrls.isEmpty() -> UrlValidationResult.InvalidFormat
        trimmedInput != detectedUrls.single() -> UrlValidationResult.InvalidFormat
        else -> UrlValidationResult.Valid
    }
}

/**
 * [PatternsCompat.WEB_URL] 기준으로 문자열 안의 URL 후보를 발견된 순서대로 반환합니다.
 *
 * 외부 공유 본문처럼 설명과 URL이 함께 전달되는 입력에서도 링크 저장 화면과 동일한 후보 판정
 * 기준을 재사용할 수 있도록 공개합니다.
 *
 * @param input URL 후보를 찾을 문자열
 */
fun extractWebUrls(input: String): List<String> {
    val matcher = PatternsCompat.WEB_URL.matcher(input)
    return buildList {
        while (matcher.find()) {
            add(matcher.group())
        }
    }
}
