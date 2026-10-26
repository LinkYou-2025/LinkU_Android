package com.linku.core.analytics

/**
 * 분석 이벤트를 기록하는 추상화입니다.
 *
 * feature 모듈은 이 인터페이스에만 의존하고, 실제 전송 구현(Firebase 등)은 data 모듈이 제공합니다.
 */
interface AnalyticsLogger {

    /**
     * 분석 이벤트를 기록합니다.
     *
     * @param event 기록할 분석 이벤트
     */
    fun log(event: AnalyticsEvent)
}
