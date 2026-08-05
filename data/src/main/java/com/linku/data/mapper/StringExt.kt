package com.linku.data.mapper

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * ISO 8601 형식의 UTC 타임스탬프 문자열을 사람이 읽기 쉬운 상대 시간 문자열로 변환합니다.
 *
 * 현재 시각과의 차이를 분 단위로 계산하여 아래 기준으로 포맷합니다.
 * - 1분 미만 → "방금 전"
 * - 1분 이상 60분 미만 → "n분 전"
 * - 1시간 이상 24시간 미만 → "n시간 전"
 * - 24시간 이상 → "n일 전"
 *
 * 파싱에 실패하거나 포맷이 올바르지 않은 경우 "알 수 없음"을 반환합니다.
 * clock skew로 인해 diff가 음수일 경우 0으로 처리합니다.
 *
 * @receiver ISO 8601 형식의 UTC 타임스탬프 문자열 (예: "2024-01-01T00:00:00.000Z")
 * @return 상대 시간 문자열
 */
fun String.toRelativeTime(
    now: Instant = Instant.now(),
    zoneId: ZoneId = ZoneId.systemDefault()
): String {
    // 파싱
    val parsed = runCatching {
        LocalDateTime.parse(this)
            .atZone(zoneId)
            .toInstant()
    }.getOrElse {
        return "알 수 없음"
    }

    // 현재 시간과 차이 계산
    val diff = maxOf(
        0L,
        ChronoUnit.MINUTES.between(parsed, now)
    )

    return when {
        diff < 1       -> "방금 전"
        diff < 60      -> "${diff}분 전"
        diff < 60 * 24 -> "${diff / 60}시간 전"
        else           -> "${diff / (60 * 24)}일 전"
    }
}

/**
 * "yyyy-MM" 형식의 문자열을 큐레이션 제목 형식으로 변환합니다.
 *
 * @receiver "yyyy-MM" 형식의 문자열 (예: "2026-01")
 * @return 큐레이션 제목 문자열 (예: "2026\n월간 큐레이션 1월호"), 파싱 실패 시 원본 반환
 */
fun String.toCurationTitle(): String {
    val parts = this.split("-")

    if (parts.size != 2) return this

    val year = parts[0].toIntOrNull() ?: return this
    val month = parts[1].toIntOrNull() ?: return this

    return "$year\n월간 큐레이션 ${month}월호"
}

/**
 * "yyyy-MM" 형식의 문자열에서 연도만 추출합니다.
 *
 * @receiver "yyyy-MM" 형식의 문자열 (예: "2026-01")
 * @return 연도 문자열 (예: "2026"), 파싱 실패 시 원본 반환
 */
fun String.toCurationYear(): String {
    return this.split("-").firstOrNull()?.takeIf { it.toIntOrNull() != null } ?: this
}

/**
 * ISO 8601 형식의 문자열을 "yy.mm.dd" 형식의 날짜 문자열로 변환합니다.
 *
 * 문자열 파싱에 실패하거나 포맷이 올바르지 않은 경우 "알 수 없음"을 반환합니다.
 * 연도는 뒤의 두 자리만 표시하며, 월과 일은 두 자리 숫자로 패딩됩니다.
 *
 */
fun String.toDate(): String {
    return runCatching {
        val parsed = LocalDateTime.parse(this)
        val yy = parsed.year % 100
        val mm = parsed.monthValue
        val dd = parsed.dayOfMonth
        "%02d.%02d.%02d".format(yy, mm, dd)
    }.getOrElse { "알 수 없음" }
}