package com.linku.core.analytics

import com.linku.core.model.CategoryType

/**
 * GA4로 전송하는 이벤트 정의입니다.
 *
 * 이벤트명과 파라미터 키·값은 모두 snake_case이며, GA에서 이름이 바뀌면 완전히 다른 이벤트로
 * 집계되어 기존 데이터가 분리되므로 한 번 정한 뒤 **절대 변경하지 않습니다.** 파라미터 값은 문자열을
 * 직접 넘기지 않고 [SignUpMethod], [AnalyticsEmotionType] 같은 enum으로만 만들어 값이 흩어지지 않게 합니다.
 *
 * 화면 진입마다 전송하면 CTR이 왜곡되므로, 각 이벤트는 사용자의 행동이 완료된 시점에만 호출합니다.
 */
sealed class AnalyticsEvent(
    val name: String,
    val params: Map<String, String> = emptyMap(),
) {

    /** 회원가입 완료 */
    data class SignUp(val method: SignUpMethod) :
        AnalyticsEvent("sign_up", mapOf("method" to method.value))

    /** 링크 저장 완료. [category]는 AI가 분류한 카테고리입니다. */
    data class LinkSaved(val category: CategoryType) :
        AnalyticsEvent("link_saved", mapOf("category" to category.tagName))

    /** 저장된 링크의 AI 요약 버튼 클릭 */
    data class AiSummaryView(val summarySource: SummarySource) :
        AnalyticsEvent("ai_summary_view", mapOf("summary_source" to summarySource.value))

    /** 홈 화면에서 감정 버튼 선택 */
    data class EmotionSelected(val emotionType: AnalyticsEmotionType) :
        AnalyticsEvent("emotion_selected", mapOf("emotion_type" to emotionType.value))

    /** 홈 화면에서 상황 버튼 선택 */
    data class SituationSelected(val situationType: AnalyticsSituationType) :
        AnalyticsEvent("situation_selected", mapOf("situation_type" to situationType.value))

    /** 감정·상황 기반 추천 링크 목록이 화면에 표시됨 */
    data class RecommendationShown(
        val emotionType: AnalyticsEmotionType,
        val situationType: AnalyticsSituationType,
    ) : AnalyticsEvent(
        "recommendation_shown",
        mapOf(
            "emotion_type" to emotionType.value,
            "situation_type" to situationType.value,
        ),
    )

    /** 추천 영역에서 링크 클릭 */
    data class RecommendedLinkClick(
        val emotionType: AnalyticsEmotionType,
        val situationType: AnalyticsSituationType,
    ) : AnalyticsEvent(
        "recommended_link_click",
        mapOf(
            "emotion_type" to emotionType.value,
            "situation_type" to situationType.value,
        ),
    )

    /** 폴더 공유 기능으로 공유 폴더 생성 */
    data class FolderShared(val shareMethod: ShareMethod) :
        AnalyticsEvent("folder_shared", mapOf("share_method" to shareMethod.value))
}
