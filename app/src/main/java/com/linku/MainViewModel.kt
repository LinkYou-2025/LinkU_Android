package com.linku

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.repository.UserRepository
import com.linku.core.usecase.FirstPushAlarmAllowedUseCase
import com.linku.data.preference.AuthPreference
import com.linku.data.preference.NotificationPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val notificationPreference: NotificationPreference,
    private val authPreference: AuthPreference,
    private val userRepository: UserRepository, // 닉네임 호출용
    private val firstPushAlarmAllowedUseCase: FirstPushAlarmAllowedUseCase
) : AndroidViewModel(application) {

    private val _sideEffect = Channel<SideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

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
        _nickname.value = ""
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

    // 푸시 알림 권한 요청 다이얼로그를 최초 1회만 노출
    // 이미 요청된 적 있으면 아무 동작도 하지 않음
    // 다이얼로그 표시 전에 요청 여부를 true로 선저장하여 중복 노출을 방지
    fun checkAndShowPushAlarmDialog() {
        viewModelScope.launch {
            if (!notificationPreference.isPushPermissionRequested()) {
                notificationPreference.setPushPermissionRequested(true)
                _sideEffect.send(SideEffect.ShowPushAlarmDialog)
            }
        }
    }

    fun allowPushAlarm() {
        viewModelScope.launch {
            firstPushAlarmAllowedUseCase()
                .fold(
                    onSuccess = { _sideEffect.send(SideEffect.ShowToast("푸시 알림이 설정되었어요.")) },
                    onFailure = { e ->
                        notificationPreference.setPushPermissionRequested(false)
                        _sideEffect.send(
                            SideEffect.ShowToast(
                                e.message ?: "푸시 알림 설정에 실패했어요. 알림 설정 창에서 다시 시도할 수 있어요."
                            )
                        )
                    }
                )
        }
    }

}

// 1회성 사이드 이펙트
sealed interface SideEffect {
    data object ShowPushAlarmDialog: SideEffect
    data class ShowToast(val message: String) : SideEffect
}
