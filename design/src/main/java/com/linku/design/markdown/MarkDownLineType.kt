package com.linku.design.markdown

/**
 * 컴포넌트 내에서 지원되는 마크다운 라인 유형을 정의합니다.
 *
 * 이 열거형은 각 줄의 [prefix]를 기준으로 유형을 분류하여,
 * 불렛 포인트나 일반 본문 등 그에 맞는 렌더링 스타일을 결정하는 데 사용됩니다.
 *
 * @property prefix 해당 라인 유형을 식별하기 위해 줄 시작 부분에 사용되는 접두사 문자열입니다.
 */
internal enum class MarkdownLineType(val prefix: String) {
    BulletPoint(prefix = "* "),
    Body(prefix = "");

    companion object {
        fun from(line: String): MarkdownLineType =
            entries.firstOrNull { it.prefix.isNotEmpty() && line.startsWith(it.prefix) } ?: Body
    }
}