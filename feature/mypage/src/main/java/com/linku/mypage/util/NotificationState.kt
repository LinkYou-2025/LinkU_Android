package com.linku.mypage.util

data class NotificationState(
    val notificationEnabled: Boolean,
    val aiCurationEnabled: Boolean,
    val linkActivityEnabled: Boolean,
    val sharedFolderEnabled: Boolean,
    val systemNoticeEnabled: Boolean
)
