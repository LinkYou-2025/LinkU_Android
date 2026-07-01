package com.linku.core.model

data class Situation(
    val id: SituationId,
    val tagName: String
)

enum class SituationId(val value: Long) {
    HIGH_SCHOOL_COMMUTE(1L),
    HIGH_SCHOOL_STUDY(2L),
    HIGH_SCHOOL_MEAL(3L),
    HIGH_SCHOOL_EXAM_PREP(4L),
    HIGH_SCHOOL_WITH_FRIENDS(5L),
    HIGH_SCHOOL_SHOPPING(6L),
    HIGH_SCHOOL_REST(7L),
    HIGH_SCHOOL_BEFORE_SLEEP(8L),

    UNIVERSITY_ASSIGNMENT(9L),
    UNIVERSITY_COMMUTE(10L),
    UNIVERSITY_SHOPPING(11L),
    UNIVERSITY_PART_TIME_JOB(12L),
    UNIVERSITY_TREND_CHECK(13L),
    UNIVERSITY_DATE(14L),
    UNIVERSITY_REST(15L),
    UNIVERSITY_BEFORE_SLEEP(16L),

    OFFICE_COMMUTE(17L),
    OFFICE_TREND_CHECK(18L),
    OFFICE_WORKING(19L),
    OFFICE_CAREER_WORRY(20L),
    OFFICE_SHOPPING(21L),
    OFFICE_DATE(22L),
    OFFICE_REST(23L),
    OFFICE_BEFORE_SLEEP(24L),

    SELF_EMPLOYED_COMMUTE(25L),
    SELF_EMPLOYED_WORK_PREP(26L),
    SELF_EMPLOYED_DATE(27L),
    SELF_EMPLOYED_MEAL(28L),
    SELF_EMPLOYED_SHOPPING(29L),
    SELF_EMPLOYED_TREND_CHECK(30L),
    SELF_EMPLOYED_REST(31L),
    SELF_EMPLOYED_BEFORE_SLEEP(32L),

    CREATOR_WORKING(33L),
    CREATOR_SHOPPING(34L),
    CREATOR_TREND_CHECK(35L),
    CREATOR_DATE(36L),
    CREATOR_EXERCISE(37L),
    CREATOR_MEAL(38L),
    CREATOR_REST(39L),
    CREATOR_BEFORE_SLEEP(40L),

    JOB_SEEKER_COVER_LETTER(41L),
    JOB_SEEKER_INTERVIEW_PREP(42L),
    JOB_SEEKER_COOKING(43L),
    JOB_SEEKER_TREND_CHECK(44L),
    JOB_SEEKER_SHOPPING(45L),
    JOB_SEEKER_EXERCISE(46L),
    JOB_SEEKER_REST(47L),
    JOB_SEEKER_BEFORE_SLEEP(48L)
}

