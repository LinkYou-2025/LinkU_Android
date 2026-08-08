package com.linku.core.preference

import kotlinx.coroutines.flow.Flow

interface NotificationPreference {

    // ===== Notification =====

    val masterNotificationEnabled: Flow<Boolean>

    suspend fun isMasterNotificationEnabled(): Boolean

    suspend fun setMasterNotificationEnabled(enabled: Boolean)

    // ===== FCM Token =====

    suspend fun getFcmToken(): String?

    suspend fun setFcmToken(token: String)

    // ===== FCM Token Server Registration =====

    suspend fun isFcmTokenRegistered(): Boolean

    suspend fun setFcmTokenRegistered(registered: Boolean)

    suspend fun clearFcmToken()

    // ===== Dialog State =====

    suspend fun isSystemPermissionRequested(): Boolean

    suspend fun setSystemPermissionRequested(requested: Boolean)

    suspend fun isPushPermissionRequested(): Boolean

    suspend fun setPushPermissionRequested(requested: Boolean)
}
