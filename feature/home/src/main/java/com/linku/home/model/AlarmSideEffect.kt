package com.linku.home.model

sealed interface AlarmSideEffect {
    data class ShowToast(val message: String = ""): AlarmSideEffect
    data class NavigateToLinkDetail(val targetId: Long): AlarmSideEffect
    data class NavigateToSharedFolder(val targetId: Long): AlarmSideEffect
    data class NavigateToCuration(val targetId: Long): AlarmSideEffect
    data class NavigateToNotice(val targetId: Long): AlarmSideEffect // 공지 알람의 경우에는 alarmId와 targetId가 같음
}