enum class JobType(
    val id: Long,
    val situations: List<Situation>
) {
    HIGH_SCHOOL_STUDENT(
        id = 1L,
        situations = listOf(
            Situation(SituationId.HIGH_SCHOOL_COMMUTE, "통학 중"),
            Situation(SituationId.HIGH_SCHOOL_STUDY, "공부 중"),
            Situation(SituationId.HIGH_SCHOOL_MEAL, "식사 중"),
            Situation(SituationId.HIGH_SCHOOL_EXAM_PREP, "시험 준비"),
            Situation(SituationId.HIGH_SCHOOL_WITH_FRIENDS, "친구랑"),
            Situation(SituationId.HIGH_SCHOOL_SHOPPING, "쇼핑 중"),
            Situation(SituationId.HIGH_SCHOOL_REST, "휴식 중"),
            Situation(SituationId.HIGH_SCHOOL_BEFORE_SLEEP, "자기 전")
        )
    ),

    UNIVERSITY_STUDENT(
        id = 2L,
        situations = listOf(
            Situation(SituationId.UNIVERSITY_ASSIGNMENT, "과제 중"),
            Situation(SituationId.UNIVERSITY_COMMUTE, "통학 중"),
            Situation(SituationId.UNIVERSITY_SHOPPING, "쇼핑 중"),
            Situation(SituationId.UNIVERSITY_PART_TIME_JOB, "알바 중"),
            Situation(SituationId.UNIVERSITY_TREND_CHECK, "트렌드 확인"),
            Situation(SituationId.UNIVERSITY_DATE, "데이트 중"),
            Situation(SituationId.UNIVERSITY_REST, "휴식 중"),
            Situation(SituationId.UNIVERSITY_BEFORE_SLEEP, "자기 전")
        )
    ),

    OFFICE_WORKER(
        id = 3L,
        situations = listOf(
            Situation(SituationId.OFFICE_COMMUTE, "출퇴근"),
            Situation(SituationId.OFFICE_TREND_CHECK, "트렌드 확인"),
            Situation(SituationId.OFFICE_WORKING, "업무 중"),
            Situation(SituationId.OFFICE_CAREER_WORRY, "커리어 고민"),
            Situation(SituationId.OFFICE_SHOPPING, "쇼핑 중"),
            Situation(SituationId.OFFICE_DATE, "데이트 중"),
            Situation(SituationId.OFFICE_REST, "휴식 중"),
            Situation(SituationId.OFFICE_BEFORE_SLEEP, "자기 전")
        )
    ),

    SELF_EMPLOYED(
        id = 4L,
        situations = listOf(
            Situation(SituationId.SELF_EMPLOYED_COMMUTE, "출퇴근"),
            Situation(SituationId.SELF_EMPLOYED_WORK_PREP, "업무 준비 중"),
            Situation(SituationId.SELF_EMPLOYED_DATE, "데이트 중"),
            Situation(SituationId.SELF_EMPLOYED_MEAL, "식사"),
            Situation(SituationId.SELF_EMPLOYED_SHOPPING, "쇼핑 중"),
            Situation(SituationId.SELF_EMPLOYED_TREND_CHECK, "트렌드 확인"),
            Situation(SituationId.SELF_EMPLOYED_REST, "휴식 중"),
            Situation(SituationId.SELF_EMPLOYED_BEFORE_SLEEP, "자기 전")
        )
    ),

    CREATOR(
        id = 5L,
        situations = listOf(
            Situation(SituationId.CREATOR_WORKING, "작업 중"),
            Situation(SituationId.CREATOR_SHOPPING, "쇼핑 중"),
            Situation(SituationId.CREATOR_TREND_CHECK, "트렌드 확인"),
            Situation(SituationId.CREATOR_DATE, "데이트 중"),
            Situation(SituationId.CREATOR_EXERCISE, "운동 중"),
            Situation(SituationId.CREATOR_MEAL, "식사"),
            Situation(SituationId.CREATOR_REST, "휴식 중"),
            Situation(SituationId.CREATOR_BEFORE_SLEEP, "자기 전")
        )
    ),

    JOB_SEEKER(
        id = 6L,
        situations = listOf(
            Situation(SituationId.JOB_SEEKER_COVER_LETTER, "자소서 작성"),
            Situation(SituationId.JOB_SEEKER_INTERVIEW_PREP, "면접 준비"),
            Situation(SituationId.JOB_SEEKER_COOKING, "요리 중"),
            Situation(SituationId.JOB_SEEKER_TREND_CHECK, "트렌드 확인"),
            Situation(SituationId.JOB_SEEKER_SHOPPING, "쇼핑 중"),
            Situation(SituationId.JOB_SEEKER_EXERCISE, "운동 중"),
            Situation(SituationId.JOB_SEEKER_REST, "휴식 중"),
            Situation(SituationId.JOB_SEEKER_BEFORE_SLEEP, "자기 전")
        )
    );

    companion object {
        private val DEFAULT = OFFICE_WORKER

        fun fromId(id: Long?): JobType {
            return entries.firstOrNull { it.id == id } ?: DEFAULT
        }
    }
}

object SituationOptions {
    val allSituations: List<Situation>
        get() = JobType.entries.flatMap { it.situations }

    fun situationsFor(jobId: Long?): List<Situation> {
        return JobType.fromId(jobId).situations
    }

    // 선택한 상황을 서버에 보낼 때 Long id가 필요하다면 valueOf 사용
//    fun valueOf(tagName: String, jobId: Long?): Long? {
//        return JobType.fromId(jobId)
//            .situations
//            .firstOrNull { it.tagName == tagName }
//            ?.id
//            ?.value
//    }

    // 서버에서 받은 situationId: Long을 화면에 이름으로 보여줘야 한다면 nameOf 사용
//    fun nameOf(id: Long?): String? {
//        return allSituations
//            .firstOrNull { it.id.value == id }
//            ?.tagName
//    }
}