package com.linku.core.analytics

import com.linku.core.model.CategoryType
import com.linku.core.model.EmotionType
import com.linku.core.model.SituationId
import com.linku.core.model.auth.LoginType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * GA4는 이벤트명·파라미터 값이 바뀌면 다른 이벤트로 집계해 기존 데이터가 분리됩니다.
 * 이 테스트가 실패한다면 이름을 바꾼 것이므로 의도한 변경인지 반드시 확인해야 합니다.
 */
class AnalyticsEventTest {

    @Test
    fun `event names and param keys follow the spec`() {
        assertEvent("sign_up", mapOf("method" to "email"), AnalyticsEvent.SignUp(SignUpMethod.EMAIL))
        assertEvent(
            "link_saved",
            mapOf("category" to "자기계발"),
            AnalyticsEvent.LinkSaved(CategoryType.SELF_IMPROVEMENT),
        )
        assertEvent(
            "ai_summary_view",
            mapOf("summary_source" to "saved_link"),
            AnalyticsEvent.AiSummaryView(SummarySource.SAVED_LINK),
        )
        assertEvent(
            "emotion_selected",
            mapOf("emotion_type" to "happy"),
            AnalyticsEvent.EmotionSelected(AnalyticsEmotionType.HAPPY),
        )
        assertEvent(
            "situation_selected",
            mapOf("situation_type" to "commute"),
            AnalyticsEvent.SituationSelected(AnalyticsSituationType.COMMUTE),
        )
        assertEvent(
            "recommendation_shown",
            mapOf("emotion_type" to "calm", "situation_type" to "working"),
            AnalyticsEvent.RecommendationShown(AnalyticsEmotionType.CALM, AnalyticsSituationType.WORKING),
        )
        assertEvent(
            "recommended_link_click",
            mapOf("emotion_type" to "sad", "situation_type" to "before_sleep"),
            AnalyticsEvent.RecommendedLinkClick(AnalyticsEmotionType.SAD, AnalyticsSituationType.BEFORE_SLEEP),
        )
        assertEvent(
            "folder_shared",
            mapOf("share_method" to "deeplink"),
            AnalyticsEvent.FolderShared(ShareMethod.DEEPLINK),
        )
    }

    @Test
    fun `sign up methods match the spec`() {
        assertEquals(
            listOf("email", "google", "naver", "kakao"),
            SignUpMethod.entries.map { it.value },
        )
    }

    @Test
    fun `login type maps to sign up method`() {
        assertEquals(SignUpMethod.EMAIL, SignUpMethod.from(LoginType.EMAIL))
        assertEquals(SignUpMethod.GOOGLE, SignUpMethod.from(LoginType.GOOGLE))
        assertEquals(SignUpMethod.KAKAO, SignUpMethod.from(LoginType.KAKAO))
        assertNull(SignUpMethod.from(LoginType.NONE))
    }

    @Test
    fun `all 16 categories match the spec`() {
        assertEquals(
            listOf(
                "어학", "뉴스", "공부법", "IT·개발", "자기계발", "취업·이직", "비즈니스 인사이트", "생산성·툴",
                "라이프스타일", "심리·자기이해", "에세이·칼럼", "트렌드", "디자인·예술", "영상·뮤직", "맛집·여행", "기타",
            ),
            CategoryType.entries.map {
                AnalyticsEventMapper.toParams(AnalyticsEvent.LinkSaved(it)).getValue("category")
            },
        )
    }

    @Test
    fun `emotions map to the six spec values`() {
        assertEquals(
            listOf("happy", "calm", "excited", "sad", "annoyed", "angry"),
            EmotionType.entries.map { it.toAnalyticsType().value },
        )
    }

    @Test
    fun `situations of every job share one value regardless of job`() {
        assertEquals("commute", SituationId.HIGH_SCHOOL_COMMUTE.toAnalyticsType().value)
        assertEquals("commute", SituationId.OFFICE_COMMUTE.toAnalyticsType().value)
        assertEquals("before_sleep", SituationId.UNIVERSITY_BEFORE_SLEEP.toAnalyticsType().value)
        assertEquals("studying", SituationId.HIGH_SCHOOL_STUDY.toAnalyticsType().value)
        assertEquals("working", SituationId.OFFICE_WORKING.toAnalyticsType().value)
        assertEquals("working", SituationId.CREATOR_WORKING.toAnalyticsType().value)
    }

    @Test
    fun `every situation id resolves from its server value`() {
        SituationId.entries.forEach { id ->
            assertEquals(id.toAnalyticsType(), situationAnalyticsTypeOf(id.value))
        }
        assertNull(situationAnalyticsTypeOf(-1L))
    }

    @Test
    fun `all analytics values are snake case`() {
        val snakeCase = Regex("^[a-z]+(_[a-z]+)*$")
        val values = SignUpMethod.entries.map { it.value } +
            SummarySource.entries.map { it.value } +
            ShareMethod.entries.map { it.value } +
            AnalyticsEmotionType.entries.map { it.value } +
            AnalyticsSituationType.entries.map { it.value }

        values.forEach { value -> assertTrue("$value is not snake_case", snakeCase.matches(value)) }
    }

    private fun assertEvent(name: String, params: Map<String, String>, event: AnalyticsEvent) {
        assertEquals(name, event.name)
        assertEquals(params, AnalyticsEventMapper.toParams(event))
    }
}
