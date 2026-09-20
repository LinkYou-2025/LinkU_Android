package com.linku.core.analytics

/** 사용자 행동 이벤트를 분석 플랫폼(Firebase Analytics/GA4)으로 전송합니다. */
interface AnalyticsLogger {
    fun log(event: AnalyticsEvent)
}
