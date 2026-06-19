package com.linku.core.model

data class Situation(
    val id: Long,
    val tagName: String
)

object SituationOptions {
    val linkDetailSituations: List<Situation> = listOf(
        Situation(1L, "통학 중"),
        Situation(2L, "공부 중"),
        Situation(3L, "휴식 중"),
        Situation(4L, "이동 중"),
        Situation(5L, "식사 중"),
        Situation(6L, "자기 전")
    )

    fun situationsFor(jobId: Long): List<Situation> = when (jobId) {
        1L -> listOf(
            Situation(1L, "통학 중"),
            Situation(2L, "공부 중"),
            Situation(3L, "식사 중"),
            Situation(4L, "시험 준비"),
            Situation(5L, "친구랑"),
            Situation(6L, "쇼핑 중"),
            Situation(7L, "휴식 중"),
            Situation(8L, "자기 전")
        )

        2L -> listOf(
            Situation(9L, "과제 중"),
            Situation(10L, "통학 중"),
            Situation(11L, "쇼핑 중"),
            Situation(12L, "알바 중"),
            Situation(13L, "트렌드 확인"),
            Situation(14L, "데이트 중"),
            Situation(15L, "휴식 중"),
            Situation(16L, "자기 전")
        )

        3L -> listOf(
            Situation(17L, "출퇴근"),
            Situation(18L, "트렌드 확인"),
            Situation(19L, "업무 중"),
            Situation(20L, "커리어 고민"),
            Situation(21L, "쇼핑 중"),
            Situation(22L, "데이트 중"),
            Situation(23L, "휴식 중"),
            Situation(24L, "자기 전")
        )

        4L -> listOf(
            Situation(25L, "출퇴근"),
            Situation(26L, "업무 준비 중"),
            Situation(27L, "데이트 중"),
            Situation(28L, "식사"),
            Situation(29L, "쇼핑 중"),
            Situation(30L, "트렌드 확인"),
            Situation(31L, "휴식 중"),
            Situation(32L, "자기 전")
        )

        5L -> listOf(
            Situation(33L, "작업 중"),
            Situation(34L, "쇼핑 중"),
            Situation(35L, "트렌드 확인"),
            Situation(36L, "데이트 중"),
            Situation(37L, "운동 중"),
            Situation(38L, "식사"),
            Situation(39L, "휴식 중"),
            Situation(40L, "자기 전")
        )

        6L -> listOf(
            Situation(41L, "자소서 작성"),
            Situation(42L, "면접 준비"),
            Situation(43L, "요리 중"),
            Situation(44L, "트렌드 확인"),
            Situation(45L, "쇼핑 중"),
            Situation(46L, "운동 중"),
            Situation(47L, "휴식 중"),
            Situation(48L, "자기 전")
        )

        else -> situationsFor(3L)
    }

    fun nameOf(id: Long?): String? {
        if (id == null) return null

        return (linkDetailSituations + (1L..6L).flatMap { situationsFor(it) })
            .distinctBy { it.id }
            .firstOrNull { it.id == id }
            ?.tagName
    }

    fun idOf(tagName: String, jobId: Long? = null): Long? {
        val options = if (jobId != null) {
            situationsFor(jobId)
        } else {
            linkDetailSituations
        }

        return options.firstOrNull { it.tagName == tagName }?.id
    }
}