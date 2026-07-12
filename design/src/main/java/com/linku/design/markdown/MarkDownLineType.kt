package com.linku.design.markdown

/**
 * 기본적인 Markdown 파싱을 위해 지원하는 라인 타입을 나타냅니다.
 *
 * 각 문자열의 접두사를 기준으로 라인의 유형을 판별하며,
 * UI에서 어떤 형태로 렌더링할지 결정하는 데 사용됩니다.
 *
 */
internal enum class MarkdownLineType {
    BulletPoint, // `* `로 시작하는 순서 없는 목록 항목
    OrderedList, // `1. `과 같이 숫자와 마침표로 시작하는 순서 있는 목록 항목
    Body; // 일반 본문
    companion object {

        /**
         * 문자열 한 줄의 접두사를 검사하여 Markdown 라인 타입을 판별합니다.
         *
         * 판별 규칙:
         * - `* `로 시작하면 [BulletPoint]
         * - 숫자 + `. `로 시작하면 [OrderedList]
         * - 그 외의 경우 [Body]
         *
         * 예시:
         * - `"* Kotlin"` → [BulletPoint]
         * - `"1. Android"` → [OrderedList]
         * - `"Jetpack Compose"` → [Body]
         */
        fun from(line: String): MarkdownLineType = when {
            line.startsWith("* ") -> BulletPoint
            line.firstOrNull()?.isDigit() == true && line.contains(". ") -> OrderedList
            else -> Body
        }
    }
}