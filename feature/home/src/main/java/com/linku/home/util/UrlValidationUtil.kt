package com.linku.home.util

import android.net.Uri
import android.util.Patterns

sealed interface UrlValidationResult {
    data object Valid : UrlValidationResult
    data object InvalidFormat : UrlValidationResult
    data object MultipleLinks : UrlValidationResult
    data object VideoFormat : UrlValidationResult
}

/**
 * 링크 입력 필드의 URL 형식을 1차 검증한다.
 *
 * 검증 기준:
 * 1. 비어 있거나 URL이 감지되지 않으면 InvalidFormat
 * 2. Patterns.WEB_URL로 감지되는 URL이 2개 이상이면 MultipleLinks
 * 3. URL이 1개 감지되더라도 입력값 전체가 URL 하나가 아니면 InvalidFormat
 * 4. 영상 콘텐츠 URL이면 VideoFormat
 * 5. URL 하나만 정확히 입력되어 있으면 Valid
 *
 * 예)
 * - https://naver.com -> Valid
 * - www.naver.com -> Valid
 * - naver.com -> Valid
 * - https://naver.com https://google.com -> MultipleLinks
 * - 링크는 https://naver.com 입니다 -> InvalidFormat
 * - https://youtube.com/watch?v=... -> VideoFormat
 * - https://youtu.be/... -> VideoFormat
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

        isVideoUrl(detectedUrls.first()) -> {
            UrlValidationResult.VideoFormat
        }

        else -> {
            UrlValidationResult.Valid
        }
    }
}

/**
 * Patterns.WEB_URL 기준으로 입력값 안의 URL 후보를 추출한다.
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
 * 입력값 전체가 감지된 URL 하나와 정확히 일치하는지 확인한다.
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

/**
 * 현재 링큐에서 지원하지 않는 영상 콘텐츠 URL인지 확인한다.
 */
private fun isVideoUrl(url: String): Boolean {
    val videoDomains = listOf(
        "youtube.com",
        "youtu.be"
    )

    val host = runCatching {
        val normalizedUrl = if (url.contains("://")) {
            url
        } else {
            "https://$url"
        }

        Uri.parse(normalizedUrl).host
    }.getOrNull() ?: return false

    return videoDomains.any { domain ->
        host.equals(domain, ignoreCase = true) ||
                host.endsWith(".$domain", ignoreCase = true)
    }
}