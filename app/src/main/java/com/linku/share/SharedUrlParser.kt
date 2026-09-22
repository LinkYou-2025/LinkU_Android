package com.linku.share

import com.linku.core.util.UrlValidationResult
import com.linku.core.util.extractWebUrls
import com.linku.core.util.validateUrlInput

/**
 * 외부 앱이 `ACTION_SEND`로 공유한 텍스트에서 저장할 웹 URL을 추출합니다.
 *
 * 공유 본문에는 기사 제목이나 설명이 URL과 함께 포함될 수 있으므로 URL 후보를 먼저 찾고, 링크
 * 저장 화면과 동일한 공통 검사 기준을 통과한 URL 하나만 허용합니다.
 *
 * @param sharedText `Intent.EXTRA_TEXT`로 전달된 공유 본문
 * @return URL 개수와 유효성에 따른 [SharedUrlParseResult]
 */
fun parseSharedUrl(sharedText: String?): SharedUrlParseResult {
    val normalizedText = sharedText?.trim().orEmpty()
    if (normalizedText.isEmpty()) {
        return SharedUrlParseResult.EmptyText
    }

    val urls = extractWebUrls(normalizedText)
        .map(::trimTrailingSentencePunctuation)
        .filter { candidate -> validateUrlInput(candidate) == UrlValidationResult.Valid }
        .toList()

    return when {
        urls.isEmpty() -> SharedUrlParseResult.NoSupportedUrl
        urls.size > 1 -> SharedUrlParseResult.MultipleUrls
        else -> SharedUrlParseResult.Success(urls.single())
    }
}

/** 외부 공유 텍스트의 URL 파싱 결과입니다. */
sealed interface SharedUrlParseResult {
    /** 저장 화면에 전달할 웹 URL을 찾은 상태입니다. */
    data class Success(val url: String) : SharedUrlParseResult

    /** 공유 본문이 없거나 공백뿐인 상태입니다. */
    data object EmptyText : SharedUrlParseResult

    /** 공유 본문에서 지원하는 HTTP 또는 HTTPS URL을 찾지 못한 상태입니다. */
    data object NoSupportedUrl : SharedUrlParseResult

    /** 공유 본문에 URL이 두 개 이상 포함되어 저장 대상을 정할 수 없는 상태입니다. */
    data object MultipleUrls : SharedUrlParseResult
}

/** 문장 끝의 구두점과 짝이 없는 닫는 괄호를 URL 후보에서 제거합니다. */
private fun trimTrailingSentencePunctuation(candidate: String): String {
    var normalized = candidate.trimEnd(*TRAILING_SENTENCE_PUNCTUATION)
    normalized = trimUnmatchedClosingDelimiter(normalized, '(', ')')
    normalized = trimUnmatchedClosingDelimiter(normalized, '[', ']')
    normalized = trimUnmatchedClosingDelimiter(normalized, '{', '}')
    return normalized
}

/** URL 후보 끝의 짝이 맞지 않는 닫는 괄호만 제거합니다. */
private fun trimUnmatchedClosingDelimiter(
    candidate: String,
    opening: Char,
    closing: Char,
): String {
    var normalized = candidate
    while (
        normalized.endsWith(closing) &&
        normalized.count { character -> character == closing } >
        normalized.count { character -> character == opening }
    ) {
        normalized = normalized.dropLast(1)
    }
    return normalized
}

/** 공유 문장에서 URL 뒤에 붙을 수 있는 일반적인 구두점입니다. */
private val TRAILING_SENTENCE_PUNCTUATION = charArrayOf('.', ',', '!', '?', ';', ':', '。', '，')
