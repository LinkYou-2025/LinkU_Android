package com.linku.data.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkReceiver @Inject constructor(
    @param:ApplicationContext private val context: Context // Hilt ApplicationContext 주입
) : BroadcastReceiver() {

    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    override fun onReceive(context: Context, intent: Intent) {
        _isConnected.value = isNetworkAvailable()
    }

    // 앱 시작 시 등록
    fun register() {

        // 앱 시작 시 현재 상태 즉시 반영
        _isConnected.value = isNetworkAvailable()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isConnected.value = true // 네트워크 연결
            }

            override fun onLost(network: Network) {
                _isConnected.value = false // 네트워크 끊김
            }

            override fun onUnavailable() {
                _isConnected.value = false // 네트워크 사용 불가
            }
        }

        // 감지 등록
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val callback = networkCallback ?: return
        connectivityManager.registerNetworkCallback(request, callback)
    }

    // 앱 종료시 해제
    fun unregister() {
        networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
        networkCallback = null
    }

    // 현재 상태 직접 확인
    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}