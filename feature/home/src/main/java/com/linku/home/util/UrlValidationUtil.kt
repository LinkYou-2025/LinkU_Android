package com.linku.home.util

import android.util.Patterns

sealed interface UrlValidationResult {
    data object Valid : UrlValidationResult
    data object InvalidFormat : UrlValidationResult
    data object MultipleLinks : UrlValidationResult
}

fun UrlValidationResult.toToastMessage(): String {
    return when (this) {
        UrlValidationResult.MultipleLinks -> "링크는 1개만 등록할 수 있어요."
        UrlValidationResult.InvalidFormat -> "유효하지 않은 링크입니다!"
        UrlValidationResult.Valid -> "유효한 링크입니다!"
    }
}

/**
 * 링크 입력 필드의 URL 형식을 프론트에서 1차 검증합니다.
 *
 * 검증 기준:
 * 1. 비어 있거나 URL이 감지되지 않으면 InvalidFormat
 * 2. URL이 2개 이상 감지되면 MultipleLinks
 * 3. URL이 1개 감지되더라도 입력값 전체가 URL 하나가 아니면 InvalidFormat
 * 4. URL 하나만 정확히 입력되어 있으면 Valid
 *
 * 예)
 * - https://naver.com -> Valid
 * - www.naver.com -> Valid
 * - naver.com -> Valid
 * - https://naver.com https://google.com -> MultipleLinks
 * - 링크는 https://naver.com 입니다 -> InvalidFormat
 */
fun validateUrlInput(input: String): UrlValidationResult {
    val trimmedInput = input.trim()

    if (trimmedInput.isEmpty()) {
        return UrlValidationResult.InvalidFormat
    }

    val detectedUrls = extractWebUrls(trimmedInput)

    return when {
        detectedUrls.size >= 2 -> {
            UrlValidationResult.MultipleLinks
        }

        detectedUrls.isEmpty() -> {
            UrlValidationResult.InvalidFormat
        }

        !isExactSingleUrl(
            input = trimmedInput,
            detectedUrl = detectedUrls.first()
        ) -> {
            UrlValidationResult.InvalidFormat
        }

        else -> {
            UrlValidationResult.Valid
        }
    }
}

/**
 * Patterns.WEB_URL 기준으로 입력값 안의 URL 후보를 추출합니다.
 */
private fun extractWebUrls(input: String): List<String> {
    val matcher = Patterns.WEB_URL.matcher(input)

    return buildList {
        while (matcher.find()) {
            add(matcher.group())
        }
    }
}

/**
 * 입력값 전체가 감지된 URL 하나와 정확히 일치하는지 확인합니다.
 *
 * 예)
 * - "https://naver.com" == "https://naver.com" -> true
 * - "링크 https://naver.com" != "https://naver.com" -> false
 */
private fun isExactSingleUrl(
    input: String,
    detectedUrl: String,
): Boolean {
    return input == detectedUrl
}