package com.linku.data.network

import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    private val networkReceiver: NetworkReceiver
) {
    // 외부에서 네트워크 상태 구독
    val isConnected: StateFlow<Boolean> = networkReceiver.isConnected

    // 현재 순간 네트워크 상태 즉시 확인
    fun isConnectedNow(): Boolean = isConnected.value
}