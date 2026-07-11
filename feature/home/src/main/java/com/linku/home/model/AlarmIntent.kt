package com.linku.home.model

import com.linku.core.model.alarm.AlarmSummary

/**
 * 알람 관리 화면에서 발생하는 사용자의 의도(Intent) 또는 액션을 정의하는 인터페이스입니다.
 *
 * 주로 MVI(Model-View-Intent) 아키텍처 패턴에서 UI 이벤트를 캡슐화하여
 * ViewModel로 전달하고 비즈니스 로직을 처리하는 데 사용됩니다.
 */
sealed interface AlarmIntent {
    data class ClickAlarm(val alarm: AlarmSummary) : AlarmIntent
}