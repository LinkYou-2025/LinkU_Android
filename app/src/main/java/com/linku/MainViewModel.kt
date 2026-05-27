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
import com.linku.core.repository.RecentSearchRepository
import com.linku.core.system.NotificationController
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
    private val notificationController: NotificationController
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

    // 알림 허용 여부 저장
    // 로그인 성공 후 시스템 권한 요청 결과를 로컬에 반영
    fun setNotificationEnabled(enabled: Boolean) {
        notificationController.setNotificationEnabled(enabled)
    }

}