package com.linku.core.analytics

/**
 * [AnalyticsEvent]를 GA4 이벤트 파라미터로 변환합니다.
 *
 * 이벤트를 추가하면 `when`이 sealed 클래스를 모두 다뤄야 하므로 변환 누락은 컴파일 오류로 드러납니다.
 * 파라미터 키는 [AnalyticsEvent]와 같은 이유로 변경하지 않습니다.
 */
object AnalyticsEventMapper {

    private const val METHOD = "method"
    private const val CATEGORY = "category"
    private const val SUMMARY_SOURCE = "summary_source"
    private const val EMOTION_TYPE = "emotion_type"
    private const val SITUATION_TYPE = "situation_type"
    private const val SHARE_METHOD = "share_method"

    // Firebase에는 파라미터를 키-값 쌍으로 넘겨야 하므로 이벤트별로 (키 to 값) Map으로 변환합니다.
    fun toParams(event: AnalyticsEvent): Map<String, String> = when (event) {
        is AnalyticsEvent.SignUp -> mapOf(METHOD to event.method.value)

        is AnalyticsEvent.LinkSaved -> mapOf(CATEGORY to event.category.tagName)

        is AnalyticsEvent.AiSummaryView -> mapOf(SUMMARY_SOURCE to event.summarySource.value)

        is AnalyticsEvent.EmotionSelected -> mapOf(EMOTION_TYPE to event.emotionType.value)

        is AnalyticsEvent.SituationSelected -> mapOf(SITUATION_TYPE to event.situationType.value)

        is AnalyticsEvent.RecommendationShown -> mapOf(
            EMOTION_TYPE to event.emotionType.value,
            SITUATION_TYPE to event.situationType.value,
        )

        is AnalyticsEvent.RecommendedLinkClick -> mapOf(
            EMOTION_TYPE to event.emotionType.value,
            SITUATION_TYPE to event.situationType.value,
        )

        is AnalyticsEvent.FolderShared -> mapOf(SHARE_METHOD to event.shareMethod.value)
    }
}
