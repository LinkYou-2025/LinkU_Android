package com.linku.mypage.intent

// Intent
sealed interface NotificationIntent {
    data class ToggleAll(val enabled: Boolean) : NotificationIntent
    data class ToggleLink(val enabled: Boolean) : NotificationIntent
    data class ToggleFolder(val enabled: Boolean) : NotificationIntent
    data class ToggleCuration(val enabled: Boolean) : NotificationIntent
    data class ToggleNotice(val enabled: Boolean) : NotificationIntent
    data object RefreshSystemAlarm : NotificationIntent
}