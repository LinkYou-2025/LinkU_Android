package com.linku

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import com.linku.core.repository.RecentSearchRepository
import com.linku.core.repository.UserRepository
import com.linku.core.usecase.FirstPushAlarmAllowedUseCase
import com.linku.core.usecase.ReRegisterFcmTokenUseCase
import com.linku.core.util.logging.LinkuLog
import com.linku.core.util.logging.e
import com.linku.data.preference.AuthPreference
import com.linku.core.preference.NotificationPreference
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
    private val firstPushAlarmAllowedUseCase: FirstPushAlarmAllowedUseCase,
    private val reRegisterFcmTokenUseCase: ReRegisterFcmTokenUseCase,
    private val alarmRepository: AlarmRepository,
) : AndroidViewModel(application) {

    private val _sideEffect = Channel<SideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    // 닉네임 캐싱 먼저 -> ui 지직거림 방지
    private val _nickname = MutableStateFlow<String>("")
    val nickname: StateFlow<String> = _nickname.asStateFlow()

    // 인증 상태. ViewModel State로 관리하는 이유:
    // ViewModel은 구성 변경(회전)엔 살아남지만 프로세스 종료 시엔 함께 소멸된다.
    // 따라서 복원 시 항상 false부터 시작 → 실제 로그인/자동 로그인 성공 시에만 true가 되어,
    // 세션이 죽었는데 stale-true가 복원돼 인증 전 pending 알림을 소비하는 문제를 원천 차단.
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    // 로그인/자동 로그인 성공 시 true, 로그아웃 시 false로 갱신
    fun setAuthenticated(value: Boolean) {
        _isAuthenticated.value = value
        if (value) {
            reRegisterFcmToken()
            syncAlarmSetting()
        }
    }

    private fun syncAlarmSetting() {
        viewModelScope.launch {
            alarmRepository.getAlarmSetting()
        }
    }

    private fun reRegisterFcmToken() {
        viewModelScope.launch {
            // 최초알림팝업을 통한 fcm토큰등록이 진행되었다면 스킵
            if (!notificationPreference.isFcmTokenRegistered()) return@launch

            // fcm토큰 재등록
            reRegisterFcmTokenUseCase()
                //로그인 직후 백그라운드에서 조용히 실행되는 작업이므로
                //UI 개입을 최소화
                .fold(
                    onSuccess ={
                        Log.d("FCM", "FCM 토큰 재등록 성공")
                    },
                    onFailure = {
                        Log.w("FCM", "FCM 토큰 재등록 실패(다음 로그인 때 재시도)")
                    }
                )
        }
    }

    // 로그인 세션 반영함.
    val isLoggedIn: StateFlow<Boolean?> = authPreference.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // 첫 값이 로그되기 전 아직 모름 상태임. MainApp에서 previousLoggedIn과 비교해 로그아웃 전이면 감지함.
        )

    fun fetchNickname() {
        viewModelScope.launch {
            // 우선적으로 캐싱 먼저 가져옵니다.(닉네임 변경이 그렇게 많지 않을테니. ui 자연스러움을 위해서 입니다)
            val cachedNickname = authPreference.getCachedNickname()
            if (!cachedNickname.isNullOrBlank()) {
                _nickname.value = cachedNickname
            }

            // 서버에서 최신 닉네임 조회 후, 변경 감지 시 갱신 및 닉네임 캐시 업데이트
            userRepository.getNickname()
                .onSuccess { result ->
                    val fresh = result.nickname
                    if (fresh.isNotBlank() && fresh != cachedNickname) {
                        _nickname.value = fresh
                        authPreference.saveNickname(fresh)  // 캐시 갱신
                    }
                }
                .onFailure { e ->
                    LinkuLog.e("MainViewModel", "[닉네임 조회 실패] ${e.message}")
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
        return !authPreference.getRefreshToken().isNullOrBlank()
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

    fun handleNotification(type: AlarmType, targetId: Long, alarmId: Long?) {
        viewModelScope.launch {

            _sideEffect.send(
                SideEffect.NavigateByNotification(type, targetId, alarmId)
            )
        }
    }

    // 푸시 알림 클릭 시 앱이 죽어있던 경우, auth 완료 전까지 목적지를 임시 보관
    private var pendingNotification: PendingNotification? = null


    // auth 플로우 중 수신된 알림 목적지 저장 (login/auto-login 성공 후 consume)
    fun setPendingNotification(
        type: AlarmType,
        targetId: Long,
        alarmId: Long?
    ) {
        pendingNotification = PendingNotification(
            type = type,
            targetId = targetId,
            alarmId = alarmId
        )
    }

    // 저장된 알림 목적지를 꺼내고 초기화 (1회용).
    // alarmId가 있으면 이 시점(인증 완료 후)에 readAlarm 호출 → 인증 전 콜드스타트에서도 안전하게 처리됨.
    fun consumePendingNotification(): PendingNotification? {
        val pending = pendingNotification.also { pendingNotification = null }
        pending?.alarmId?.let { id ->
            viewModelScope.launch { alarmRepository.readAlarm(id) }
        }
        return pending
    }

    // "안하기" 선택 시 서버 알림 설정이 활성화 상태라면 비활성화 API 호출
    fun denyPushAlarm() {
        viewModelScope.launch {
            if (notificationPreference.isMasterNotificationEnabled()) {
                alarmRepository.updateAlarmSetting(AlarmType.ALL)
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

    data class NavigateByNotification(
        val type: AlarmType,
        val targetId: Long,
        val alarmId: Long?
    ) : SideEffect
}

data class PendingNotification(
    val type: AlarmType,
    val targetId: Long,
    val alarmId: Long?
)
