package com.linku

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.repository.RecentSearchRepository
import com.linku.core.repository.UserRepository
import com.linku.core.system.NotificationController
import com.linku.data.preference.AuthPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val recentRepository: RecentSearchRepository,
    private val notificationController: NotificationController,
    private val authPreference: AuthPreference,
    private val userRepository: UserRepository // 닉네임 호출용
) : AndroidViewModel(application) {

    // 닉네임 캐싱 먼저 -> ui 지직거림 방지
    private val _nickname = MutableStateFlow<String>("")
    val nickname: StateFlow<String> = _nickname.asStateFlow()

    fun fetchNickname() {
        viewModelScope.launch {
            // 우선적으로 캐싱 먼저 가져옵니다.(닉네임 변경이 그렇게 많지 않을테니. ui 자연스러움을 위해서 입니다)
            val cachedNickname = authPreference.getCachedNickname()
            if (!cachedNickname.isNullOrBlank()) {
                _nickname.value = cachedNickname
            }

            // 서버에서 최신 닉네임 조회 후, 변경 감지 시 갱신 및 닉네임 캐시 업데이트
            val fresh = userRepository.getNickname()
            if (!fresh.isNullOrBlank() && fresh != cachedNickname) {
                _nickname.value = fresh
                authPreference.saveNickname(fresh)  // 캐시 갱신
            }
        }
    }

    // 메인 뷰모델은 액티비티에 속해있어서 앱 종료까지 살아있어서, 로그아웃시에도 닉네임 정보 살아 있을 수 있음. 제거용 구현
    fun clearNickname() {
        _nickname.value
    }

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

    // 디바이스 정보 초기화 비즈니스 로직 내포
    fun initDeviceInfo(deviceType: String) {
        viewModelScope.launch {
            val uniqueDeviceId = "android-${UUID.randomUUID()}"
            authPreference.initDeviceInfo(uniqueDeviceId, deviceType)
        }
    }

    // 리프레시 토큰 유효성 검사 (컴포저블에서 동기/비동기 흐름 제어를 위해 suspend 함수로 제공)
    suspend fun hasValidRefreshToken(): Boolean {
        val token = authPreference.getRefreshToken()
        return !token.isNullOrBlank()
    }

    // 알림 허용 여부 저장
    // 로그인 성공 후 시스템 권한 요청 결과를 로컬에 반영
    fun setNotificationEnabled(enabled: Boolean) {
        notificationController.setNotificationEnabled(enabled)
    }

}