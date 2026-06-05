package com.linku

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.datastore.session.LoginSessionStore
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import com.linku.core.repository.RecentSearchRepository
import com.linku.data.preference.NotificationPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val recentRepository: RecentSearchRepository,
    val loginSessionStore: LoginSessionStore,
    private val notificationPreference: NotificationPreference,
    private val alarmRepository: AlarmRepository
) : AndroidViewModel(application) {

    private val connectivityManager =
        getApplication<Application>().getSystemService(ConnectivityManager::class.java)

    // 실시간 네트워크 감지
    val isConnected: StateFlow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(isNetworkAvailable())
            }

            override fun onLost(network: Network) {
                trySend(isNetworkAvailable())
            }

            override fun onCapabilitiesChanged(
                network: Network,
                caps: NetworkCapabilities
            ) {
                trySend(isNetworkAvailable())
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)
        trySend(isNetworkAvailable())

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = isNetworkAvailable()
    )

    // 현재 시점 네트워크 체크
    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }


    // 최근 검색 기록 전체 삭제
    // 앱 실행 시 실행하여 이전 계정 기록 삭제
    fun clearRecentQuery() {
        Log.d("MainViewModel", "clearRecentQuery")

        viewModelScope.launch {
            Log.d("MainViewModel", "clearRecentQuery launch")

            try{
                Log.d("MainViewModel", "clearRecentQuery try")

                recentRepository.clear()

            }catch (e: Exception){
                Log.d("MainViewModel", "clearRecentQuery catch: $e.message")
            }finally {
                Log.d("MainViewModel", "clearRecentQuery finally")
            }
        }
        Log.d("MainViewModel", "clearRecentQuery return")
    }

    // 시스템 알람 허용 여부에 따른 초기 푸시알람설정 초기화
    fun setNotificationEnabled(isGranted: Boolean) {
        if (!isGranted) return

        viewModelScope.launch {
            val token = notificationPreference.getFcmToken()

            if (token == null) {
                Log.d("FCM", "token 없음 → skip")
                return@launch
            }

            // 토큰 등록이 성공했으면 전체 푸시알림 활성화
            val registerResult = alarmRepository.registerFCMToken(token)

            if (registerResult.isSuccess) {
                alarmRepository.updateAlarmSetting(AlarmType.ALL)
                    .onFailure { e ->
                        Log.e("FCM", "알람 설정 실패: ${e.message}", e)
                    }
            } else {
                Log.e("FCM", "register 실패: ${registerResult.exceptionOrNull()?.message}")
            }
        }
    }
}