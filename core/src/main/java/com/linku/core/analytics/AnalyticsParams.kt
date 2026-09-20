package com.linku.core.analytics

import com.linku.core.model.EmotionType
import com.linku.core.model.SituationId
import com.linku.core.model.auth.LoginType

// 아래 enum의 value는 GA4에 그대로 기록되므로 변경하지 않습니다. (AnalyticsEvent 문서 참고)

enum class SignUpMethod(val value: String) {
    EMAIL("email"),
    GOOGLE("google"),
    NAVER("naver"),
    KAKAO("kakao");

    companion object {
        /** 가입 수단을 알 수 없는 [LoginType.NONE]은 `null`입니다. */
        fun from(loginType: LoginType): SignUpMethod? = when (loginType) {
            LoginType.EMAIL -> EMAIL
            LoginType.GOOGLE -> GOOGLE
            LoginType.KAKAO -> KAKAO
            LoginType.NONE -> null
        }
    }
}

enum class SummarySource(val value: String) {
    SAVED_LINK("saved_link"),
}

enum class ShareMethod(val value: String) {
    DEEPLINK("deeplink"),
}

enum class AnalyticsEmotionType(val value: String) {
    HAPPY("happy"),
    CALM("calm"),
    EXCITED("excited"),
    SAD("sad"),
    ANNOYED("annoyed"),
    ANGRY("angry"),
}

/** 직업별로 나뉜 상황 ID를 직업과 무관한 하나의 분석 값으로 묶습니다. */
enum class AnalyticsSituationType(val value: String) {
    COMMUTE("commute"),
    STUDYING("studying"),
    MEAL("meal"),
    EXAM_PREP("exam_prep"),
    WITH_FRIENDS("with_friends"),
    SHOPPING("shopping"),
    REST("rest"),
    BEFORE_SLEEP("before_sleep"),
    ASSIGNMENT("assignment"),
    PART_TIME_JOB("part_time_job"),
    TREND_CHECK("trend_check"),
    DATE("date"),
    WORKING("working"),
    CAREER_WORRY("career_worry"),
    WORK_PREP("work_prep"),
    EXERCISE("exercise"),
    COOKING("cooking"),
    COVER_LETTER("cover_letter"),
    INTERVIEW_PREP("interview_prep"),
}

fun EmotionType.toAnalyticsType(): AnalyticsEmotionType = when (this) {
    EmotionType.JOY -> AnalyticsEmotionType.HAPPY
    EmotionType.CALM -> AnalyticsEmotionType.CALM
    EmotionType.EXCITE -> AnalyticsEmotionType.EXCITED
    EmotionType.SAD -> AnalyticsEmotionType.SAD
    EmotionType.IRRITATION -> AnalyticsEmotionType.ANNOYED
    EmotionType.ANGER -> AnalyticsEmotionType.ANGRY
}

fun SituationId.toAnalyticsType(): AnalyticsSituationType = when (this) {
    SituationId.HIGH_SCHOOL_COMMUTE,
    SituationId.UNIVERSITY_COMMUTE,
    SituationId.OFFICE_COMMUTE,
    SituationId.SELF_EMPLOYED_COMMUTE -> AnalyticsSituationType.COMMUTE

    SituationId.HIGH_SCHOOL_STUDY -> AnalyticsSituationType.STUDYING

    SituationId.HIGH_SCHOOL_MEAL,
    SituationId.SELF_EMPLOYED_MEAL,
    SituationId.CREATOR_MEAL -> AnalyticsSituationType.MEAL

    SituationId.HIGH_SCHOOL_EXAM_PREP -> AnalyticsSituationType.EXAM_PREP
    SituationId.HIGH_SCHOOL_WITH_FRIENDS -> AnalyticsSituationType.WITH_FRIENDS

    SituationId.HIGH_SCHOOL_SHOPPING,
    SituationId.UNIVERSITY_SHOPPING,
    SituationId.OFFICE_SHOPPING,
    SituationId.SELF_EMPLOYED_SHOPPING,
    SituationId.CREATOR_SHOPPING,
    SituationId.JOB_SEEKER_SHOPPING -> AnalyticsSituationType.SHOPPING

    SituationId.HIGH_SCHOOL_REST,
    SituationId.UNIVERSITY_REST,
    SituationId.OFFICE_REST,
    SituationId.SELF_EMPLOYED_REST,
    SituationId.CREATOR_REST,
    SituationId.JOB_SEEKER_REST -> AnalyticsSituationType.REST

    SituationId.HIGH_SCHOOL_BEFORE_SLEEP,
    SituationId.UNIVERSITY_BEFORE_SLEEP,
    SituationId.OFFICE_BEFORE_SLEEP,
    SituationId.SELF_EMPLOYED_BEFORE_SLEEP,
    SituationId.CREATOR_BEFORE_SLEEP,
    SituationId.JOB_SEEKER_BEFORE_SLEEP -> AnalyticsSituationType.BEFORE_SLEEP

    SituationId.UNIVERSITY_ASSIGNMENT -> AnalyticsSituationType.ASSIGNMENT
    SituationId.UNIVERSITY_PART_TIME_JOB -> AnalyticsSituationType.PART_TIME_JOB

    SituationId.UNIVERSITY_TREND_CHECK,
    SituationId.OFFICE_TREND_CHECK,
    SituationId.SELF_EMPLOYED_TREND_CHECK,
    SituationId.CREATOR_TREND_CHECK,
    SituationId.JOB_SEEKER_TREND_CHECK -> AnalyticsSituationType.TREND_CHECK

    SituationId.UNIVERSITY_DATE,
    SituationId.OFFICE_DATE,
    SituationId.SELF_EMPLOYED_DATE,
    SituationId.CREATOR_DATE -> AnalyticsSituationType.DATE

    SituationId.OFFICE_WORKING,
    SituationId.CREATOR_WORKING -> AnalyticsSituationType.WORKING

    SituationId.OFFICE_CAREER_WORRY -> AnalyticsSituationType.CAREER_WORRY
    SituationId.SELF_EMPLOYED_WORK_PREP -> AnalyticsSituationType.WORK_PREP

    SituationId.CREATOR_EXERCISE,
    SituationId.JOB_SEEKER_EXERCISE -> AnalyticsSituationType.EXERCISE

    SituationId.JOB_SEEKER_COOKING -> AnalyticsSituationType.COOKING
    SituationId.JOB_SEEKER_COVER_LETTER -> AnalyticsSituationType.COVER_LETTER
    SituationId.JOB_SEEKER_INTERVIEW_PREP -> AnalyticsSituationType.INTERVIEW_PREP
}

/** 서버에서 내려오는 Long 상황 ID를 분석 값으로 변환합니다. 알 수 없는 ID는 `null`입니다. */
fun situationAnalyticsTypeOf(situationValue: Long): AnalyticsSituationType? =
    SituationId.entries.firstOrNull { it.value == situationValue }?.toAnalyticsType()
